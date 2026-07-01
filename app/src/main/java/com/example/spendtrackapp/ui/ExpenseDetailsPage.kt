package com.example.spendtrackapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendtrackapp.model.Expense

@Composable
fun ExpenseDetailsPage(
    expense: Expense?,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (expense == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Gasto não encontrado",
                fontSize = 20.sp
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Detalhes do gasto",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Descrição: ${expense.description}", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Valor: R$ %.2f".format(expense.amount), fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Categoria: ${expense.category}", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))

        if (expense.lat != null && expense.lng != null) {
            Text(
                text = "Localização: %.5f, %.5f".format(expense.lat, expense.lng),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        } else {
            Text(
                text = "Localização: não disponível",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onDelete) {
            Text("Excluir gasto")
        }
    }
}