@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cuidacultivo.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.Surface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.cuidacultivo.R
import com.example.cuidacultivo.data.saveHistorial
import com.example.cuidacultivo.tflite.runModel
import com.example.cuidacultivo.ui.components.*
import com.example.cuidacultivo.utils.uriToBitmap
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.concurrent.Executors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp


// ------------------------------------------------------------
// Captura imagen
// ------------------------------------------------------------
// ------------------------------------------------------------
// Captura imagen OPTIMIZADA
// ------------------------------------------------------------
fun captureImage(
    imageCapture: ImageCapture,
    executor: java.util.concurrent.Executor,
    context: Context,
    onImageCaptured: (Bitmap) -> Unit
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    // 1. Convertir a Bitmap de forma eficiente
                    val bitmap = image.toBitmap() // Función nativa de CameraX

                    // 2. Obtener rotación (importante en Samsung/Xiaomi)
                    val rotationDegrees = image.imageInfo.rotationDegrees

                    // 3. ¡EL SECRETO! Redimensionar AHORA a lo que pide el modelo (180x180)
                    // Esto reduce el tamaño de memoria de 40MB a 0.1MB
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 180, 180, true)

                    // 4. Rotar la imagen pequeña (muy rápido)
                    val matrix = android.graphics.Matrix().apply {
                        postRotate(rotationDegrees.toFloat())
                    }
                    val finalBitmap = Bitmap.createBitmap(
                        scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true
                    )

                    // Limpiar memoria del grande
                    bitmap.recycle()

                    // Retornar la imagen lista
                    onImageCaptured(finalBitmap)

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    // SIEMPRE cerrar la imagen o la cámara se bloqueará
                    image.close()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        }
    )
}

