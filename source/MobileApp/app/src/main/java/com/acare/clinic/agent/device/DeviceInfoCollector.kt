package com.acare.clinic.agent.device

import android.os.Build

class DeviceInfoCollector(
    private val deviceIdProvider: DeviceIdProvider
) {
    fun collect(): DeviceInfo {
        return DeviceInfo(
            deviceId = deviceIdProvider.getDeviceId(),
            deviceName = "${Build.MANUFACTURER} ${Build.MODEL}",
            osVersion = "Android ${Build.VERSION.RELEASE} API ${Build.VERSION.SDK_INT}",
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL
        )
    }
}