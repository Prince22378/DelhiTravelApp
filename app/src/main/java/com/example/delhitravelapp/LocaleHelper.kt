package com.example.delhitravelapp

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun Context.wrapLocale(lang: String): Context {
    val locale = Locale(lang)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration).apply {
        setLocale(locale)
    }
    return createConfigurationContext(config)
}
