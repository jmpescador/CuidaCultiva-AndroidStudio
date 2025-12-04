@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cuidacultivo.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.Surface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.cuidacultivo.ui.components.*
import com.example.cuidacultivo.tflite.runModel
import com.example.cuidacultivo.utils.uriToBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Composable
fun HomeScreen(navController: NavHostController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- PERMISO DE CÁMARA ---
    var tienePermiso by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> tienePermiso = granted }

    LaunchedEffect(Unit) { permissionLauncher.launch(Manifest.permission.CAMERA) }

    // --- ESTADOS ---
    var switchState by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    var flashEnabled by remember { mutableStateOf(false) }
    var pulseEffect by remember { mutableStateOf(false) }

    // --- ImageCapture ---
    val imageCapture = remember {
        ImageCapture.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            .build()
    }

    // --- Convertir a ARGB8888 ---
    fun convertToARGB8888(bitmap: Bitmap): Bitmap {
        return try {
            if (!bitmap.isMutable) bitmap.copy(Bitmap.Config.ARGB_8888, true) else bitmap
        } catch (e: Exception) {
            val converted = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(converted)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            converted
        }
    }

    // --- GALERÍA → RESULTSCREEN ---
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = uriToBitmap(context, it)
            bitmap?.let { bmp ->
                val safeBitmap = convertToARGB8888(bmp)
                val jsonString = runModel(context, safeBitmap)

                Handler(Looper.getMainLooper()).post {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "plagaInfo",
                        jsonString
                    )
                    navController.navigate("result")
                }
            }
        }
    }


    // --- ANIMACIONES ---
    val scale by animateFloatAsState(if (isFocused) 4f else 1f, tween(600))
    val innerBlur by animateDpAsState(if (isFocused) 0.dp else 6.dp, tween(600))
    val overlayAlpha by animateFloatAsState(if (isFocused) 0.3f else 0.5f, tween(500))
    val verticalOffset by animateDpAsState(if (isFocused) (-50).dp else 0.dp, tween(600))
    val pulseScale by animateFloatAsState(if (pulseEffect) 1.15f else 1f, tween(250))

    // --- FUNCIÓN TOMAR FOTO Y PREDECIR ---
    fun takePhotoAndPredict() {
        if (!isFocused) {
            pulseEffect = true
            scope.launch {
                delay(300)
                pulseEffect = false
            }
            return
        }

        val executor = Executors.newSingleThreadExecutor()

        captureImage(
            imageCapture = imageCapture,
            executor = executor,
            onImageCaptured = { bitmap ->
                val safeBitmap = convertToARGB8888(bitmap)
                val jsonString = runModel(context, safeBitmap)

                Handler(Looper.getMainLooper()).post {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "plagaInfo",
                        jsonString
                    )
                    navController.navigate("result")
                }
            },
            context = context
        )
    }


    // --- UI ---
    Box(modifier = Modifier.fillMaxSize()) {

        GradientBlock(
            switchState = switchState,
            height = 180,
            reverse = false,
            modifier = Modifier.align(Alignment.TopCenter).zIndex(2f)
        )

        if (tienePermiso) {
            CameraPreview(
                enableFlash = flashEnabled,
                imageCapture = imageCapture,
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(24.dp))
                    .blur(innerBlur)
                    .zIndex(1f)
            )
        } else {
            Box(
                Modifier.fillMaxSize().background(Color.Gray),
                contentAlignment = Alignment.Center
            ) { Text("Permiso de cámara requerido") }
        }

        Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = overlayAlpha)).zIndex(2f)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Surface(color = Color.Transparent) {
                    TopBarMenu(
                        navController = navController,
                        switchState = switchState,
                        onSwitchChange = { switchState = it }
                    )
                }
            },
            bottomBar = {
                ButtonSearchSection(
                    onGalleryClick = { galleryLauncher.launch("image/*") },
                    onCameraClick = { takePhotoAndPredict() },
                    onSearchClick = {
                        navController.navigate("buscarPlaga")
                    }
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
            Modifier.fillMaxSize().zIndex(4f)
        ) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .offset(y = verticalOffset)
                    .size(80.dp, 120.dp)
                    .scale(scale * pulseScale)
                    .pointerInput(Unit) { detectTapGestures { isFocused = !isFocused } }
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
