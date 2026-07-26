package com.example.spendtrackapp.ui.nav

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.spendtrackapp.model.Expense
import com.example.spendtrackapp.ui.ExpenseDetailsPage
import com.example.spendtrackapp.ui.GoalsPage
import com.example.spendtrackapp.ui.HomePage
import com.example.spendtrackapp.ui.ListPage
import com.example.spendtrackapp.ui.MapPage
import com.example.spendtrackapp.ui.SettingsPage
import com.example.spendtrackapp.ui.SettingsPage
import com.example.spendtrackapp.viewmodel.MainViewModel
import com.google.android.gms.maps.model.LatLng


@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onMapClick: (LatLng) -> Unit,
    onChangeExpenseLocation: (Expense) -> Unit,
    onViewExpenseLocation: (Expense) -> Unit,
    focusedLat: Double?,
    focusedLng: Double?
) {
    val activity = LocalActivity.current

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
                viewModel = viewModel,
                onMapClick = onMapClick,
                focusedLat = focusedLat,
                focusedLng = focusedLng
            )
        }

        composable(Routes.GOALS) {
            GoalsPage(
                modifier = modifier,
                viewModel = viewModel
            )
        }

        composable(Routes.SETTINGS) {
            SettingsPage(
                modifier = modifier
            )
        }

        composable(
            route = "${Routes.DETAIL}/{expenseId}",
            arguments = listOf(
                navArgument("expenseId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId")

            val expense = if (expenseId != null) {
                viewModel.findById(expenseId)
            } else {
                null
            }

            ExpenseDetailsPage(
                expense = expense,
                onDelete = {
                    if (expense != null) {
                        viewModel.remove(
                            expense = expense,
                            onSuccess = {
                                Toast.makeText(
                                    activity,
                                    "Gasto excluído com sucesso",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.popBackStack()
                            },
                            onFailure = { ex ->
                                Toast.makeText(
                                    activity,
                                    "Erro ao excluir gasto: ${ex.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                },
                onUpdate = { description, amount, category ->
                    if (expense != null) {
                        viewModel.update(
                            expense = expense,
                            description = description,
                            amount = amount,
                            category = category,
                            onSuccess = {
                                Toast.makeText(
                                    activity,
                                    "Gasto atualizado com sucesso",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            onFailure = { ex ->
                                Toast.makeText(
                                    activity,
                                    "Erro ao atualizar gasto: ${ex.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                },
                onChangeLocation = { selectedExpense ->
                    onChangeExpenseLocation(selectedExpense)
                    navController.navigate(Routes.MAP) {
                        launchSingleTop = true
                    }
                },
                onViewLocation = { selectedExpense ->
                    onViewExpenseLocation(selectedExpense)

                    navController.navigate(Routes.MAP) {
                        launchSingleTop = true
                    }
                },
                modifier = modifier
            )
        }
    }
}
