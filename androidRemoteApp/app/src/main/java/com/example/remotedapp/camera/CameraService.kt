package com.example.remotedapp.camera

/**
 * Interface for camera operations such as starting the camera
 * and taking photos.
 */
interface CameraService {

    /**
     * Starts the camera and sets up any necessary use cases.
     *
     * @param onReady Optional callback that is invoked when the camera is fully initialized.
     */
    fun startCamera(onReady: (() -> Unit)? = null)

    /**
     * Takes a photo and returns the result via a callback.
     *
     * @param onResult Callback that receives a Boolean indicating success,
     * and a String message with additional information (e.g. file name or error).
     */
    fun takePhoto(onResult: (Boolean, String) -> Unit)
}
