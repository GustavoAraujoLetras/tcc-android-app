package io.github.pucmaps.android.frontend.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import io.github.pucmaps.android.R
import com.gustavogoma.android.libraries.permissions_ktx.ManifestPermission

class PermissionExplanationDialog(
    private val permission: com.gustavogoma.android.libraries.permissions_ktx.ManifestPermission
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = this.activity ?: throw IllegalStateException("Activity cannot be null")

        val messageStringRes = when (permission) {
            com.gustavogoma.android.libraries.permissions_ktx.ManifestPermission.AccessFineLocation,
            com.gustavogoma.android.libraries.permissions_ktx.ManifestPermission.AccessCoarseLocation -> {
                R.string.permission_explanation_access_fine_location
            }
            else -> throw IllegalStateException("Unknown permission")
        }

        return AlertDialog.Builder(activity)
            .setMessage(messageStringRes)
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .create()
    }

    companion object {
        private val TAG = PermissionExplanationDialog::class.java.simpleName

        fun show(fragmentManager: FragmentManager, permission: com.gustavogoma.android.libraries.permissions_ktx.ManifestPermission) {
            PermissionExplanationDialog(
                permission
            )
                .show(fragmentManager,
                    TAG
                )
        }
    }
}