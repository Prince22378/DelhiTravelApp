//package com.example.delhitravelapp
//
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
//
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
