package com.example.remotedapp.property

import android.content.Context
import android.os.Build
import android.provider.Settings

/**
 * Helper class for retrieving basic device information such as
 * model, manufacturer, OS version, and unique Android ID.
 *
 * @param context The application or activity context used to access system services.
 */
class DeviceInfoHelper(private val context: Context) {

    /**
     * Retrieves a map of key-value pairs describing various device properties.
     *
     * @return A [Map] containing information such as model, brand, Android version,
     * hardware details, and the Android ID.
     */
    fun getDeviceData(): Map<String, String> {
        val deviceData = mutableMapOf<String, String>()

        deviceData["Manufacturer"] = Build.MANUFACTURER
        deviceData["Model"] = Build.MODEL
        deviceData["Device"] = Build.DEVICE
        deviceData["Brand"] = Build.BRAND
        deviceData["Hardware"] = Build.HARDWARE
        deviceData["Product"] = Build.PRODUCT
        deviceData["Board"] = Build.BOARD
        deviceData["Bootloader"] = Build.BOOTLOADER
        deviceData["Display"] = Build.DISPLAY
        deviceData["Fingerprint"] = Build.FINGERPRINT
        deviceData["AndroidVersion"] = Build.VERSION.RELEASE
        deviceData["SDKVersion"] = Build.VERSION.SDK_INT.toString()

        // Unique ID (Android ID)
        deviceData["AndroidID"] = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        return deviceData
    }
}
