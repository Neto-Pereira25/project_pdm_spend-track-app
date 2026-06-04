package com.example.spendtrackapp.ui.nav


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val HOME = "home"
    const val LIST = "list"
    const val MAP = "map"
}

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Home : BottomNavItem("Dashboard", Icons.Default.Home, Routes.HOME)
    object List : BottomNavItem("Histórico", Icons.Default.List, Routes.LIST)
    object Map : BottomNavItem("Mapa", Icons.Default.LocationOn, Routes.MAP)
}
