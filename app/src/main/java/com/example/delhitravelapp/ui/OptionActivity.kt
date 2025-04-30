////package com.example.delhitravelapp.ui
////
////import android.content.Intent
////import android.os.Bundle
////import androidx.activity.ComponentActivity
////import androidx.activity.compose.setContent
////import androidx.compose.animation.core.animateFloatAsState
////import androidx.compose.foundation.Image
////import androidx.compose.foundation.background
////import androidx.compose.foundation.clickable
////import androidx.compose.foundation.interaction.MutableInteractionSource
////import androidx.compose.foundation.interaction.collectIsPressedAsState
////import androidx.compose.foundation.layout.Arrangement
////import androidx.compose.foundation.layout.Box
////import androidx.compose.foundation.layout.Column
////import androidx.compose.foundation.layout.Row
////import androidx.compose.foundation.layout.Spacer
////import androidx.compose.foundation.layout.fillMaxSize
////import androidx.compose.foundation.layout.fillMaxWidth
////import androidx.compose.foundation.layout.height
////import androidx.compose.foundation.layout.padding
////import androidx.compose.foundation.layout.size
////import androidx.compose.foundation.layout.width
////import androidx.compose.foundation.shape.RoundedCornerShape
////import androidx.compose.material3.Card
////import androidx.compose.material3.CardDefaults
////import androidx.compose.material3.MaterialTheme
////import androidx.compose.material3.Text
////import androidx.compose.runtime.Composable
////import androidx.compose.runtime.getValue
////import androidx.compose.runtime.remember
////import androidx.compose.ui.Alignment
////import androidx.compose.ui.Modifier
////import androidx.compose.ui.graphics.Color
////import androidx.compose.ui.graphics.graphicsLayer
////import androidx.compose.ui.res.painterResource
////import androidx.compose.ui.text.font.FontWeight
////import androidx.compose.ui.text.style.TextAlign
////import androidx.compose.ui.unit.dp
////import androidx.compose.ui.unit.sp
////import com.example.delhitravelapp.HomeActivity
////import com.example.delhitravelapp.R
////import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme
////
////class OptionActivity : ComponentActivity() {
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContent {
////            DelhiTravelAppTheme {
////                OptionScreen(
////                    onSelectNormal = {
////                        startActivity(Intent(this, HomeActivity::class.java))
////                        finish()
////                    },
////                    onSelectDisabled = {
////                        // TODO: your disabled-person flow
////                    }
////                )
////            }
////        }
////    }
////}
////
////@Composable
////fun OptionScreen(
////    onSelectNormal: () -> Unit,
////    onSelectDisabled: () -> Unit
////) {
////    Column(
////        modifier = Modifier
////            .fillMaxSize()
////            .background(Color(0xFFF5F5F5))
////            .padding(16.dp),
////        horizontalAlignment = Alignment.CenterHorizontally
////    ) {
////        Spacer(Modifier.height(24.dp))
////        Text("Option Page", style = MaterialTheme.typography.headlineMedium)
////        Spacer(Modifier.height(16.dp))
////        Text("Choose option", style = MaterialTheme.typography.bodyLarge)
////        Spacer(Modifier.height(32.dp))
////
////        Row(
////            horizontalArrangement = Arrangement.spacedBy(32.dp),
////            modifier = Modifier.fillMaxWidth(),
////            verticalAlignment = Alignment.CenterVertically
////        ) {
////            OptionItem(
////                icon = R.drawable.normal_person1,
////                label = "Normal People",
////                onClick = onSelectNormal
////            )
////            OptionItem(
////                icon = R.drawable.disabled_person,
////                label = "Disabled Person",
////                onClick = onSelectDisabled
////            )
////        }
////    }
////}
////
////@Composable
////fun OptionItem(
////    icon: Int,
////    label: String,
////    onClick: () -> Unit
////) {
////    val interactionSource = remember { MutableInteractionSource() }
////    val isPressed by interactionSource.collectIsPressedAsState()
////    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)
////    val cardSize = 140.dp
////
////    Column(
////        horizontalAlignment = Alignment.CenterHorizontally,
////        modifier = Modifier
////            .graphicsLayer { scaleX = scale; scaleY = scale }
////            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
////    ) {
////        Card(
////            modifier = Modifier.size(cardSize),
////            shape = RoundedCornerShape(16.dp),
////            colors = CardDefaults.cardColors(containerColor = Color.White),
////            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
////        ) {
////            Box(
////                Modifier.fillMaxSize(),
////                contentAlignment = Alignment.Center
////            ) {
////                Image(
////                    painter = painterResource(icon),
////                    contentDescription = label,
////                    modifier = Modifier.size(80.dp)
////                )
////            }
////        }
////
////        Spacer(Modifier.height(12.dp))
////
////        Text(
////            text = label,
////            style = MaterialTheme.typography.bodyMedium.copy(
////                fontSize = 16.sp,
////                fontWeight = FontWeight.SemiBold
////            ),
////            color = MaterialTheme.colorScheme.onBackground,
////            modifier = Modifier
////                .width(cardSize)
////                .padding(horizontal = 4.dp),
////            textAlign = TextAlign.Center
////        )
////    }
////}
//
//
//
//
//package com.example.delhitravelapp.ui
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.interaction.collectIsPressedAsState
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.delhitravelapp.HomeActivity
//import com.example.delhitravelapp.R
//import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme
//
//class OptionActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            DelhiTravelAppTheme {
//                OptionScreen(
//                    onSelectNormal = {
//                        startActivity(Intent(this, HomeActivity::class.java))
//                        finish()
//                    },
//                    onSelectDisabled = {
//                        // TODO: your disabled-person flow
//                    }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun OptionScreen(
//    onSelectNormal: () -> Unit,
//    onSelectDisabled: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF5F5F5))
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Spacer(Modifier.height(16.dp))
//
//        // Polished Title
//        Text(
//            text = "Option Page",
//            style = MaterialTheme.typography.headlineSmall.copy(
//                fontWeight = FontWeight.Bold,
//                letterSpacing = 1.2.sp
//            ),
//            color = MaterialTheme.colorScheme.primary,
//            textAlign = TextAlign.Center,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp)
//        )
//
//        // Polished Subtitle
//        Text(
//            text = "Choose an option below",
//            style = MaterialTheme.typography.titleMedium.copy(
//                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
//            ),
//            color = MaterialTheme.colorScheme.onSurfaceVariant,
//            textAlign = TextAlign.Center,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 32.dp)
//        )
//
//        // Centered Row of Options
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            OptionItem(
//                icon = R.drawable.normal_person1,
//                label = "Normal\nPeople",
//                onClick = onSelectNormal
//            )
//            Spacer(Modifier.width(32.dp))
//            OptionItem(
//                icon = R.drawable.disabled_person,
//                label = "Disabled\nPerson",
//                onClick = onSelectDisabled
//            )
//        }
//    }
//}
//
//@Composable
//fun OptionItem(
//    icon: Int,
//    label: String,
//    onClick: () -> Unit
//) {
//    val interactionSource = remember { MutableInteractionSource() }
//    val isPressed by interactionSource.collectIsPressedAsState()
//    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)
//    val cardSize = 140.dp
//
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .graphicsLayer { scaleX = scale; scaleY = scale }
//            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
//    ) {
//        Card(
//            modifier = Modifier.size(cardSize),
//            shape = RoundedCornerShape(16.dp),
//            colors = CardDefaults.cardColors(containerColor = Color.White),
//            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
//        ) {
//            Box(
//                Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Image(
//                    painter = painterResource(icon),
//                    contentDescription = label,
//                    modifier = Modifier.size(80.dp)
//                )
//            }
//        }
//
//        Spacer(Modifier.height(12.dp))
//
//        Text(
//            text = label,
//            style = MaterialTheme.typography.bodyMedium.copy(
//                fontSize = 16.sp,
//                fontWeight = FontWeight.SemiBold
//            ),
//            color = MaterialTheme.colorScheme.onBackground,
//            modifier = Modifier
//                .width(cardSize),
//            textAlign = TextAlign.Center
//        )
//    }
//}


package com.example.delhitravelapp.ui

// ← import your new screen here:
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.delhitravelapp.R
import com.example.delhitravelapp.ui.theme.DelhiTravelAppTheme

class OptionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DelhiTravelAppTheme {
                OptionScreen(
                    onSelectNormal = {
                        // launch the language selector instead of HomeActivity
                        startActivity(Intent(this, LanguageSelectionActivity::class.java))
                        finish()
                    },
                    onSelectDisabled = {
                        // TODO: launch your disabled-person flow
                    }
                )
            }
        }
    }
}

@Composable
fun OptionScreen(
    onSelectNormal: () -> Unit,
    onSelectDisabled: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Option Page",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Choose an option below",
            style = MaterialTheme.typography.titleMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OptionItem(
                icon = R.drawable.normal_person1,
                label = "Normal\nPeople",
                onClick = onSelectNormal
            )
            Spacer(Modifier.width(32.dp))
            OptionItem(
                icon = R.drawable.disabled_person,
                label = "Disabled\nPerson",
                onClick = onSelectDisabled
            )
        }
    }
}

@Composable
fun OptionItem(icon: Int, label: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)
    val cardSize = 140.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
        Card(
            modifier = Modifier.size(cardSize),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    modifier = Modifier.size(80.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.width(cardSize),
            textAlign = TextAlign.Center
        )
    }
}
