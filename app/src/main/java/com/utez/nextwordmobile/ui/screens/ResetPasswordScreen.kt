package com.utez.nextwordmobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utez.nextwordmobile.ui.components.NextWordButton
import com.utez.nextwordmobile.ui.components.NextWordTextField
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.AuthViewModel
import com.utez.nextwordmobile.viewModel.ForgotPasswordViewModel
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordScreen(
    viewModel: ForgotPasswordViewModel = viewModel(),
    email: String,
    onNavigateBack: () -> Unit,
    onResetSuccess: () -> Unit
) {
    var otpCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().background(NextWordGradient).padding(paddingValues)
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.padding(top = 40.dp, start = 16.dp).background(Color.White.copy(alpha = 0.8f), CircleShape)
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
                        ) { Icon(Icons.Default.VpnKey, contentDescription = "Key", tint = PrimaryDark, modifier = Modifier.size(30.dp)) }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Nueva Contraseña", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Ingresa el código que enviamos a $email y crea tu nueva contraseña.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)

                        Spacer(modifier = Modifier.height(24.dp))

                        OtpInputField(otpText = otpCode, onOtpTextChange = { if (it.length <= 6) otpCode = it })

                        Spacer(modifier = Modifier.height(16.dp))
                        NextWordTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it; passwordError = null },
                            label = "Nueva Contraseña",
                            isPassword = true,
                            isError = passwordError != null,
                            errorMessage = passwordError
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        NextWordTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; passwordError = null },
                            label = "Confirmar Contraseña",
                            isPassword = true,
                            isError = passwordError != null,
                            errorMessage = passwordError
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        NextWordButton(text = "Cambiar Contraseña", onClick = {
                            if (otpCode.length < 6) {
                                scope.launch { snackbarHostState.showSnackbar("Ingresa el código completo de 6 dígitos.") }
                            } else if (newPassword.length < 8) {
                                passwordError = "La contraseña debe tener mínimo 8 caracteres."
                            } else if (newPassword != confirmPassword) {
                                passwordError = "Las contraseñas no coinciden."
                            } else {
                                viewModel.resetPassword(
                                    email = email,
                                    code = otpCode,
                                    newPassword = newPassword,
                                    onSuccess = { showSuccessDialog = true },
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

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(text = "¡Éxito!", fontWeight = FontWeight.Bold) },
                text = { Text("Tu contraseña ha sido actualizada correctamente.") },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                        onClick = {
                            showSuccessDialog = false
                            onResetSuccess()
                        }
                    ) { Text("Iniciar Sesión", color = Color.White) }
                }
            )
        }
    }
}