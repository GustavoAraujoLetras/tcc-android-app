package io.github.pucmaps.manager.android.frontend.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.gustavogoma.android.libraries.permissions_ktx.ManifestPermission
import io.github.pucmaps.manager.android.R

class PermissionExplanationDialog(
    private val permission: ManifestPermission
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        requireNotNull(activity) { "Activity cannot be null" }

        val messageStringRes = when (permission) {
            ManifestPermission.AccessFineLocation,
            ManifestPermission.AccessCoarseLocation -> {
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

        fun show(fragmentManager: FragmentManager, permission: ManifestPermission) {
            PermissionExplanationDialog(permission)
                .show(fragmentManager, TAG)
        }
    }
}