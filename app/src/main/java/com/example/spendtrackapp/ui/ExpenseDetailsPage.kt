package com.example.spendtrackapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendtrackapp.model.Expense
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.spendtrackapp.ui.components.InfoCard
import com.example.spendtrackapp.ui.components.SectionTitle
import com.example.spendtrackapp.ui.components.StatCard

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
                onUpdate(
                    description,
                    amount,
                    category
                )
                showEditDialog = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {

        SectionTitle(
            text = "Detalhes do gasto"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        InfoCard(
            title = "Descrição",
            value = expense.description
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        StatCard(
            title = "Valor",
            value = "R$ %.2f".format(expense.amount)
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        InfoCard(
            title = "Categoria",
            value = expense.category
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        if (expense.lat != null && expense.lng != null) {

            SectionTitle(
                text = "Localização"
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "%.5f, %.5f".format(
                    expense.lat,
                    expense.lng
                ),
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Visualização da localização",
                fontSize = 16.sp
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            val position = LatLng(
                expense.lat,
                expense.lng
            )

            val cameraPositionState =
                rememberCameraPositionState()

            val markerState = remember(position) {
                MarkerState(position)
            }

            LaunchedEffect(position) {
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(
                        position,
                        16f
                    )
                )
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    scrollGesturesEnabled = false,
                    zoomGesturesEnabled = false
                )
            ) {

                Marker(
                    state = markerState,
                    title = expense.description,
                    snippet = "R$ %.2f".format(
                        expense.amount
                    )
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = {
                    onViewLocation(expense)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver localização no mapa")
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

        } else {

            InfoCard(
                title = "Localização",
                value = "Não disponível"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        Button(
            onClick = {
                showEditDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar gasto")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = {
                onChangeLocation(expense)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Alterar localização no mapa")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Excluir gasto")
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}