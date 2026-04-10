package com.utez.nextwordmobile.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.utez.nextwordmobile.data.remote.dto.studentDto.StudentProfileDto
import com.utez.nextwordmobile.data.remote.dto.studentDto.StudentUpdateDto
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.studentViewModel.StudentUpdateProfileViewModel
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentUpdateProfileScreen(
    currentUser: StudentProfileDto?,
    viewModel: StudentUpdateProfileViewModel,
    onDismiss: () -> Unit,
    onProfileUpdated: () -> Unit,
    onLogout: () -> Unit
) {
    val initialFullName = currentUser?.fullName ?: ""
    val initialTutorName = currentUser?.tutorName ?: ""
    val initialTutorPhone = currentUser?.tutorPhone ?: ""
    val initialTutorEmail = currentUser?.tutorEmail ?: ""

    var fullName by remember(currentUser) { mutableStateOf(initialFullName) }
    var phoneNumber by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var tutorName by remember(currentUser) { mutableStateOf(initialTutorName) }
    var tutorPhone by remember(currentUser) { mutableStateOf(initialTutorPhone) }
    var tutorEmail by remember(currentUser) { mutableStateOf(initialTutorEmail) }

    val isLoading by viewModel.isLoading.collectAsState()
    val updateMessage by viewModel.updateMessage.collectAsState()

    val isNameValid = fullName.isNotBlank()
    val isPasswordValid = newPassword.isEmpty() || newPassword.length >= 8

    val hasChanges = fullName != initialFullName ||
            phoneNumber.isNotBlank() ||
            newPassword.isNotBlank() ||
            tutorName != initialTutorName ||
            tutorPhone != initialTutorPhone ||
            tutorEmail != initialTutorEmail

    val canSave = hasChanges && isNameValid && isPasswordValid && !isLoading

    LaunchedEffect(updateMessage) {
        updateMessage?.let { msg ->
            if (msg.contains("correctamente", ignoreCase = true) || msg.contains("éxito", ignoreCase = true)) {
                onProfileUpdated()
            }
            viewModel.clearMessage()
        }
    }

    val isUnderage = remember(currentUser?.dateOfBirth) {
        try {
            if (currentUser?.dateOfBirth.isNullOrEmpty()) return@remember true
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val birthDate = LocalDate.parse(currentUser!!.dateOfBirth, formatter)
            val today = LocalDate.now()
            Period.between(birthDate, today).years < 18
        } catch (e: Exception) {
            true
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // HEADER
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Configuración", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                    }
                }

                Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                // FORMULARIO
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // INFO CABECERA
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFF2962FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = fullName.take(1).uppercase().ifBlank { "U" }
                            Text(initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(fullName.ifBlank { "Usuario" }, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(currentUser?.email ?: " ", fontSize = 14.sp, color = Color.Gray)
                            Text("Estudiante", fontSize = 12.sp, color = Color(0xFF2962FF), fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // SECCIÓN: INFORMACIÓN BÁSICA
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Información Básica", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nombre Completo") },
                        isError = !isNameValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    if (!isNameValid) {
                        Text("El nombre no puede estar vacío", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = currentUser?.email ?: "", onValueChange = { }, label = { Text("Correo Electrónico") }, readOnly = true, enabled = false, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))

                    Spacer(modifier = Modifier.height(32.dp))

                    // SECCIÓN: SEGURIDAD
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seguridad", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Nueva Contraseña (Opcional)") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = !isPasswordValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = description, tint = Color.Gray)
                            }
                        }
                    )
                    if (!isPasswordValid) {
                        Text("La contraseña debe tener mínimo 8 caracteres", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (isUnderage) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Información del Tutor", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = tutorName, onValueChange = { tutorName = it }, label = { Text("Nombre del Tutor") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(value = tutorPhone, onValueChange = { tutorPhone = it }, label = { Text("Teléfono del Tutor") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    Button(
                        onClick = {
                            val updateDto = StudentUpdateDto(
                                fullName = fullName,
                                phoneNumber = phoneNumber.ifBlank { null },
                                profilePicture = null,
                                newPassword = newPassword.ifBlank { null },
                                tutorName = if (isUnderage) tutorName.ifBlank { null } else null,
                                tutorPhone = if (isUnderage) tutorPhone.ifBlank { null } else null,
                                tutorEmail = if (isUnderage) tutorEmail.ifBlank { null } else null
                            )
                            viewModel.updateProfile(updateDto) {
                                onProfileUpdated()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = canSave,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryDark,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onLogout()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
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