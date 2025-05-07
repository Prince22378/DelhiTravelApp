package com.example.delhitravelapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalMaterial3Api::class)
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

    Column {
        // Top Bar with Back Button and Title
        TopAppBar(
            title = {
                Text(
                    text = "Route from $startStationName to $destStationName",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    context.startActivity(Intent(context, SearchActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        if (startStationName == "Unknown" || destStationName == "Unknown") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Missing Station data. Please go back & select station.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        context.startActivity(Intent(context, SearchActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        })
                    }
                ) {
                    Text("Go Back")
                }
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                // Map View at the top
                Image(
                    painter = painterResource(R.drawable.dmrc_map),
                    contentDescription = "Delhi Metro Map",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Route Details ($routeOption)",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                routeState?.let { path ->
                    if (path.stations.isEmpty()) {
                        Text(
                            text = "No route found between $startStationName and $destStationName",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        // Calculate interchanges
                        var interchangeCount = 0
                        for (i in 1 until path.lines.size) {
                            if (path.lines[i] != path.lines[i - 1]) interchangeCount++
                        }

                        // Calculate total fare: Rs. 10 per station (based on hops)
                        val totalFare = (path.stations.size - 1) * 10

                        LazyColumn {
                            itemsIndexed(path.stations) { index, stationId ->
                                val station = viewModel.getStation(stationId)
                                val line = if (index < path.lines.size) path.lines[index] else ""
                                val arrivalTime = if (index < path.arrivalTimes.size) path.arrivalTimes[index] else "N/A"
                                // Cumulative fare up to this station
                                val cumulativeFare = index * 10

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
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
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = station?.stopName ?: stationId,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = "Line: $line",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "Arrival: $arrivalTime",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray,
                                                fontSize = 14.sp
                                            )
                                            if (station?.interchangeAvailable == 1) {
                                                Text(
                                                    text = "Interchange: ${station.interchangeDesc}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.DarkGray,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Fare: Rs. $cumulativeFare",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Summary Section
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Journey Summary",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Total Stations: ${path.stations.size}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Interchanges: $interchangeCount",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Total Fare: Rs. $totalFare",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                        Text(text = "Loading route...", modifier = Modifier.padding(top = 16.dp))
                    }
                }
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
        routeRepo = com.example.delhitravelapp.data.RouteRepository(db.routeDao())
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