package seeker.doesoh.tracker.data.model

import androidx.compose.ui.graphics.vector.ImageVector
import seeker.doesoh.tracker.presentation.Screen

data class MenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector,
    val route: String
)