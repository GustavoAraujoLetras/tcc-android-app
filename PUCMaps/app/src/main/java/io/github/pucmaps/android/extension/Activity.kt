package io.github.pucmaps.android.extension

import android.content.Context
import android.content.Intent
import kotlin.reflect.KClass

fun KClass<*>.startActivity(fromContext: Context) {
    val intent = Intent(fromContext, this.java)
    fromContext.startActivity(intent)
}