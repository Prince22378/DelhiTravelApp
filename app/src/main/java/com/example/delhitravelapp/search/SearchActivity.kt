//package com.example.delhitravelapp.search
//import androidx.core.widget.addTextChangedListener
//
//import android.content.Intent
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.widget.ArrayAdapter
//import android.widget.AutoCompleteTextView
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.delhitravelapp.HomeActivity
//import com.example.delhitravelapp.R
//import com.example.delhitravelapp.data.DatabaseModule
//import com.example.delhitravelapp.data.GtfsParser
//import com.example.delhitravelapp.data.RouteRepository
//import com.example.delhitravelapp.data.StationRepository
//import com.example.delhitravelapp.data.StopTimeRepository
//import com.example.delhitravelapp.data.TripRepository
//import com.example.delhitravelapp.data.StationEntity
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//
//class SearchActivity : AppCompatActivity() {
//    private lateinit var acStart: AutoCompleteTextView
//    private lateinit var acEnd: AutoCompleteTextView
//    private lateinit var btnSearchRoutes: Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search)
//
//        // view refs
//        acStart = findViewById(R.id.acStartStation)
//        acEnd = findViewById(R.id.acEndStation)
//        btnSearchRoutes = findViewById(R.id.btnShowRoutes)
//
//        // init database and repos
//        val db = DatabaseModule.provideDatabase(this)
//        val stationRepo = StationRepository(db.stationDao())
//        val routeRepo = RouteRepository(db.routeDao())
//        val stopTimeRepo = StopTimeRepository(db.stopTimeDao())
//        val parser = GtfsParser(
//            this,
//            stationRepo,
//            routeRepo,
//            TripRepository(db.tripDao()),
//            stopTimeRepo
//        )
//
//        // parse stops and load station names for suggestions
//        lifecycleScope.launch {
//            withContext(Dispatchers.IO) {
//                parser.parseStops()
//            }
//            val stations = stationRepo.searchStations("").first()
//            val names = stations.map { it.stopName }
//            val adapter = ArrayAdapter(
//                this@SearchActivity,
//                android.R.layout.simple_dropdown_item_1line,
//                names
//            )
//            acStart.setAdapter(adapter)
//            acEnd.setAdapter(adapter)
//        }
//
//        // handle Search Routes click
//        btnSearchRoutes.setOnClickListener {
//            val startName = acStart.text.toString()
//            val endName = acEnd.text.toString()
//            lifecycleScope.launch {
//                val stationsAll = stationRepo.searchStations("").first()
//                val start = stationsAll.find { it.stopName == startName }
//                val end = stationsAll.find { it.stopName == endName }
//                if (start == null || end == null) {
//                    Toast.makeText(
//                        this@SearchActivity,
//                        "Please select valid stations",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@launch
//                }
//
//                // fetch routes for each
//                val startRoutes = withContext(Dispatchers.IO) {
//                    stopTimeRepo.getRouteIdsForStop(start.stopId)
//                }
//                val endRoutes = withContext(Dispatchers.IO) {
//                    stopTimeRepo.getRouteIdsForStop(end.stopId)
//                }
//                val common = startRoutes.intersect(endRoutes.toSet())
//                val display = if (common.isNotEmpty()) common.toTypedArray()
//                else arrayOf("No direct routes found")
//
//                AlertDialog.Builder(this@SearchActivity)
//                    .setTitle("Routes between ${start.stopName} and ${end.stopName}")
//                    .setItems(display, null)
//                    .show()
//            }
//        }
//    }
//}


/////////////////////////////////////
package com.example.delhitravelapp.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.delhitravelapp.RoutesActivity
import com.example.delhitravelapp.data.AppDatabase
import com.example.delhitravelapp.data.StationEntity
import com.example.delhitravelapp.data.StationRepository
import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DelhiTravelAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SearchScreen()
                }
            }
        }
    }
}

