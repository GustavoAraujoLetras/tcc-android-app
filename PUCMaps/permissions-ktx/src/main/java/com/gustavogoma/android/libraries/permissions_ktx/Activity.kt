package com.gustavogoma.android.libraries.permissions_ktx

import android.app.Activity

fun Activity.requestPermissions(
    permissions: List<ManifestPermission>,
    requestCode: Int,
    onPermissionsRequestRationale: (List<ManifestPermission>) -> Unit
): Boolean {
    val permissionsNeedingRequestPermissionRationale = permissions
        .shouldShowRequestPermissionRationale(this)

    if (permissionsNeedingRequestPermissionRationale.isEmpty()) {
        permissions.requestDetails(this, requestCode)
        return true
    }

    permissionsNeedingRequestPermissionRationale
        .reversed()
        .let { onPermissionsRequestRationale(it) }

    return false
}