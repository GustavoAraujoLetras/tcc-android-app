package io.github.pucmaps.android

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication

class PUCMapsApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}