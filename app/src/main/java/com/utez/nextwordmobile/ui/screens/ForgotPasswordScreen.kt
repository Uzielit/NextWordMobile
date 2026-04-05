package com.utez.nextwordmobile.ui.screens

import android.util.Patterns
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utez.nextwordmobile.ui.components.NextWordButton
import com.utez.nextwordmobile.ui.components.NextWordTextField
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.utez.nextwordmobile.viewModel.ForgotPasswordViewModel

import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun ForgotPasswordScreen(
   viewModel: ForgotPasswordViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToReset: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NextWordGradient)
                .padding(paddingValues)
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(top = 40.dp, start = 16.dp)
                    .background(Color.White.copy(alpha = 0.8f), CircleShape)
            ) { Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = PrimaryDark) }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(100.dp))

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(60.dp).background(Color(0xFFE5E7EB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.LockReset, contentDescription = "Lock", tint = PrimaryDark, modifier = Modifier.size(30.dp)) }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Recuperar Contraseña", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Ingresa el correo electrónico asociado a tu cuenta. Te enviaremos un código de 6 dígitos para restablecerla.",
                            fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        NextWordTextField(
                            value = email,
                            onValueChange = { email = it; emailError = null },
                            label = "Correo Electrónico",
                            isError = emailError != null,
                            errorMessage = emailError
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        NextWordButton(text = "Enviar Código", onClick = {
                            keyboardController?.hide()
                            if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                emailError = "Ingresa un correo válido."
                            } else {

                                viewModel.forgotPassword(
                                    email = email,
                                    onSuccess = { onNavigateToReset(email) },
                                    onError = { error ->
                                        scope.launch { snackbarHostState.showSnackbar(error) }
                                    }
                                )
                            }
                        })
                    }
                }
            }
        }
    }
}