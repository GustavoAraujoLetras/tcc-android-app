package io.github.pucmaps.android.builder.frontend.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

open class PUCMapsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

}