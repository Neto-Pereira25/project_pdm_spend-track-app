package com.example.spendtrackapp.ui

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendtrackapp.viewmodel.MainViewModel

@Composable
fun GoalsPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val activity = LocalActivity.current

    var goalText by remember(viewModel.monthlyGoal) {
        mutableStateOf(
            if (viewModel.monthlyGoal > 0.0) {
                "%.2f".format(viewModel.monthlyGoal)
            } else {
                ""
            }
        )
    }

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
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Metas",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = goalText,
            onValueChange = { goalText = it },
            label = { Text("Meta mensal") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val goal = goalText.replace(",", ".").toDoubleOrNull()

                if (goal != null && goal > 0.0) {
                    viewModel.saveMonthlyGoal(
                        monthlyGoal = goal,
                        onSuccess = {
                            Toast.makeText(
                                activity,
                                "Meta salva com sucesso",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        onFailure = { ex ->
                            Toast.makeText(
                                activity,
                                "Erro ao salvar meta: ${ex.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                } else {
                    Toast.makeText(
                        activity,
                        "Informe uma meta válida",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar meta")
        }

        Spacer(modifier = Modifier.height(24.dp))

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
