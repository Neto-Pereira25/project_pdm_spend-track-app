package com.example.spendtrackapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.spendtrackapp.ui.theme.SpendTrackAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpendTrackAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomePage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val activity = LocalActivity.current as Activity

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bem-vindo ao SpendTrack!",
            fontSize = 24.sp
        )

        Button(
            onClick = {
                activity.finish()
            }
        ) {
            Text("Sair")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    SpendTrackAppTheme {
        HomePage()
    }
}
