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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendtrackapp.viewmodel.MainViewModel

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
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
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Dashboard",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Total de gastos: ${viewModel.totalItems()}",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Total gasto: R$ %.2f".format(totalSpent),
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Meta mensal: R$ %.2f".format(monthlyGoal),
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))


        val remainingText = if (remaining >= 0.0) {
            "Restante: R$ %.2f".format(remaining)
        } else {
            "Meta ultrapassada em: R$ %.2f".format(kotlin.math.abs(remaining))
        }

        Text(
            text = remainingText,
            text = remainingText,
            fontSize = 18.sp
        )


        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        GoalAlertCard(
            message = viewModel.goalStatusMessage(),
            level = viewModel.goalStatusLevel()
        )
    }
}
