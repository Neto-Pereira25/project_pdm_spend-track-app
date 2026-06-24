package com.example.spendtrackapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.spendtrackapp.viewmodel.MainViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState


@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val recife = remember{ MarkerState(position = LatLng(-8.05, -34.9)) }

    GoogleMap(
        modifier = modifier.fillMaxSize()
    ) {
        Marker(
            state = recife,
            title = "Recife",
            snippet = "Marcador de teste"
        )
    }
}
