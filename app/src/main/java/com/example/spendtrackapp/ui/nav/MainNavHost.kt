package com.example.spendtrackapp.ui.nav


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.spendtrackapp.ui.HomePage
import com.example.spendtrackapp.ui.ListPage
import com.example.spendtrackapp.ui.MapPage

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomePage(modifier = modifier)
        }
        composable(Routes.LIST) {
            ListPage(modifier = modifier)
        }
        composable(Routes.MAP) {
            MapPage(modifier = modifier)
        }
    }
}
