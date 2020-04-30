package com.gustavogoma.android.libraries.permissions_ktx

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

sealed class ManifestPermission(val value: String) {
    object AccessFineLocation : ManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    object AccessCoarseLocation : ManifestPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
}

fun ManifestPermission.requestDetails(activity: Activity, requestCode: Int) {
    ActivityCompat.requestPermissions(activity, arrayOf(value), requestCode)
}

fun Collection<ManifestPermission>.filterNotGranted(context: Context): List<ManifestPermission> {
    return this.filter { permission ->
        val permissionStatus = ActivityCompat.checkSelfPermission(context, permission.value)
        permissionStatus != PackageManager.PERMISSION_GRANTED
    }
}

fun Collection<ManifestPermission>.requestDetails(activity: Activity, requestCode: Int) {
    val permissions = this
        .map { it.value }
        .toTypedArray()

    ActivityCompat.requestPermissions(activity, permissions, requestCode)
}

fun Collection<ManifestPermission>.shouldShowRequestPermissionRationale(activity: Activity): List<ManifestPermission> {
    return this.filter { permission ->
        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.value)
    }
}