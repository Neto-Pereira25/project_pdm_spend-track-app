package com.example.spendtrackapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CategoryBarChart(
    categories: Map<String, Double>
) {

    val maxValue =
        categories.maxOfOrNull { it.value } ?: 0.0

    Column {

        categories.forEach { (category, amount) ->

            val percentage =
                if (maxValue > 0)
                    amount / maxValue
                else
                    0.0

            Text(
                text = "$category - R$ %.2f".format(amount)
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(Color.LightGray)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(
                            percentage.toFloat()
                        )
                        .background(
                            MaterialTheme.colorScheme.primary
                        )
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
    }
}
