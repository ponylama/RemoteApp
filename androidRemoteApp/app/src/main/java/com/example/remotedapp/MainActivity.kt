package com.example.remotedapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.remotedapp.camera.CameraHelper
import com.example.remotedapp.property.DeviceInfoHelper
import com.example.remotedapp.server.KtorHttpServer
import com.example.remotedapp.server.Server
import java.net.Inet4Address
import java.net.NetworkInterface

/**
 * Main activity that initializes the camera system, device info helper,
 * and a Ktor-based HTTP server to allow remote commands.
 *
 * Provides button actions for manual testing: open camera, take photo,
 * get device info, and fetch IP address.
 */
class MainActivity : AppCompatActivity() {

    /** Camera helper for managing CameraX operations */
    private lateinit var cameraHelper: CameraHelper

    /** Helper for retrieving device property data */
    private lateinit var deviceInfoHelper: DeviceInfoHelper

    /** HTTP server for receiving remote commands */
    private lateinit var server: Server

    /** Request code for camera permission */
    private val REQUEST_CAMERA_PERMISSION = 1001

    /**
     * Initializes UI and system components, starts the server, and sets up event listeners.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraHelper = CameraHelper(this)
        deviceInfoHelper = DeviceInfoHelper(this)

        // Ask for camera permission if needed
        ensureCameraPermissionThenStart()

        // Start HTTP server
        server = KtorHttpServer(cameraHelper, deviceInfoHelper)
        try {
            server.start()
            Log.d("HTTPServer", "Server started")
        } catch (e: Exception) {
            Log.e("HTTPServer", "Failed to start server", e)
        }

        // Bind buttons to actions
        val btnOpenCameraOnly = findViewById<Button>(R.id.btn_camera_open)
        val btnCaptureAndSave = findViewById<Button>(R.id.btn_take_photo)
        val btnGetProp = findViewById<Button>(R.id.btn_get_prop)
        val btnGetIp = findViewById<Button>(R.id.btn_get_ip)

        btnOpenCameraOnly.setOnClickListener {
            cameraHelper.startCamera()
        }

        btnCaptureAndSave.setOnClickListener {
            cameraHelper.takePhoto { success, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        btnGetProp.setOnClickListener {
            val data = deviceInfoHelper.getDeviceData()
            data.forEach { (key, value) ->
                println("$key: $value")
            }
        }

        btnGetIp.setOnClickListener {
            val ipAddress = getLocalIpAddress()
            println("ip: $ipAddress")
        }
    }

    /**
     * Checks if camera permission is granted. If not, requests it from the user.
     */
    private fun ensureCameraPermissionThenStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    /**
     * Callback for the result from requesting permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Camera permission allowed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Stops the HTTP server when the activity is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        server.stop()
    }

    /**
     * Retrieves the local IPv4 address of the device.
     *
     * @return the local IP address as a [String], or null if not found.
     */
    fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (intf in interfaces) {
                val addresses = intf.inetAddresses
                for (addr in addresses) {
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }
}
