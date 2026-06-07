package com.example.spendtrackapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dashboard",
            fontSize = 24.sp
        )

        Text(
            text = "Total de gastos: ${viewModel.totalItems()}",
            fontSize = 18.sp
        )

        Text(
            text = "Total gasto: R$ %.2f".format(viewModel.totalSpent()),
            fontSize = 18.sp
        )
    }
}

