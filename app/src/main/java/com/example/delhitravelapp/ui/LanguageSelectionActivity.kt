package com.example.delhitravelapp.ui

import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter
import android.icu.lang.UScript
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.delhitravelapp.BaseActivity
import com.example.delhitravelapp.HomeActivity
import com.example.delhitravelapp.R
import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong

// --- Data models ---
data class Language(val native: String, val english: String)
data class FallingChar(val id: Long, val char: String, val x: Float, val yStart: Float)

class LanguageSelectionActivity : BaseActivity(), TextToSpeech.OnInitListener {
    private var ttsEnabled = false
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // grab incoming flag
        ttsEnabled = intent.getBooleanExtra("tts_enabled", false)
        if (ttsEnabled) tts = TextToSpeech(this, this)

        setContent {
            DelhiTravelAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = MaterialTheme.colorScheme.background
                ) {
                    LanguageSelectionScreen(
                        onContinue = { chosen ->
                            // persist lang_code
                            val code = codeFor(chosen)
                            getSharedPreferences("settings", Context.MODE_PRIVATE)
                                .edit()
                                .putString("lang_code", code)
                                .apply()
                            // forward flag + launch Home
                            Intent(this, HomeActivity::class.java).also {
                                it.putExtra("tts_enabled", ttsEnabled)
                                startActivity(it)
                            }
                            finish()
                        },
                        tts        = if (ttsEnabled) tts else null,
                        ttsEnabled = ttsEnabled
                    )
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (ttsEnabled && status == TextToSpeech.SUCCESS) {
            val prefs    = getSharedPreferences("settings", Context.MODE_PRIVATE)
            val langCode = prefs.getString("lang_code", "en") ?: "en"
            val result   = tts.setLanguage(Locale(langCode))
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                startActivity(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA))
            }
        } else if (ttsEnabled) {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun codeFor(lang: Language): String = when (lang.english.lowercase()) {
        "hindi","हिन्दी"       -> "hi"
        "english"              -> "en"
        "spanish","español"    -> "es"
        "french","français"    -> "fr"
        "german","deutsch"     -> "de"
        "chinese","中文"       -> "zh"
        "japanese","日本語"     -> "ja"
        "korean","한국어"       -> "ko"
        "arabic","العربية"     -> "ar"
        "russian","русский"    -> "ru"
        "portuguese","português"-> "pt"
        "urdu","اردو"           -> "ur"
        else                   -> "en"
    }
}

@Composable
fun LanguageSelectionScreen(
    onContinue: (Language) -> Unit,
    tts: TextToSpeech?,
    ttsEnabled: Boolean
) {
    val view    = LocalView.current
    val density = LocalDensity.current

    // languages list
    val all = listOf(
        Language("English","English"),
        Language("हिन्दी","Hindi"),
        Language("Español","Spanish"),
        Language("Français","French"),
        Language("Deutsch","German"),
        Language("中文","Chinese"),
        Language("日本語","Japanese"),
        Language("한국어","Korean"),
        Language("العربية","Arabic"),
        Language("Русский","Russian"),
        Language("Português","Portuguese"),
        Language("اردو","Urdu")
    )
    var selected by remember { mutableStateOf(all.first()) }
    var query    by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) all
        else all.filter {
            it.native.contains(query, true) ||
                    it.english.contains(query, true)
        }
    }

    // falling-char state
    val nextId       = remember { AtomicLong(0) }
    val fallingChars = remember { mutableStateListOf<FallingChar>() }
    val alphabet     = rememberAlphabetFor(selected)
    var heightPx     by remember { mutableStateOf(0f) }

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(selected) {
                detectTapGestures { offset: Offset ->
                    // spawn falling char (no haptic here)
                    fallingChars += FallingChar(
                        id     = nextId.getAndIncrement(),
                        char   = alphabet.random().toString(),
                        x      = offset.x,
                        yStart = offset.y
                    )
                }
            }
    ) {
        // now maxHeight is available
        LaunchedEffect(maxHeight) {
            heightPx = with(density) { maxHeight.toPx() }
        }
        fallingChars.forEach { fc ->
            FallingCharItem(fc, heightPx) { fallingChars.remove(fc) }
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text      = stringResource(R.string.choose_language),
                style     = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value         = query,
                onValueChange = { query = it },
                placeholder   = { Text(stringResource(R.string.search_languages)) },
                singleLine    = true,
                shape         = RoundedCornerShape(50),
                modifier      = Modifier.fillMaxWidth().height(56.dp)
            )
            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns               = GridCells.Adaptive(minSize = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp),
                modifier              = Modifier.weight(1f)
            ) {
                items(filtered) { lang ->
                    Card(
                        modifier = Modifier
                            .size(100.dp)
                            .pointerInput(lang) {
                                detectTapGestures {
                                    // subtle haptic on card tap
                                    if (ttsEnabled) {
                                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    }
                                    selected = lang
                                    if (ttsEnabled) {
                                        tts?.speak(
                                            "You’ve selected ${lang.english}. To proceed further press Continue.",
                                            TextToSpeech.QUEUE_FLUSH,
                                            null,
                                            "select"
                                        )
                                    }
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (lang == selected)
                                MaterialTheme.colorScheme.primary
                            else Color(0xFFE0F7FA)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(lang.native, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                            Text(lang.english, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    // subtle haptic on Continue
                    if (ttsEnabled) {
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    }
                    onContinue(selected)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape    = RoundedCornerShape(24.dp)
            ) {
                Text(stringResource(R.string.cont))
            }
        }
    }
}

@Composable
fun rememberAlphabetFor(selected: Language): List<Char> {
    val codePoint = selected.native.firstOrNull()?.code ?: 'A'.code
    val script    = UScript.getScript(codePoint)
    return remember(script) {
        (0x0000..0xFFFF).mapNotNull { cp ->
            if (UScript.getScript(cp) == script && UCharacter.isUAlphabetic(cp))
                cp.toChar() else null
        }
    }
}

@Composable
fun FallingCharItem(
    fc: FallingChar,
    containerHeight: Float,
    onFinished: () -> Unit
) {
    var y by remember { mutableStateOf(fc.yStart) }
    LaunchedEffect(fc.id) {
        val speed = 800f      // px/sec
        val frame = 16L       // ms
        val delta = speed * frame / 1000f
        while (y < containerHeight) {
            y += delta
            delay(frame)
        }
        onFinished()
    }
    Text(
        text     = fc.char,
        fontSize = 24.sp,
        color    = MaterialTheme.colorScheme.primary,
        modifier = Modifier.offset { IntOffset(fc.x.toInt(), y.toInt()) }
    )
}
