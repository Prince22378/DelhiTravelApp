package com.example.delhitravelapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DelhiTravelAppTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.welcome))
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DelhiTravelAppTheme {
        HomeScreen()
    }
}
