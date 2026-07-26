package com.example.spendtrackapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendtrackapp.model.Expense

@Composable
fun ExpenseDetailsPage(
    expense: Expense?,
    onDelete: () -> Unit,
    onUpdate: (description: String, amount: Double, category: String) -> Unit,
    onChangeLocation: (Expense) -> Unit,
    onViewLocation: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember {
        mutableStateOf(false)
    }

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

    if (showEditDialog) {
        ExpenseDialog(
            title = "Editar gasto",
            confirmButtonText = "Salvar alterações",
            initialDescription = expense.description,
            initialAmount = "%.2f".format(expense.amount),
            initialCategory = expense.category,
            onDismiss = {
                showEditDialog = false
            },
            onConfirm = { description, amount, category ->
                onUpdate(description, amount, category)
                showEditDialog = false
            }
        )
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

        Text(
            text = "Descrição: ${expense.description}",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Valor: R$ %.2f".format(expense.amount),
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Categoria: ${expense.category}",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (expense.lat != null && expense.lng != null) {
            Text(
                text = "Localização: %.5f, %.5f".format(
                    expense.lat,
                    expense.lng
                ),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    onViewLocation(expense)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver localização no mapa")
            }
        } else {
            Text(
                text = "Localização: não disponível",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                showEditDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar gasto")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                onChangeLocation(expense)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Alterar localização no mapa")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Excluir gasto")
        }
    }
}