package io.github.pucmaps.android.extension

import android.content.Context
import io.github.pucmaps.android.R

val Context.appName: String
    get() = getString(R.string.app_name)