package com.example.delhitravelapp

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.delhitravelapp.data.DatabaseModule
import com.example.delhitravelapp.data.StationEntity
import com.example.delhitravelapp.data.StationRepository
import com.example.delhitravelapp.RoutesActivity
import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme
import java.util.Locale

class HomeActivity : BaseActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private var ttsEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) Read the flag from OptionActivity
        ttsEnabled = intent.getBooleanExtra("tts_enabled", false)

        // 2) Initialize TTS engine
        tts = TextToSpeech(this, this)

        // 3) Inflate Compose UI, passing the flag down
        setContent {
            DelhiTravelAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(tts, ttsEnabled)
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS && ttsEnabled) {
            val result = tts.setLanguage(Locale.getDefault())
            tts.voice = tts.defaultVoice
            when (result) {
                TextToSpeech.LANG_MISSING_DATA ->
                    Toast.makeText(
                        this,
                        "TTS data missing: please install a TTS language pack.",
                        Toast.LENGTH_LONG
                    ).show()
                TextToSpeech.LANG_NOT_SUPPORTED ->
                    Toast.makeText(
                        this,
                        "TTS language not supported on this device.",
                        Toast.LENGTH_LONG
                    ).show()
                else -> {
                    tts.speak(
                        "Welcome to Delhi Travel App",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "welcome"
                    )
                }
            }
        } else if (status != TextToSpeech.SUCCESS) {
            Toast.makeText(this, "TTS initialization failed.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
    }
}

@Composable
fun HomeScreen(tts: TextToSpeech, ttsEnabled: Boolean) {
    val context = LocalContext.current
    val view = LocalView.current
    val historyDesc = stringResource(R.string.travel_history)
    val openingHistory = stringResource(R.string.opening_history)

    var startStation by remember { mutableStateOf("") }
    var endStation by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.welcome),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Start Station selection field
        StationSelectionField(
            label = "Select Start Station",
            onStationSelected = { startStation = it }
        )

        Spacer(Modifier.height(16.dp))

        // End Station selection field
        StationSelectionField(
            label = "Select End Station",
            onStationSelected = { endStation = it }
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if (startStation.isNotEmpty() && endStation.isNotEmpty()) {
                    // Check if both stations are selected
                    val intent = Intent(context, RoutesActivity::class.java)
                    intent.putExtra("start_station", startStation)
                    intent.putExtra("end_station", endStation)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Please select both stations", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = stringResource(R.string.show_routes),
                fontSize = 16.sp,
                color = Color.White
            )
        }
        Spacer(Modifier.height(16.dp))

        Text(
            text = historyDesc,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier
                .semantics { contentDescription = historyDesc }
                .clickable {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    // 4) Only speak if TTS was enabled
                    if (ttsEnabled) {
                        tts.speak(
                            openingHistory,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "history"
                        )
                    }
                    context.startActivity(Intent(context, HistoryActivity::class.java))
                }
        )
    }
}

@Composable
fun StationSelectionField(
    label: String,
    onStationSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var stations by remember { mutableStateOf<List<StationEntity>>(emptyList()) }
    var isSuggestionsVisible by remember { mutableStateOf(true) } // Track visibility of suggestions

    // Fetch stations from the database as the user types
    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            val stationRepo = StationRepository(DatabaseModule.provideDatabase(context).stationDao())
            stationRepo.searchStations(query).collect { stations = it.take(5) } // Limit to top 5 results
        } else {
            stations = emptyList() // Clear results when the query is empty
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // OutlinedTextField for station selection
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                isSuggestionsVisible = true // Show suggestions while typing
            },
            placeholder = { Text("Start typing a station...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Display suggestions if query is not empty, suggestions exist, and they are visible
        if (isSuggestionsVisible && query.isNotEmpty() && stations.isNotEmpty()) {
            LazyColumn {
                items(stations) { station ->
                    Text(
                        text = station.stopName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // When a station is selected, update the input and hide suggestions
                                onStationSelected(station.stopName) // Select the station
                                query = station.stopName // Set selected station name
                                isSuggestionsVisible = false // Hide suggestions after selection
                            }
                            .padding(8.dp)
                    )
                }
            }
        } else if (query.isNotEmpty() && stations.isEmpty()) {
            // Display "No results found" only if no results match the query
            Text("No results found", color = Color.Gray)
        }
    }
}


//@Composable
//fun StationSelectionField(
//    label: String,
//    onStationSelected: (String) -> Unit
//) {
//    val context = LocalContext.current
//    var query by remember { mutableStateOf("") }
//    var stations by remember { mutableStateOf<List<StationEntity>>(emptyList()) }
//    var isSuggestionsVisible by remember { mutableStateOf(true) } // Track visibility of suggestions
//    var isSelectionMade by remember { mutableStateOf(false) } // Track if a station has been selected
//
////    // Fetch stations from the database as the user types
////    LaunchedEffect(query) {
////        val stationRepo = StationRepository(DatabaseModule.provideDatabase(context).stationDao())
////        stationRepo.searchStations(query).collect { stations = it }
////    }
//    // Fetch stations from the database as the user types
//    LaunchedEffect(query) {
//        if (query.isNotEmpty() && !isSelectionMade) {
//            val stationRepo = StationRepository(DatabaseModule.provideDatabase(context).stationDao())
//            stationRepo.searchStations(query).collect { stations = it.take(5) } // Limit to top 5 results
//        } else {
//            stations = emptyList() // Clear results when the query is empty or selection is made
//        }
//    }
//
//    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
//        Text(
//            text = label,
//            style = MaterialTheme.typography.bodyMedium,
//            modifier = Modifier.padding(bottom = 4.dp)
//        )
//
//        // OutlinedTextField for station selection
//        OutlinedTextField(
//            value = query,
//            onValueChange = {
//                if (!isSelectionMade) {
//                    query = it
//                    isSuggestionsVisible = true // Show suggestions while typing
//                }
//            },
//            placeholder = { Text("Start typing a station...") },
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true,
//            enabled = !isSelectionMade // Disable the text field after a selection is made
//        )
//
//        // Display suggestions if query is not empty, suggestions exist, and they are visible
//        if (isSuggestionsVisible && query.isNotEmpty() && stations.isNotEmpty() && !isSelectionMade) {
//            LazyColumn {
//                items(stations) { station ->
//                    Text(
//                        text = station.stopName,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable {
//                                // When a station is selected, update the input and hide suggestions
//                                onStationSelected(station.stopName) // Select the station
//                                query = station.stopName // Set selected station name
//                                isSuggestionsVisible = false // Hide suggestions after selection
//                                isSelectionMade = true // Mark that selection is made
//                            }
//                            .padding(8.dp)
//                    )
//                }
//            }
//        } else if (query.isNotEmpty() && stations.isEmpty() && !isSelectionMade) {
//            // Display "No results found" only if no results match the query
//            Text("No results found", color = Color.Gray)
//        }
//    }
//}
