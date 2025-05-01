package com.example.delhitravelapp

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme

class HistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DelhiTravelAppTheme {
                HistoryScreen()
            }
        }
    }
}

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    var historyList by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("travel_history", Context.MODE_PRIVATE)
        historyList = prefs.getStringSet("history_set", emptySet())?.toList() ?: emptyList()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(R.string.travel_history),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (historyList.isEmpty()) {
            Text(text = stringResource(R.string.no_history))
        } else {
            LazyColumn {
                items(historyList) { item ->
                    Text(text = item, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}