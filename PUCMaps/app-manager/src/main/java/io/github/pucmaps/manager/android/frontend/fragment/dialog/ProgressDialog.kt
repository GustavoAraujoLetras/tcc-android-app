package io.github.pucmaps.manager.android.frontend.fragment.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import io.github.pucmaps.manager.android.R

class ProgressDialog : DialogFragment() {

    private lateinit var bodyTextView: AppCompatTextView
    private lateinit var titleTextView: AppCompatTextView
    private lateinit var progressView: ProgressBar

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = this.activity ?: throw IllegalStateException("Activity cannot be null")
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater

        return builder
            .setView(inflater.inflate(R.layout.dialog_progress, null))
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleTextView = view.findViewById(R.id.title_text_view)
        bodyTextView = view.findViewById(R.id.body_text_view)
        progressView = view.findViewById(R.id.body_progress_view)
    }

    companion object {
        private val TAG = ProgressDialog::class.java.simpleName

        fun show(
            fragmentManager: FragmentManager,
            isIndeterminated: Boolean,
            @StringRes titleRes: Int,
            @StringRes messageRes: Int
        ) = ProgressDialog().apply {
            titleTextView.setText(titleRes)
            bodyTextView.setText(messageRes)
            progressView.isIndeterminate = isIndeterminated

            show(fragmentManager, TAG)
        }
    }
}