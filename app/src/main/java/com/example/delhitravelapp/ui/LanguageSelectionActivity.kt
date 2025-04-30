package com.example.delhitravelapp.ui

import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter
import android.icu.lang.UScript
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
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
import java.util.concurrent.atomic.AtomicLong


// --- Data models ---
data class Language(val native: String, val english: String)
data class FallingChar(val id: Long, val char: String, val x: Float, val yStart: Float)

class LanguageSelectionActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DelhiTravelAppTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    LanguageSelectionScreen { chosen ->
                        // map the chosen.english to a locale code
                        val code = when (chosen.english.lowercase()) {
                            "hindi", "हिन्दी"       -> "hi"
                            "english"              -> "en"
                            "spanish", "español"   -> "es"
                            "french", "français"   -> "fr"
                            "german", "deutsch"    -> "de"
                            "chinese", "中文"       -> "zh"
                            "japanese", "日本語"     -> "ja"
                            "korean", "한국어"       -> "ko"
                            "arabic", "العربية"     -> "ar"
                            "russian", "русский"    -> "ru"
                            "portuguese", "português" -> "pt"
                            "urdu", "اردو"           -> "ur"
                            else -> "en"
                        }
                        getSharedPreferences("settings", Context.MODE_PRIVATE)
                            .edit()
                            .putString("lang_code", code)
                            .apply()

                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageSelectionScreen(onContinue: (Language) -> Unit) {
    // 1) Languages
    val all = listOf(
        Language("English",   "English"),
        Language("हिन्दी",     "Hindi"),
        Language("Español",   "Spanish"),
        Language("Français",  "French"),
        Language("Deutsch",   "German"),
        Language("中文",       "Chinese"),
        Language("日本語",     "Japanese"),
        Language("한국어",     "Korean"),
        Language("العربية",   "Arabic"),
        Language("Русский",   "Russian"),
        Language("Português", "Portuguese"),
        Language("اردو",       "Urdu")
    )
    var selected by remember { mutableStateOf(all.first()) }

    // 2) Search filter
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) all
        else all.filter {
            it.native.contains(query, ignoreCase = true) ||
                    it.english.contains(query, ignoreCase = true)
        }
    }

    // 3) Prepare falling chars
    val nextId       = remember { AtomicLong(0) }
    val fallingChars = remember { mutableStateListOf<FallingChar>() }

    // ✂️ **PRE-COMPUTED** in Composable scope:
    val alphabet = rememberAlphabetFor(selected)
    val density  = LocalDensity.current
    var heightPx by remember { mutableStateOf(0f) }

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .pointerInput(selected) {
                detectTapGestures { offset: Offset ->
                    if (alphabet.isNotEmpty()) {
                        fallingChars += FallingChar(
                            id     = nextId.getAndIncrement(),
                            char   = alphabet.random().toString(),
                            x      = offset.x,
                            yStart = offset.y
                        )
                    }
                }
            }
    ) {
        LaunchedEffect(maxHeight) {
            heightPx = with(density) { maxHeight.toPx() }
        }
        fallingChars.forEach { fc ->
            FallingCharItem(fc, heightPx) {
                fallingChars.remove(fc)
            }
        }

        // 4) UI
        Column(Modifier.fillMaxSize().padding(16.dp)) {
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
                    LanguageCard(
                        lang       = lang,
                        isSelected = lang == selected,
                        onSelect   = { selected = lang }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { onContinue(selected) },
                modifier = Modifier.fillMaxWidth().height(48.dp)
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
                cp.toChar()
            else null
        }
    }
}

@Composable
fun LanguageCard(lang: Language, isSelected: Boolean, onSelect: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue   = if (pressed) 0.95f else 1f,
        animationSpec = tween(150)
    )

    Card(
        modifier = Modifier
            .size(100.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .pointerInput(lang) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try { awaitRelease() } finally { pressed = false }
                    },
                    onTap = { onSelect() }
                )
            },
        shape      = RoundedCornerShape(12.dp),
        colors     = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else Color(0xFFE0F7FA)
        ),
        elevation  = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text      = lang.native,
                style     = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                textAlign = TextAlign.Center,
                color     = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
            )
            Text(
                text      = lang.english,
                style     = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color     = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
            )
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
