package com.example.spendtrackapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoCard(
    title: String,
    value: String,
    subtitle: String? = null
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                fontSize = 14.sp
            )

            Text(
                text = value,
                fontSize = 20.sp
            )

            subtitle?.let {

                Text(
                    text = it,
                    fontSize = 14.sp
                )
            }
        }
    }
}