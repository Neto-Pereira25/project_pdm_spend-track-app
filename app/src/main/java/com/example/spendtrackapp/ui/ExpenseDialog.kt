package com.example.spendtrackapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

import android.widget.Toast
import androidx.activity.compose.LocalActivity

@Composable
fun ExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (description: String, amount: Double, category: String) -> Unit
) {
    val description = remember { mutableStateOf("") }
    val amount = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val activity = LocalActivity.current

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Adicionar gasto")
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Descrição") },
                    value = description.value,
                    onValueChange = { description.value = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Valor") },
                    value = amount.value,
                    onValueChange = { amount.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Categoria") },
                    value = category.value,
                    onValueChange = { category.value = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val expenseAmount = amount.value.replace(",", ".").toDoubleOrNull()

                        if (
                            description.value.isNotBlank() &&
                            category.value.isNotBlank() &&
                            expenseAmount != null &&
                            expenseAmount > 0.0
                        ) {
                            onConfirm(
                                description.value.trim(),
                                expenseAmount,
                                category.value.trim()
                            )
                        } else {
                            Toast.makeText(
                                activity,
                                "Preencha uma descrição, categoria e valor válido.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Salvar")
                }
            }
        }
    }
}
