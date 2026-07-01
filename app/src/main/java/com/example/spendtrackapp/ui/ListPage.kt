package com.example.spendtrackapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spendtrackapp.viewmodel.MainViewModel

@Composable
fun ListPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onExpenseClick: (String) -> Unit
) {
    val expenseList = viewModel.expenses

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
                },
                onClose = {
                    viewModel.remove(expense)
                }
            )
        }
    }
}