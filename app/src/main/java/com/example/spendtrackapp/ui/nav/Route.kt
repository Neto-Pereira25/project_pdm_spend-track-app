package com.example.spendtrackapp.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val HOME = "home"
    const val LIST = "list"
    const val MAP = "map"
    const val DETAIL = "detail"
    const val GOALS = "goals"
    const val SETTINGS = "settings"
}

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Home : BottomNavItem(
        title = "Dashboard",
        icon = Icons.Default.Home,
        route = Routes.HOME
    )

    object List : BottomNavItem(
        title = "Histórico",
        icon = Icons.Default.List,
        route = Routes.LIST
    )

    object Map : BottomNavItem(
        title = "Mapa",
        icon = Icons.Default.LocationOn,
        route = Routes.MAP
    )


    object Goals : BottomNavItem(
        title = "Metas",
        icon = Icons.Default.Flag,
        route = Routes.GOALS
    )
}
