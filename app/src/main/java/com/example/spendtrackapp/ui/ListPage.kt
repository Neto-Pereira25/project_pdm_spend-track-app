package com.example.spendtrackapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendtrackapp.viewmodel.MainViewModel

@Composable
fun ListPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onExpenseClick: (String) -> Unit
) {
    val expenseList = viewModel.expenses


    if (expenseList.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nenhum gasto registrado ainda.",
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Use o botão + para adicionar seu primeiro gasto.",
                fontSize = 14.sp
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(expenseList, key = { it.id }) { expense ->
            ExpenseItem(
                expense = expense,
                onClick = {
                    onExpenseClick(expense.id)
                    onExpenseClick(expense.id)
                },
                onClose = {
                    viewModel.remove(expense)
                }
            )
        }
    }
}
