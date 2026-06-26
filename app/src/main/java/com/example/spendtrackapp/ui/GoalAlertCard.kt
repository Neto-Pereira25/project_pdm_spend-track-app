package com.example.spendtrackapp.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GoalAlertCard(
    message: String,
    level: Int,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (level) {
        3 -> Color(0xFFFFCDD2)
        2 -> Color(0xFFFFF9C4)
        1 -> Color(0xFFC8E6C9)
        else -> Color(0xFFE0E0E0)
    }

    val textColor = when (level) {
        3 -> Color(0xFFB71C1C)
        2 -> Color(0xFFF57F17)
        1 -> Color(0xFF1B5E20)
        else -> Color(0xFF424242)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = message,
            color = textColor,
            fontSize = 16.sp
        )
    }
}
