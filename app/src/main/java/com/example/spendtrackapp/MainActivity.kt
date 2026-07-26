package com.example.spendtrackapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


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
            var selectedMapLat by remember { mutableStateOf<Double?>(null) }
            var selectedMapLng by remember { mutableStateOf<Double?>(null) }
            var expenseIdWaitingLocationUpdate by remember { mutableStateOf<String?>(null) }
            var focusedExpenseLat by remember {
                mutableStateOf<Double?>(null)
            }

            var focusedExpenseLng by remember {
                mutableStateOf<Double?>(null)
            }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showFab = currentDestination?.hierarchy?.any {
                it.route == Routes.LIST
            } == true
            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    Toast.makeText(
                        this@MainActivity,
                        "Permissão de localização concedida",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Permissão de localização negada. Os gastos não poderão ser associados ao mapa.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            LaunchedEffect(Unit) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasPermission) {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            SpendTrackAppTheme {
                if (showDialog) {
                    ExpenseDialog(
                        onDismiss = {
                            showDialog = false
                            selectedMapLat = null
                            selectedMapLng = null
                        },
                        onConfirm = { description, amount, category ->

                            val hasMapLocation = selectedMapLat != null && selectedMapLng != null

                            if (hasMapLocation) {
                                viewModel.add(
                                    description = description,
                                    amount = amount,
                                    category = category,
                                    lat = selectedMapLat,
                                    lng = selectedMapLng,
                                    onSuccess = {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Gasto salvo com a localização selecionada no mapa",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        showDialog = false
                                        selectedMapLat = null
                                        selectedMapLng = null
                                    },
                                    onFailure = { ex ->
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Erro ao salvar gasto: ${ex.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                )
                            } else {
                                val hasLocationPermission = ContextCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasLocationPermission) {
                                    getCurrentLocation { lat, lng ->
                                        viewModel.add(
                                            description = description,
                                            amount = amount,
                                            category = category,
                                            lat = lat,
                                            lng = lng,
                                            onSuccess = {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    if (lat != null && lng != null) {
                                                        "Gasto salvo com localização atual"
                                                    } else {
                                                        "Gasto salvo, mas a localização não foi obtida"
                                                    },
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                showDialog = false
                                            },
                                            onFailure = { ex ->
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Erro ao salvar gasto: ${ex.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        )
                                    }
                                } else {
                                    locationPermissionLauncher.launch(
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )

                                    Toast.makeText(
                                        this@MainActivity,
                                        "Permita o acesso à localização e tente salvar novamente.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("SpendTrack")
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        navController.navigate(Routes.SETTINGS) {
                                            launchSingleTop = true
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Configurações"
                                    )
                                }

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
                                onClick = {
                                    showDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Adicionar gasto"
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        MainNavHost(
                            navController = navController,
                            viewModel = viewModel,
                            onMapClick = { latLng ->
                                focusedExpenseLat = null
                                focusedExpenseLng = null

                                val expenseIdToUpdate = expenseIdWaitingLocationUpdate

                                if (expenseIdToUpdate != null) {

                                    val expenseToUpdate =
                                        viewModel.findById(expenseIdToUpdate)

                                    if (expenseToUpdate != null) {

                                        viewModel.updateLocation(
                                            expense = expenseToUpdate,
                                            lat = latLng.latitude,
                                            lng = latLng.longitude,
                                            onSuccess = {

                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Localização atualizada",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                expenseIdWaitingLocationUpdate = null
                                            }
                                        )
                                    }

                                } else {

                                    selectedMapLat = latLng.latitude
                                    selectedMapLng = latLng.longitude

                                    showDialog = true
                                }
                            },

                            onChangeExpenseLocation = { expense ->

                                expenseIdWaitingLocationUpdate = expense.id

                                Toast.makeText(
                                    this@MainActivity,
                                    "Toque no mapa para selecionar a nova localização",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            onViewExpenseLocation = { expense ->
                                focusedExpenseLat = expense.lat
                                focusedExpenseLng = expense.lng
                            },
                            focusedLat = focusedExpenseLat,
                            focusedLng = focusedExpenseLng
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(
        onLocationResult: (lat: Double?, lng: Double?) -> Unit
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setGranularity(Granularity.GRANULARITY_FINE)
            .setMaxUpdateAgeMillis(0)
            .setDurationMillis(10_000)
            .build()

        fusedLocationClient
            .getCurrentLocation(
                request,
                CancellationTokenSource().token
            )
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d(
                        TAG,
                        "Localização atual obtida: provider=${location.provider}, accuracy=${location.accuracy}, lat=${location.latitude}, lng=${location.longitude}"
                    )

                    onLocationResult(
                        location.latitude,
                        location.longitude
                    )
                } else {
                    Log.w(
                        TAG,
                        "Localização atual retornou null"
                    )

                    onLocationResult(
                        null,
                        null
                    )
                }
            }
            .addOnFailureListener { ex ->
                Log.e(
                    TAG,
                    "Erro ao obter localização atual",
                    ex
                )

                onLocationResult(
                    null,
                    null
                )
            }
    }
}
