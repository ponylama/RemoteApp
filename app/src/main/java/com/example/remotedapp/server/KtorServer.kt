package com.example.remotedapp.server

import android.util.Log
import com.example.remotedapp.camera.CameraService
import com.example.remotedapp.property.DeviceInfoHelper
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * An implementation of the [Server] interface using Ktor HTTP server.
 * Handles remote HTTP commands to control the camera and query device info.
 *
 * @property cameraHelper abstraction for camera operations
 * @property deviceInfoHelper helper class for fetching device metadata
 * @property port the port the server will bind to (default is 8080)
 */
class KtorHttpServer(
    private val cameraHelper: CameraService,
    private val deviceInfoHelper: DeviceInfoHelper,
    private val port: Int = 8080
) : Server {

    /** The Ktor embedded server engine instance */
    private var server: ApplicationEngine? = null

    /**
     * Starts the HTTP server with endpoints:
     * - `POST /opencamera`: opens the camera
     * - `POST /takephoto`: takes a photo
     * - `GET /getprop`: retrieves device properties
     * - `GET /`: health check
     */
    override fun start() {
        server = embeddedServer(CIO, port = port) {
            install(ContentNegotiation) {
                gson()
            }

            routing {
                post("/opencamera") {
                    Log.d("KtorServer", "Received POST to /opencamera")
                    suspendCancellableCoroutine { cont ->
                        cameraHelper.startCamera {
                            cont.resume(Unit) {}
                        }
                    }
                    call.respond(mapOf("status" to "camera opened"))
                }

                post("/takephoto") {
                    Log.d("KtorServer", "Received POST to /takephoto")
                    val (success, message) = suspendCancellableCoroutine { cont ->
                        cameraHelper.takePhoto { isSuccess, resultMessage ->
                            cont.resume(Pair(isSuccess, resultMessage)) {}
                        }
                    }
                    call.respond(
                        mapOf(
                            "success" to success,
                            "message" to message
                        )
                    )
                }

                get("/getprop") {
                    Log.d("KtorServer", "Received GET to /getprop")
                    val deviceInfo = deviceInfoHelper.getDeviceData()
                    call.respond(deviceInfo)
                }

                get("/") {
                    call.respond(mapOf("message" to "Ktor server is running"))
                }
            }
        }.start(wait = false)
    }

    /**
     * Suspends and takes a photo using the camera helper.
     * Useful for calling from coroutines or other non-blocking contexts.
     *
     * @return a map containing `status` ("ok" or "error") and a `message`.
     */
    override suspend fun takePhotoSuspend(): Map<String, String> = suspendCancellableCoroutine { cont ->
        cameraHelper.takePhoto { success, message ->
            val result = mapOf(
                "status" to if (success) "ok" else "error",
                "message" to message
            )
            cont.resume(result)
        }
    }

    /**
     * Stops the running Ktor server, if it is active.
     */
    override fun stop() {
        server?.stop()
    }
}
