package com.example.spendtrackapp.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendtrackapp.ui.components.CategoryBarChart
import com.example.spendtrackapp.ui.components.InfoCard
import com.example.spendtrackapp.ui.components.SectionTitle
import com.example.spendtrackapp.ui.components.StatCard
import com.example.spendtrackapp.viewmodel.MainViewModel

@Composable
fun HomePage(
    modifier: Modifier = Modifier, viewModel: MainViewModel
) {

    val totalSpent = viewModel.totalSpent()

    val monthlyGoal = viewModel.monthlyGoal

    val remaining = monthlyGoal - totalSpent

    val progress = if (monthlyGoal > 0.0) {
        viewModel.goalUsagePercent().toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        SectionTitle(
            text = "Dashboard"
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Total de gastos: ${viewModel.totalItems()}", fontSize = 18.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        StatCard(
            title = "Total gasto", value = "R$ %.2f".format(totalSpent)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        StatCard(
            title = "Meta mensal", value = "R$ %.2f".format(monthlyGoal)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val remainingText = if (remaining >= 0.0) {
            "Restante: R$ %.2f".format(
                remaining
            )
        } else {
            "Meta ultrapassada em: R$ %.2f".format(
                kotlin.math.abs(
                    remaining
                )
            )
        }

        StatCard(
            title = "Saldo disponível", value = remainingText
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        LinearProgressIndicator(
            progress = { progress }, modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        GoalAlertCard(
            message = viewModel.goalStatusMessage(), level = viewModel.goalStatusLevel()
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        SectionTitle(
            text = "Análise de gastos"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "Categorias cadastradas: ${viewModel.totalCategories()}", fontSize = 18.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Ticket médio: R$ %.2f".format(
                viewModel.averageExpenseValue()
            ), fontSize = 18.sp
        )

        viewModel.topCategory()?.let { (category, amount) ->

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            InfoCard(
                title = "Categoria líder",
                value = category,
                subtitle = "R$ %.2f • %.1f%% dos gastos".format(
                        amount, viewModel.topCategoryPercentage()
                    )
            )
            /*
                        Text(
                            text = "Categoria com maior gasto",
                            fontSize = 20.sp
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = category,
                            fontSize = 18.sp
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "R$ %.2f".format(
                                amount
                            ),
                            fontSize = 18.sp
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "%.1f%% dos gastos".format(
                                viewModel.topCategoryPercentage()
                            ),
                            fontSize = 16.sp
                        )
             */
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Distribuição por categoria", fontSize = 22.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        CategoryBarChart(
            categories = viewModel.expensesByCategory()
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}
