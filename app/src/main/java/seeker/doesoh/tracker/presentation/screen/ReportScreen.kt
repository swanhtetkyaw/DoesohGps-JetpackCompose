package seeker.doesoh.tracker.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import seeker.doesoh.tracker.presentation.MainViewModel

@Composable
fun ReportScreen(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Report Screen", fontSize = 30.sp, fontWeight = FontWeight.Bold)
    }
}