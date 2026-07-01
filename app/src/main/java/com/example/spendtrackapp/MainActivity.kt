package com.example.spendtrackapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.spendtrackapp.db.fb.FBDatabase
import com.example.spendtrackapp.ui.ExpenseDialog
import com.example.spendtrackapp.ui.nav.BottomNavBar
import com.example.spendtrackapp.ui.nav.BottomNavItem
import com.example.spendtrackapp.ui.nav.MainNavHost
import com.example.spendtrackapp.ui.nav.Routes
import com.example.spendtrackapp.ui.theme.SpendTrackAppTheme
import com.example.spendtrackapp.viewmodel.MainViewModel
import com.example.spendtrackapp.viewmodel.MainViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "SpendTrackExpense"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val navController = rememberNavController()
            val fbDB = remember { FBDatabase() }
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(fbDB)
            )

            var showDialog by remember { mutableStateOf(false) }

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val showFab = currentDestination?.hierarchy?.any {
                it.route == Routes.LIST
            } == true

            val context = LocalContext.current
            val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

            var currentLat by remember { mutableStateOf<Double?>(null) }
            var currentLng by remember { mutableStateOf<Double?>(null) }

            val hasLocationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            fun fetchCurrentLocation(onLocationReady: (Double?, Double?) -> Unit) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    onLocationReady(currentLat, currentLng)
                    return
                }

                val cancellationTokenSource = CancellationTokenSource()
                val request = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setGranularity(Granularity.GRANULARITY_FINE)
                    .setMaxUpdateAgeMillis(0)
                    .setDurationMillis(10_000)
                    .build()

                fusedLocationClient
                    .getCurrentLocation(
                        request,
                        cancellationTokenSource.token
                    )
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            currentLat = location.latitude
                            currentLng = location.longitude
                            Log.d(
                                TAG,
                                "Fresh location obtained: provider=${location.provider}, accuracy=${location.accuracy}, lat=${location.latitude}, lng=${location.longitude}"
                            )
                            onLocationReady(location.latitude, location.longitude)
                        } else {
                            Log.w(TAG, "Fresh location is null. Using last known state values")
                            onLocationReady(currentLat, currentLng)
                        }
                    }
                    .addOnFailureListener { ex ->
                        Log.e(TAG, "Error getting fresh location", ex)
                        onLocationReady(currentLat, currentLng)
                    }
            }

            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    fetchCurrentLocation { _, _ -> }
                }
            }

            LaunchedEffect(Unit) {
                if (!hasLocationPermission) {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    fetchCurrentLocation { _, _ -> }
                }
            }

            SpendTrackAppTheme {
                if (showDialog) {
                    ExpenseDialog(
                        onDismiss = { showDialog = false },
                        onConfirm = { description, amount, category ->
                            Log.d(TAG, "Expense dialog confirmed: description='$description', amount=$amount, category='$category'")
                            fetchCurrentLocation { lat, lng ->
                                viewModel.add(
                                    description = description,
                                    amount = amount,
                                    category = category,
                                    lat = lat,
                                    lng = lng,
                                    onSuccess = {
                                        Toast.makeText(
                                            this,
                                            "Gasto salvo com sucesso no Firestore",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        showDialog = false
                                    },
                                    onFailure = { ex ->
                                        Toast.makeText(
                                            this,
                                            "Falha ao salvar gasto no Firestore: ${ex.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                )
                            }
                        }
                    )
                }


                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("SpendTrack") },
                            actions = {
                                IconButton(
                                    onClick = {
                                        Firebase.auth.signOut()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Sair"
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.Home,
                            BottomNavItem.List,
                            BottomNavItem.Map,
                            BottomNavItem.Goals
                        )

                        BottomNavBar(
                            navController = navController,
                            items = items
                        )
                    },
                    floatingActionButton = {
                        if (showFab) {
                            FloatingActionButton(
                                onClick = { showDialog = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Adicionar gasto"
                                )
                            }
                        }
                    }
                )
                { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MainNavHost(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

