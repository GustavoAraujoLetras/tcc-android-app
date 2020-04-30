package io.github.pucmaps.android.extension

import android.util.Log
import kotlin.reflect.KProperty0

fun <T : KProperty0<*>> T.logIt(tag: String) {
    Log.d(tag, "$name = ${get()}")
}