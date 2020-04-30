package com.gustavogoma.android.libraries.permissions_ktx

import android.content.Context

fun Context.checkPermissionsGranted(permissions: List<ManifestPermission>): Boolean {
    val missingPermissions = permissions.filterNotGranted(this)
    return missingPermissions.isNotEmpty()
}