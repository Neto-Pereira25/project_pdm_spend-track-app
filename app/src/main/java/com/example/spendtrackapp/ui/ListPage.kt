package com.example.spendtrackapp.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
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
    viewModel: MainViewModel
) {
    val expenseList = viewModel.expenses
    val activity = LocalActivity.current as Activity

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(expenseList, key = { it.id }) { expense ->
            ExpenseItem(
                expense = expense,
                onClick = {
                    Toast.makeText(
                        activity,
                        "${expense.description} - R$ %.2f".format(expense.amount),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onClose = {
                    viewModel.remove(expense)
                }
            )
        }
    }
}

