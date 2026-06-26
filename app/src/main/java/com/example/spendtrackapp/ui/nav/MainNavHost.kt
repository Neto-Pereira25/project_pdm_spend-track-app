package com.example.spendtrackapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.spendtrackapp.ui.ExpenseDetailsPage
import com.example.spendtrackapp.ui.HomePage
import com.example.spendtrackapp.ui.ListPage
import com.example.spendtrackapp.ui.MapPage
import com.example.spendtrackapp.viewmodel.MainViewModel

@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomePage(
                modifier = modifier,
                viewModel = viewModel
            )
        }

        composable(Routes.LIST) {
            ListPage(
                modifier = modifier,
                viewModel = viewModel,
                onExpenseClick = { expenseId ->
                    navController.navigate("${Routes.DETAIL}/$expenseId")
                }
            )
        }

        composable(Routes.MAP) {
            MapPage(
                modifier = modifier,
                viewModel = viewModel
            )
        }

        composable(
            route = "${Routes.DETAIL}/{expenseId}",
            arguments = listOf(
                navArgument("expenseId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId")
            val expense = if (expenseId != null) viewModel.findById(expenseId) else null

            ExpenseDetailsPage(
                expense = expense,
                onDelete = {
                    if (expense != null) {
                        viewModel.remove(expense)
                    }
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
    }
}
