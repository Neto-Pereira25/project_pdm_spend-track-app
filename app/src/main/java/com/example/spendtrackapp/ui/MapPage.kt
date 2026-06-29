package com.example.spendtrackapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.spendtrackapp.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onMapClick: (LatLng) -> Unit
) {
    val context = LocalContext.current

    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val clusters = viewModel.collectivePriceClusters()
    val cameraPositionState = rememberCameraPositionState()

    val firstCluster = clusters.firstOrNull()

    LaunchedEffect(firstCluster) {
        if (firstCluster != null) {
            val position = LatLng(firstCluster.lat, firstCluster.lng)
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(position, 15f)
            )
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = hasLocationPermission
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = hasLocationPermission,
            zoomControlsEnabled = true
        ),
        onMapClick = { latLng ->
            onMapClick(latLng)
        }
    ) {
        clusters.forEach { cluster ->
            val position = LatLng(cluster.lat, cluster.lng)

            Marker(
                state = MarkerState(position = position),
                title = "Preço médio: R$ %.2f".format(cluster.averageAmount),
                snippet = "${cluster.count} registro(s) próximos • Categoria: ${cluster.mainCategory}"
            )
        }
    }
}
