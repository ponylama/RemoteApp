package com.example.remotedapp.server

/**
 * Interface representing a server that provides functionalities for controlling
 * the camera and retrieving device data. It includes methods to start, stop the server,
 * and take a photo asynchronously.
 */
interface Server {

    /**
     * Starts the server and begins listening for incoming requests.
     * The specific routes and functionalities are determined by the implementing class.
     */
    fun start()

    /**
     * Stops the server and releases any resources being used.
     * Should be called to properly shut down the server when no longer needed.
     */
    fun stop()

    /**
     * Takes a photo asynchronously, using the camera service.
     * This is a suspending function that can be used in coroutines.
     *
     * @return a map containing the result of the operation:
     *  - `status`: a string indicating success ("ok") or failure ("error")
     *  - `message`: a message with additional details about the result
     */
    suspend fun takePhotoSuspend(): Map<String, String>
}
