package com.example.spendtrackapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.spendtrackapp.viewmodel.MainViewModel

@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF616161)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mapa",
            fontSize = 24.sp,
            color = Color.White
        )

        Text(
            text = "Em breve: ${viewModel.totalItems()} gastos no mapa",
            color = Color.White
        )
    }
}