@Composable
fun SearchScreen() {
    val context = LocalContext.current
    val stationRepo = remember {
        StationRepository(AppDatabase.getInstance(context).stationDao())
    }

    var startQuery by remember { mutableStateOf("") }
    var endQuery by remember { mutableStateOf("") }
    var startSuggestions by remember { mutableStateOf<List<StationEntity>>(emptyList()) }
    var endSuggestions by remember { mutableStateOf<List<StationEntity>>(emptyList()) }
    var isStartSuggestionsVisible by remember { mutableStateOf(true) }
    var isEndSuggestionsVisible by remember { mutableStateOf(true) }
    var selectedStartStation by remember { mutableStateOf<StationEntity?>(null) }
    var selectedEndStation by remember { mutableStateOf<StationEntity?>(null) }

    // Fetch suggestions for start station
    LaunchedEffect(startQuery) {
        if (startQuery.isNotEmpty()) {
            stationRepo.searchStations(startQuery).collectLatest { stations ->
                startSuggestions = stations.take(5)
                Log.d("SearchActivity", "Start suggestions updated: ${startSuggestions.map { it.stopName }}")
                if (stations.none { it.stopName.equals(startQuery, ignoreCase = true) }) { ////////////////////////////////////// 3 lines added
                    selectedStartStation = null
                }
            }
        } else {
            startSuggestions = emptyList()
            selectedStartStation = null //////////////////////////////////////added
            Log.d("SearchActivity", "Start suggestions cleared")
        }
    }

    // Fetch suggestions for end station
    LaunchedEffect(endQuery) {
        if (endQuery.isNotEmpty()) {
            stationRepo.searchStations(endQuery).collectLatest { stations ->
                endSuggestions = stations.take(5)
                Log.d("SearchActivity", "End suggestions updated: ${endSuggestions.map { it.stopName }}")
                if (stations.none { it.stopName.equals(endQuery, ignoreCase = true) }) {///////////////////////////////////////////3 lines
                    selectedEndStation = null
                }
            }
        } else {
            endSuggestions = emptyList()
            selectedEndStation = null ////////////////////////
            Log.d("SearchActivity", "End suggestions cleared")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Start Station selection
        Text(
            text = "Select Start Station",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = startQuery,
            onValueChange = {
                startQuery = it
                isStartSuggestionsVisible = true
                selectedStartStation = null // Reset selection while typing
                Log.d("SearchActivity", "Start query changed: $startQuery")
            },
            placeholder = { Text("Start typing a station...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (isStartSuggestionsVisible && startQuery.isNotEmpty() && startSuggestions.isNotEmpty()) {
            LazyColumn {
                items(startSuggestions) { station ->
                    Text(
                        text = station.stopName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedStartStation = station
                                startQuery = station.stopName
                                isStartSuggestionsVisible = false
                                Log.d("SearchActivity", "Start station selected: ${station.stopName}")
                            }
                            .padding(8.dp)
                    )
                }
            }
        } else if (startQuery.isNotEmpty() && startSuggestions.isEmpty()) {
            Text("No results found", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // End Station selection
        Text(
            text = "Select End Station",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = endQuery,
            onValueChange = {
                endQuery = it
                isEndSuggestionsVisible = true
                selectedEndStation = null // Reset selection while typing
                Log.d("SearchActivity", "End query changed: $endQuery")
            },
            placeholder = { Text("Start typing a station...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (isEndSuggestionsVisible && endQuery.isNotEmpty() && endSuggestions.isNotEmpty()) {
            LazyColumn {
                items(endSuggestions) { station ->
                    Text(
                        text = station.stopName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedEndStation = station
                                endQuery = station.stopName
                                isEndSuggestionsVisible = false
                                Log.d("SearchActivity", "End station selected: ${station.stopName}")
                            }
                            .padding(8.dp)
                    )
                }
            }
        } else if (endQuery.isNotEmpty() && endSuggestions.isEmpty()) {
            Text("No results found", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Route option buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    Log.d("SearchActivity", "Shortest Route clicked. Start: ${selectedStartStation?.stopName}, End: ${selectedEndStation?.stopName}")
                    if (selectedStartStation != null && selectedEndStation != null) {
                        val intent = Intent(context, RoutesActivity::class.java).apply {
                            putExtra("START_STATION_ID", selectedStartStation?.stopId)
                            putExtra("START_STATION_NAME", selectedStartStation?.stopName)
                            putExtra("DEST_STATION_ID", selectedEndStation?.stopId)
                            putExtra("DEST_STATION_NAME", selectedEndStation?.stopName)
                            putExtra("ROUTE_OPTION", "SHORTEST")
                        }
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Please select both stations", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "Shortest Route",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            Button(
                onClick = {
                    Log.d("SearchActivity", "Min Interchange clicked. Start: ${selectedStartStation?.stopName}, End: ${selectedEndStation?.stopName}")
                    if (selectedStartStation != null && selectedEndStation != null) {
                        val intent = Intent(context, RoutesActivity::class.java).apply {
                            putExtra("START_STATION_ID", selectedStartStation?.stopId)
                            putExtra("START_STATION_NAME", selectedStartStation?.stopName)
                            putExtra("DEST_STATION_ID", selectedEndStation?.stopId)
                            putExtra("DEST_STATION_NAME", selectedEndStation?.stopName)
                            putExtra("ROUTE_OPTION", "MIN_INTERCHANGE")
                        }
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Please select both stations", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = "Min Interchange",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}