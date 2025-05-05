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


package com.example.delhitravelapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.delhitravelapp.data.AppDatabase
import com.example.delhitravelapp.data.RouteRepository
import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme

class RoutesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the passed data (start and end station names)
        val startStation = intent.getStringExtra("start_station")
        val endStation = intent.getStringExtra("end_station")

        // Check if startStation or endStation are null
        if (startStation == null || endStation == null) {
            Toast.makeText(this, "Missing station data. Please go back and select stations.", Toast.LENGTH_LONG).show()
            finish() // Close the activity if no data is found
            return
        }

        // Inflate Compose UI
        setContent {
            DelhiTravelAppTheme {
                Surface(modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.background) {
                    ShowRoutesScreen(startStation, endStation)
                }
            }
        }
    }
}


@Composable
fun ShowRoutesScreen(startStation: String, endStation: String) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Start Station: $startStation")
        Text(text = "End Station: $endStation")
        Spacer(modifier = Modifier.height(16.dp))

        // Dummy Routes (3 paths)
        Text(text = "Path 1: $startStation -> Station X -> Station Y -> $endStation")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Path 2: $startStation -> Station A -> Station B -> $endStation")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Path 3: $startStation -> Station M -> Station N -> $endStation")
        Spacer(modifier = Modifier.height(16.dp))

        // Back button to return to the previous screen
        Button(onClick = { /* Navigate back to HomeActivity */ }) {
            Text(text = "Back")
        }
    }
}
