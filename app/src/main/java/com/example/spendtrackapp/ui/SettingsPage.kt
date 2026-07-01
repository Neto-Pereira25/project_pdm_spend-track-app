package com.example.spendtrackapp.ui

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun SettingsPage(
    modifier: Modifier = Modifier
) {
    val activity = LocalActivity.current
    val user = Firebase.auth.currentUser

    val darkModeChecked = remember { mutableStateOf(false) }
    val notificationsChecked = remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Configurações",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "SpendTrack",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Controle inteligente de gastos",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Versão: 1.0",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Divider()

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Usuário",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "E-mail: ${user?.email ?: "Não informado"}",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Divider()

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Preferências",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Modo escuro")

        Switch(
            checked = darkModeChecked.value,
            onCheckedChange = {
                darkModeChecked.value = it
                Toast.makeText(
                    activity,
                    "Modo escuro será implementado em uma versão futura",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Alertas de limite")

        Switch(
            checked = notificationsChecked.value,
            onCheckedChange = {
                notificationsChecked.value = it
                Toast.makeText(
                    activity,
                    "Configuração visual de alertas atualizada",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Divider()

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Dados",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = {
                Toast.makeText(
                    activity,
                    "Exportação CSV será implementada em uma versão futura",
                    Toast.LENGTH_SHORT
                ).show()
            }
        ) {
            Text("Exportar dados em CSV")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                Firebase.auth.signOut()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sair da conta")
        }
    }
}
