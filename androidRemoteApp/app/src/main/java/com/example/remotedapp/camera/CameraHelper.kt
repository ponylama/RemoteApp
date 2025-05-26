package com.example.remotedapp.camera

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * A helper class for managing CameraX camera operations such as starting the camera
 * and capturing photos, intended for headless or lifecycle-based control.
 *
 * @param context a [Context] that must be a [androidx.lifecycle.LifecycleOwner]
 */
class CameraHelper(private val context: Context) : CameraService {

    /** ImageCapture instance used for taking pictures. */
    private var imageCapture: ImageCapture? = null

    /** Callback to be invoked when the camera is ready. */
    private var onCameraReady: (() -> Unit)? = null

    /** Prevents multiple simultaneous camera initializations. */
    private var isCameraStarting = false

    /**
     * Starts the camera and sets up the image capture.
     *
     * @param onReady optional callback invoked when the camera is ready to use.
     */
    override fun startCamera(onReady: (() -> Unit)?) {
        if (isCameraStarting) return // Prevent multiple initializations
        isCameraStarting = true
        onCameraReady = onReady

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as androidx.lifecycle.LifecycleOwner,
                    cameraSelector,
                    imageCapture
                )
                isCameraStarting = false
                onCameraReady?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
                isCameraStarting = false
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Takes a photo and saves it to the device's external media directory.
     *
     * @param onResult a callback invoked with the success status and a message.
     * If the camera isn't ready, it will start it and retry automatically.
     */
    override fun takePhoto(onResult: (Boolean, String) -> Unit) {
        if (imageCapture == null) {
            startCamera {
                takePhoto(onResult)
            }
            return
        }

        val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_$name")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                /**
                 * Called when the image has been successfully saved.
                 */
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onResult(true, "Photo saved successfully: IMG_$name.jpg")
                }

                /**
                 * Called when there was an error saving the image.
                 */
                override fun onError(exception: ImageCaptureException) {
                    onResult(false, "Failed to save photo: ${exception.message}")
                }
            }
        )
    }
}
