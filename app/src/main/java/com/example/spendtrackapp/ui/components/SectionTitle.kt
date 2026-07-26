package com.example.spendtrackapp.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun SectionTitle(
    text: String
) {
    Text(
        text = text,
        fontSize = 22.sp
    )
}
