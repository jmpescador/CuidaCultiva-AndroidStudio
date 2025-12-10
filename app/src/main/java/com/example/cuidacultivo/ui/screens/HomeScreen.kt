@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cuidacultivo.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.Surface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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
import java.nio.ByteBuffer
import java.util.concurrent.Executors

// ------------------------------------------------------------
// Captura imagen
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
                val buffer: ByteBuffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                image.close()

                onImageCaptured(
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                )
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
    val blur by animateDpAsState(if (isFocused) 0.dp else 6.dp)
    val overlayAlpha by animateFloatAsState(if (isFocused) 0.3f else 0.5f)

    val imageCapture = remember {
        ImageCapture.Builder()
            .setTargetRotation(Surface.ROTATION_0)
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
                enableFlash = false,
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
                Image(
                    painter = painterResource(id = R.drawable.enfoque),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.FillBounds
                )
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
