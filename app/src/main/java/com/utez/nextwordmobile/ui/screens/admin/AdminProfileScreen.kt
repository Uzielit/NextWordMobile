package com.utez.nextwordmobile.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.utez.nextwordmobile.data.remote.dto.adminDto.UpdateProfileRequest
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.adminViewModel.AdminUpdateProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    currentUser: Any?,
    viewModel: AdminUpdateProfileViewModel,
    onDismiss: () -> Unit,
    onProfileUpdated: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    val prefs = context.getSharedPreferences("NextWordPrefs", android.content.Context.MODE_PRIVATE)
    val initialName = prefs.getString("USER_NAME", "Administrador") ?: "Administrador"
    val initialEmail = prefs.getString("USER_EMAIL", "admin@nextword.com.mx") ?: "admin@nextword.com.mx"
    val initialPhone = prefs.getString("USER_PHONE", "") ?: ""

    var fullName by remember { mutableStateOf(initialName) }
    var phoneNumber by remember { mutableStateOf(initialPhone) }


    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()

    val passwordsMatch = newPassword == confirmPassword

    val hasProfileChanges = fullName != initialName || phoneNumber != initialPhone || newPassword.isNotBlank()

    val isFormValid = fullName.isNotBlank() && passwordsMatch
    val canSave = isFormValid && hasProfileChanges && !isLoading

    LaunchedEffect(initialName, initialPhone) {
        fullName = initialName
        phoneNumber = initialPhone
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // HEADER FIJO
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Configuración de Cuenta", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                    }
                }

                HorizontalDivider(color = Color(0xFFF0F0F0))

                // CONTENIDO SCROLLEABLE
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // FOTO DE PERFIL
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier.size(100.dp).clip(CircleShape).background(PrimaryDark),
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = fullName.take(2).uppercase().ifBlank { "AD" }
                            Text(initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 36.sp)
                        }
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF3B6D11)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Cambiar Foto", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(fullName.ifBlank { "Administrador" }, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Acceso Total", color = Color(0xFF3B6D11), fontSize = 14.sp, fontWeight = FontWeight.Medium)

                    Spacer(modifier = Modifier.height(32.dp))

                    // DATOS DE CONTACTO
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Datos de Contacto", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = initialEmail,
                        onValueChange = { },
                        label = { Text("Correo (Solo lectura)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        readOnly = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Gray,
                            disabledBorderColor = Color(0xFFE5E7EB)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nombre Completo") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF111827),
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) phoneNumber = it
                        },
                        label = { Text("Teléfono)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF111827),
                            fontSize = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Spacer(modifier = Modifier.height(24.dp))

                    // CAMBIO DE CONTRASEÑA
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Actualizar Contraseña ", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Nueva Contraseña") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827),
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar Nueva Contraseña") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        isError = confirmPassword.isNotEmpty() && !passwordsMatch,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827),
                            fontSize = 16.sp
                        )

                    )
                    if (confirmPassword.isNotEmpty() && !passwordsMatch) {
                        Text("Las contraseñas no coinciden", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp, top = 4.dp).align(Alignment.Start))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // BOTÓN DE GUARDAR
                    Button(
                        onClick = {
                            val request = UpdateProfileRequest(
                                fullName = fullName,
                                phoneNumber = phoneNumber,
                                profilePicture = null,
                                newPassword = if (newPassword.isNotBlank()) newPassword else null
                            )
                            viewModel.updateProfile(context, request) {
                                onProfileUpdated()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = canSave,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryDark,
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Guardar Cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // BOTÓN CERRAR SESIÓN
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onLogout()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = BorderStroke(1.dp, Color.Red)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}