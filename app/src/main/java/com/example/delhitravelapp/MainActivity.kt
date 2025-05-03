package com.example.delhitravelapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.delhitravelapp.data.DatabaseModule
import com.example.delhitravelapp.data.GtfsParser
import com.example.delhitravelapp.ui.OptionActivity
import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) Launch GTFS parsing on IO immediately
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                GtfsParser(
                    this@MainActivity,
                    DatabaseModule.provideStationRepo(this@MainActivity),
                    DatabaseModule.provideRouteRepo(this@MainActivity),
                    DatabaseModule.provideTripRepo(this@MainActivity),
                    DatabaseModule.provideStopTimeRepo(this@MainActivity)
                ).parseAll()
            }
        }

        // 2) Show splash UI
        setContent {
            DelhiTravelAppTheme {
                SplashScreen()
            }
        }

        // 3) Navigate after 3 seconds
        lifecycleScope.launch {
            delay(3000)
            startActivity(Intent(this@MainActivity, OptionActivity::class.java))
            finish()
        }
    }
}

@Composable
fun SplashScreen() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(durationMillis = 200)),
            exit    = fadeOut(tween(durationMillis = 200))
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MetroLogo()
                Spacer(Modifier.height(32.dp))
                Text(
                    text = "Delhi Travel App",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Navigating with ease at your fingertips",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun MetroLogo() {
    Box(
        Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(80.dp)) {
            // white rounded square
            drawRoundRect(
                color        = Color.White,
                topLeft      = Offset.Zero,
                size         = size,
                cornerRadius = CornerRadius(8.dp.toPx())
            )
            // red vertical
            drawLine(
                color       = Color.Red,
                start       = center.copy(y = 20f),
                end         = center.copy(y = size.height - 20f),
                strokeWidth = 12f
            )
            // red horizontal
            drawLine(
                color       = Color.Red,
                start       = center.copy(x = 20f),
                end         = center.copy(x = size.width - 20f),
                strokeWidth = 12f
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    DelhiTravelAppTheme {
        SplashScreen()
    }
}
