package com.example.spendtrackapp


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendtrackapp.ui.theme.SpendTrackAppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlin.jvm.java


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpendTrackAppTheme {
                LoginPage()
            }
        }
    }
}


@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val activity = LocalActivity.current as Activity

    val fieldModifier = Modifier.fillMaxWidth(0.9f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login - SpendTrack",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Digite seu e-mail") },
            modifier = fieldModifier,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Digite sua senha") },
            modifier = fieldModifier,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                Firebase.auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            activity.startActivity(
                                Intent(activity, MainActivity::class.java)
                            )
                            Toast.makeText(activity, "Login OK!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(activity, "Login FALHOU!", Toast.LENGTH_LONG).show()
                        }
                    }
            },
            enabled = email.isNotEmpty() && password.isNotEmpty()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                activity.startActivity(
                    Intent(activity, RegisterActivity::class.java)
                )
            }
        ) {
            Text("Criar conta")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                email = ""
                password = ""
            }
        ) {
            Text("Limpar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    SpendTrackAppTheme {
        LoginPage()
    }
}

