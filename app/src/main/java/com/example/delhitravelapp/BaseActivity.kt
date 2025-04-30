package com.example.delhitravelapp

import android.content.Context
import androidx.activity.ComponentActivity

open class BaseActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("lang_code", "en") ?: "en"
        super.attachBaseContext(newBase.wrapLocale(lang))
    }
}
