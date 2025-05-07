package com.example.delhitravelapp

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme
import java.util.*

class SettingsActivity : BaseActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private var ttsEnabled: Boolean = true
    private var selectedLanguage: Locale = Locale.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        setContent {
            DelhiTravelAppTheme {
                SettingsScreen() // Calling the composable
            }
        }
    }

    @Composable
    fun SettingsScreen() {
        val context = LocalContext.current
        var language by remember { mutableStateOf("English") }
        var ttsState by remember { mutableStateOf(ttsEnabled) }

        // Language options
        val languages = listOf("English", "Hindi", "French", "Spanish")
        val languageList = languages.map { it.toString() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("TTS Settings", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(32.dp))

            Text("Select Language", fontSize = 16.sp)

            // Dropdown for selecting language
            var selectedLanguageOption by remember { mutableStateOf(language) }
            DropdownMenu(
                expanded = true,
                onDismissRequest = {},
                modifier = Modifier.padding(16.dp)
            ) {
                languageList.forEach { lang ->
                    DropdownMenuItem(
                        onClick = {
                            selectedLanguageOption = lang
                            language = lang
                            selectedLanguage = when (lang) {
                                "English" -> Locale.ENGLISH
                                "Hindi" -> Locale("hi")
                                "French" -> Locale.FRENCH
                                "Spanish" -> Locale("es")
                                else -> Locale.getDefault()
                            }
                        },
                        text = TODO(),
                        modifier = TODO(),
                        leadingIcon = TODO(),
                        trailingIcon = TODO(),
                        enabled = TODO(),
                        colors = TODO(),
                        contentPadding = TODO()
                    ) {
                        Text(lang)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Switch for enabling/disabling TTS
            Text("Enable TTS")
            Switch(
                checked = ttsState,
                onCheckedChange = {
                    ttsState = it
                    ttsEnabled = it
                    if (!it) {
                        tts.stop() // Stop TTS if disabled
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Apply button
            Button(
                onClick = {
                    // Apply changes to TTS
                    if (ttsEnabled) {
                        tts.language = selectedLanguage
                        tts.speak("Language changed to ${selectedLanguage.displayLanguage}", TextToSpeech.QUEUE_FLUSH, null, "init")
                    }
                    context.startActivity(Intent(context, HomeActivity::class.java))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "Apply",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }

    private fun DropdownMenuItem(
        onClick: () -> Unit,
        text: Nothing,
        modifier: Nothing,
        leadingIcon: Nothing,
        trailingIcon: Nothing,
        enabled: Nothing,
        colors: Nothing,
        contentPadding: Nothing,
        interactionSource: @Composable () -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = selectedLanguage
            if (ttsEnabled) {
                // Initializing TTS with the selected language
                tts.speak("Settings loaded", TextToSpeech.QUEUE_FLUSH, null, "init")
            }
        } else {
            Toast.makeText(this, "TTS initialization failed.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
    }
}