// ------------------------------------------------------------
// HOME SCREEN
// ------------------------------------------------------------
@Composable
fun HomeScreen(navController: NavHostController) {

    var blockHeight by remember { mutableStateOf(0.dp) }
    var flashEnabled by remember { mutableStateOf(false) }
    var showFocusMessage by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var switchState by remember { mutableStateOf(false) }
    var tienePermiso by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    var pulseEffect by remember { mutableStateOf(false) }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            tienePermiso = it
        }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Animaciones
    val scale by animateFloatAsState(if (isFocused) 4f else 1f)
    val offsetY by animateDpAsState(if (isFocused) (-50).dp else 0.dp)
    val pulseScale by animateFloatAsState(if (pulseEffect) 1.15f else 1f)
    val blur by animateDpAsState(
        targetValue = if (isFocused) 0.dp else 12.dp,
        animationSpec = tween(600, easing = LinearOutSlowInEasing)
    )
    val overlayAlpha by animateFloatAsState(if (isFocused) 0.3f else 0.5f)

    val imageCapture = remember {
        ImageCapture.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            // NUEVO: Limitar resolución de captura (640x480 es suficiente y muy rápido)
            .setTargetResolution(android.util.Size(640, 480))
            // NUEVO: Priorizar velocidad sobre calidad
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    fun convertToARGB8888(bitmap: Bitmap): Bitmap =
        if (bitmap.isMutable) bitmap else bitmap.copy(Bitmap.Config.ARGB_8888, true)

    // ------------------------------------------------------------
    // PROCESAR IMAGEN + GUARDAR HISTORIAL (FONDO)
    // ------------------------------------------------------------
    fun processBitmap(bitmap: Bitmap, metodo: String) {
        scope.launch(Dispatchers.IO) {

            val safeBitmap = convertToARGB8888(bitmap)
            val jsonString = runModel(context, safeBitmap)

            val json = JSONObject(jsonString)
            val plaga = json.optString("nombre", "Desconocida")
            val porcentaje = json.optDouble("probabilidad", 0.0)

            // ✅ GUARDA HISTORIAL EN SEGUNDO PLANO
            saveHistorial(
                context = context,
                plaga = plaga,
                metodo = metodo,
                porcentaje = porcentaje,
                bitmap = safeBitmap
            )

            withContext(Dispatchers.Main) {
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("plagaInfo", jsonString)

                navController.navigate("result")
            }
        }
    }
    // ------------------------------------------------------------
    // GALERÍA
    // ------------------------------------------------------------
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri ?: return@rememberLauncherForActivityResult
            val bmp = uriToBitmap(context, uri) ?: return@rememberLauncherForActivityResult
            processBitmap(bmp, "galería")
        }
    // ------------------------------------------------------------
    // CÁMARA
    // ------------------------------------------------------------
    fun takePhotoAndPredict() {

        if (!isFocused) {
            pulseEffect = true
            scope.launch {
                delay(250)
                pulseEffect = false
            }
            return
        }
        captureImage(
            imageCapture = imageCapture,
            executor = Executors.newSingleThreadExecutor(),
            context = context
        ) { bitmap ->
            processBitmap(bitmap, "cámara")
        }
    }
    // ------------------------------------------------------------
    // UI (TU DISEÑO SE MANTIENE)
    // ------------------------------------------------------------
    Box(Modifier.fillMaxSize()) {

        if (tienePermiso) {
            CameraPreview(
                enableFlash = flashEnabled,
                imageCapture = imageCapture,
                modifier = Modifier.fillMaxSize().blur(blur)
            )
        }

        GradientBlock(
            switchState = switchState,
            height = 180,
            reverse = false,
            modifier = Modifier.align(Alignment.TopCenter).zIndex(2f)
        )

        Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = overlayAlpha)).zIndex(2f)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopBarMenu(
                    navController = navController,
                    switchState = switchState,
                    onSwitchChange = { switchState = it }
                )
            },
            bottomBar = {
                ButtonSearchSection(
                    onGalleryClick = { galleryLauncher.launch("image/*") },
                    onCameraClick = { takePhotoAndPredict() },
                    onSearchClick = { navController.navigate("buscarPlaga") }
                )
            },
            modifier = Modifier.zIndex(3f)
        ) { padding ->

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 25.dp, vertical = 90.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(!isFocused) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("¡Hola!", color = Color.White, fontSize = 22.sp)
                        Text(
                            "Asegúrate de que la imagen sea clara.",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .zIndex(4f)
        ) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .offset(y = offsetY)
                    .size(80.dp, 120.dp)
                    .scale(scale * pulseScale)
                    .pointerInput(Unit) {
                        detectTapGestures { isFocused = !isFocused }
                    }
            ) {
                // ESQUINA SUPERIOR IZQUIERDA
                Image(
                    painter = painterResource(R.drawable.esquina_superior_izquierda),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.TopStart)
                )

                // ESQUINA SUPERIOR DERECHA
                Image(
                    painter = painterResource(R.drawable.esquinas_superior_derecha),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.TopEnd)
                )

                // ESQUINA INFERIOR IZQUIERDA
                Image(
                    painter = painterResource(R.drawable.esquina_inferior_izquierda),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.BottomStart)
                )

                // ESQUINA INFERIOR DERECHA
                Image(
                    painter = painterResource(R.drawable.esquina_inferior_derecha),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.BottomEnd)
                )
            }

        }

        AnimatedVisibility(
            visible = isFocused,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 110.dp)
                .zIndex(5f)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .height(IntrinsicSize.Min),   // ← IGUALA ALTURA DE AMBOS BLOQUES
                verticalAlignment = Alignment.CenterVertically
            ) {

                // --- BLOQUE 1 ---
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .fillMaxHeight(), // ← SE AJUSTA A LA ALTURA DEL BLOQUE MÁS ALTO
                    color = Color.Black.copy(alpha = 0.40f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(
                                if (flashEnabled) R.drawable.flash_on else R.drawable.flash_off
                            ),
                            contentDescription = "Flash",
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { flashEnabled = !flashEnabled }
                        )
                        Spacer(Modifier.width(16.dp))
                        Icon(
                            painter = painterResource(R.drawable.enfoque2),
                            contentDescription = "Cerrar enfoque",
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { isFocused = false }
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                // --- BLOQUE 2 ---
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .fillMaxHeight(), // ← SE AJUSTA IGUAL QUE BLOQUE 1
                    color = Color.Black.copy(alpha = 0.45f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    AnimatedVisibility(showFocusMessage) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                "Enfoca bien y busca buena luz para ver mejor los síntomas.",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(Modifier.width(8.dp))

                            IconButton(
                                onClick = { showFocusMessage = false },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.cerrar),
                                    contentDescription = "Cerrar",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        GradientBlock(
            switchState = switchState,
            height = 180,
            reverse = true,
            modifier = Modifier.align(Alignment.BottomCenter).zIndex(2f)
        )
    }
}

