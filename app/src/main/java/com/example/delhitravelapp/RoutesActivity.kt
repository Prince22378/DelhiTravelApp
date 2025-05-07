//package com.example.delhitravelapp
////
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import com.example.delhitravelapp.data.AppDatabase
//import com.example.delhitravelapp.data.RouteRepository
//import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme
////
//class RoutesActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // Read the two station IDs passed via Intent
//        val startId = intent.getStringExtra("start_id")
//        val endId   = intent.getStringExtra("end_id")
//
//        setContent {
//            DelhiTravelAppTheme {
//                Surface(color = MaterialTheme.colorScheme.background) {
//                    RoutesScreen(startId, endId)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun RoutesScreen(startId: String?, endId: String?) {
//    // local context to build the repository
//    val context = LocalContext.current
//
//    if (startId.isNullOrEmpty() || endId.isNullOrEmpty()) {
//        Text(
//            text = "Please select both start and end stations.",
//            modifier = Modifier.padding(16.dp),
//            style = MaterialTheme.typography.bodyLarge
//        )
//        return
//    }
//
//    // Build repository and collect matching routes
//    val repo = remember {
//        RouteRepository(AppDatabase.getInstance(context).routeDao())
//    }
//    val routes by repo.getRoutesBetween(startId, endId)
//        .collectAsState(initial = emptyList())
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        Text(
//            text = "Routes from $startId → $endId:",
//            style = MaterialTheme.typography.titleMedium,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )
//
//        if (routes.isEmpty()) {
//            Text(
//                text = "No direct route found.",
//                style = MaterialTheme.typography.bodyMedium
//            )
//        } else {
//            routes.forEach { route ->
//                Text(
//                    text = "• ${route.routeShortName} — ${route.routeLongName}",
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier.padding(vertical = 4.dp)
//                )
//            }
//        }
//    }
//}


//package com.example.delhitravelapp
//
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import com.example.delhitravelapp.data.AppDatabase
//import com.example.delhitravelapp.data.RouteRepository
//import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme
//
//class RoutesActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Get the passed data (start and end station names)
//        val startStation = intent.getStringExtra("start_station")
//        val endStation = intent.getStringExtra("end_station")
//
//        // Check if startStation or endStation are null
//        if (startStation == null || endStation == null) {
//            Toast.makeText(this, "Missing station data. Please go back and select stations.", Toast.LENGTH_LONG).show()
//            finish() // Close the activity if no data is found
//            return
//        }
//
//        // Inflate Compose UI
//        setContent {
//            DelhiTravelAppTheme {
//                Surface(modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.background) {
//                    ShowRoutesScreen(startStation, endStation)
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//fun ShowRoutesScreen(startStation: String, endStation: String) {
//    Column(
//        modifier = Modifier.padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(text = "Start Station: $startStation")
//        Text(text = "End Station: $endStation")
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Dummy Routes (3 paths)
//        Text(text = "Path 1: $startStation -> Station X -> Station Y -> $endStation")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "Path 2: $startStation -> Station A -> Station B -> $endStation")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "Path 3: $startStation -> Station M -> Station N -> $endStation")
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Back button to return to the previous screen
//        Button(onClick = { /* Navigate back to HomeActivity */ }) {
//            Text(text = "Back")
//        }
//    }
//}

////////////////////////////
package com.example.delhitravelapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.delhitravelapp.data.AppDatabase
import com.example.delhitravelapp.data.RouteCalculation
import com.example.delhitravelapp.data.StationEntity
import com.example.delhitravelapp.data.StationRepository
import com.example.delhitravelapp.search.SearchActivity
import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RoutesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(
            this,
            RoutesViewModelFactory(this)
        )[RoutesViewModel::class.java]

        setContent {
            DelhiTravelAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RoutesScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun RoutesScreen(viewModel: RoutesViewModel) {
    val routeState by viewModel.routeState.collectAsState()
    val startStationName = viewModel.getStartStationName()
    val destStationName = viewModel.getDestStationName()
    val routeOption = viewModel.getRouteOption()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.findRoute()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (startStationName == "Unknown" || destStationName == "Unknown") {
            Text(
                text = "Missing Station data. Please go back & select station.",
                color = MaterialTheme.colorScheme.error
            )
            Button(
                onClick = {
                    context.startActivity(Intent(context, SearchActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Go Back")
            }
        } else {
            Text(
                text = "Route from $startStationName to $destStationName ($routeOption)",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            routeState?.let { path ->
                if (path.stations.isEmpty()) {
                    Text(
                        text = "No route found between $startStationName and $destStationName",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    LazyColumn {
                        itemsIndexed(path.stations) { index, stationId ->
                            val station = viewModel.getStation(stationId)
                            val line = if (index < path.lines.size) path.lines[index] else ""
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(
                                                color = when (line) {
                                                    "Yellow" -> Color.Yellow
                                                    "Red" -> Color.Red
                                                    "Blue" -> Color.Blue
                                                    "Pink" -> Color(0xFFFFC1CC)
                                                    "Green" -> Color.Green
                                                    "Magenta" -> Color.Magenta
                                                    "Violet" -> Color(0xFF800080)
                                                    "Airport Express" -> Color(0xFFFFA500)
                                                    "Rapid Metro" -> Color(0xFF00CED1)
                                                    "Grey" -> Color.Gray
                                                    else -> Color.Gray
                                                },
                                                shape = CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = station?.stopName ?: stationId,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "Line: $line",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                        if (station?.interchangeAvailable == 1) {
                                            Text(
                                                text = "Interchange: ${station.interchangeDesc}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Total Stations: ${path.stations.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } ?: run {
                Text(text = "Loading route...")
            }
        }
    }
}

class RoutesViewModel(
    private val context: RoutesActivity
) : ViewModel() {
    private val db = AppDatabase.getInstance(context)
    private val routeCalculation = RouteCalculation(
        stationDao = db.stationDao(),
        stopTimeDao = db.stopTimeDao(),
        tripRepo = com.example.delhitravelapp.data.TripRepository(db.tripDao()),
        routeRepo = com.example.delhitravelapp.data.RouteRepository(db.routeDao()) // Add RouteRepository
    )
    private val stationRepo = StationRepository(db.stationDao())

    private val _routeState = MutableStateFlow<RouteCalculation.Path?>(null)
    val routeState: StateFlow<RouteCalculation.Path?> = _routeState.asStateFlow()

    private val startStationId: String? = context.intent.getStringExtra("START_STATION_ID")
    private val startStationName: String? = context.intent.getStringExtra("START_STATION_NAME")
    private val destStationId: String? = context.intent.getStringExtra("DEST_STATION_ID")
    private val destStationName: String? = context.intent.getStringExtra("DEST_STATION_NAME")
    private val routeOption: String? = context.intent.getStringExtra("ROUTE_OPTION")

    fun getStartStationName(): String = startStationName ?: "Unknown"
    fun getDestStationName(): String = destStationName ?: "Unknown"
    fun getRouteOption(): String = when (routeOption) {
        "SHORTEST" -> "Shortest Route"
        "MIN_INTERCHANGE" -> "Minimum Interchange Route"
        else -> "Unknown"
    }

    fun findRoute() {
        viewModelScope.launch {
            if (startStationId != null && destStationId != null) {
                val path = when (routeOption) {
                    "SHORTEST" -> routeCalculation.findShortestRoute(startStationId, destStationId)
                    "MIN_INTERCHANGE" -> routeCalculation.findMinimumInterchangeRoute(startStationId, destStationId)
                    else -> routeCalculation.findShortestRoute(startStationId, destStationId)
                }
                _routeState.value = path
            }
        }
    }

    fun getStation(stationId: String): StationEntity? {
        return runBlocking { stationRepo.getById(stationId) }
    }
}

class RoutesViewModelFactory(
    private val context: RoutesActivity
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoutesViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
