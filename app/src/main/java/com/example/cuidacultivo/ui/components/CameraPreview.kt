package com.example.cuidacultivo.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors

/**
 * Muestra la vista previa de la cámara y enlaza el caso de uso ImageCapture.
 *
 * @param imageCapture Objeto ImageCapture instanciado en HomeScreen para tomar la foto.
 * @param enableFlash Controla el estado del flash (antorcha) para la previsualización.
 */
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture, // <-- ¡NUEVO PARÁMETRO REQUERIDO!
    enableFlash: Boolean = false
) {
    val context = LocalContext.current
    var camera by remember { mutableStateOf<Camera?>(null) }

    // Vista de previsualización cuando es Preview en Android Studio
    if (LocalInspectionMode.current) {
        Box(modifier = modifier.background(Color.DarkGray))
        return
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val lifecycleOwner = ctx as LifecycleOwner // El Context debe ser un LifecycleOwner (ej. MainActivity)

                    val preview = Preview.Builder().build().apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                    }

                    // Inicializar el modo de flash en ImageCapture
                    imageCapture.flashMode = if (enableFlash) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF

                    cameraProvider.unbindAll()

                    // Unir todos los casos de uso: Preview y ImageCapture
                    camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture // <-- Se enlaza el ImageCapture
                    )

                } catch (e: Exception) {
                    // Nota: Asegúrate de que el Contexto sea un LifecycleOwner (ej. ComponentActivity)
                    Log.e("CameraPreview", "Error inicializando cámara: ${e.message}")
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )

    // Manejo del flash en tiempo real
    LaunchedEffect(enableFlash) {
        // Establecer el modo de flash en ImageCapture para la foto real
        imageCapture.flashMode = if (enableFlash) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
        // Habilitar/deshabilitar la linterna para previsualización
        camera?.cameraControl?.enableTorch(enableFlash)
    }
}

// --- FUNCIONES AUXILIARES NECESARIAS EN ESTE MISMO PAQUETE (o en Utils) ---

/**
 * Dispara la captura de imagen real de CameraX.
 */
fun captureImage(
    imageCapture: ImageCapture,
    executor: java.util.concurrent.Executor,
    onImageCaptured: (Bitmap) -> Unit,
    context: Context
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = image.toBitmap()
                val rotatedBitmap = rotateBitmap(bitmap, image.imageInfo.rotationDegrees)

                // Enviar el Bitmap real a HomeScreen para procesamiento
                onImageCaptured(rotatedBitmap)

                image.close()
                Log.d("CameraPreview", "Captura exitosa. Tamaño: ${rotatedBitmap.width}x${rotatedBitmap.height}")
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraPreview", "Error al capturar imagen: ${exception.message}", exception)
            }
        }
    )
}

/**
 * Rota el Bitmap si CameraX indica un ángulo de rotación.
 */
fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
    if (degrees == 0) return bitmap
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}