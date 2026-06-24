package com.example.spendtrackapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.spendtrackapp.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val expenses = viewModel.expenses

    val firstLocation = expenses.firstOrNull { it.lat != null && it.lng != null }

    val cameraPositionState = rememberCameraPositionState {
        if (firstLocation != null) {
            position = CameraPosition.fromLatLngZoom(
                LatLng(firstLocation.lat!!, firstLocation.lng!!),
                15f
            )
        }
    }

    LaunchedEffect(expenses.size) {
        val latestLocation = expenses.lastOrNull { it.lat != null && it.lng != null }
        if (latestLocation != null) {
            try {
                val latLng = LatLng(latestLocation.lat!!, latestLocation.lng!!)
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(latLng, 15f),
                    durationMs = 1000
                )
            } catch (e: Exception) {
                // Ignorar erros de animação
            }
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        expenses.forEach { expense ->
            if (expense.lat != null && expense.lng != null) {
                val position = LatLng(expense.lat, expense.lng)

                Marker(
                    state = MarkerState(position = position),
                    title = expense.description,
                    snippet = "R$ %.2f - ${expense.category}".format(expense.amount)
                )
            }
        }
    }
}
