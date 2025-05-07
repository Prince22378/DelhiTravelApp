package com.example.delhitravelapp

import android.content.Context
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import java.util.Locale

open class BaseActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("lang_code", "en") ?: "en"
        super.attachBaseContext(newBase.wrapLocale(lang))
    }

    fun Context.wrapLocale(lang: String): Context {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration).apply {
            setLocale(locale)
        }
        return createConfigurationContext(config)
    }
}
