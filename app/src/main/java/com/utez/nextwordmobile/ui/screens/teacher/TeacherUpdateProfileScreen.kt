package com.utez.nextwordmobile.ui.screens.teacher

import android.R.attr.fontWeight
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherDto
import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherProfileUpdateDto
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.teacherViewModel.TeacherUpdateProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherUpdateProfileScreen(
    currentUser: TeacherDto?,
    viewModel: TeacherUpdateProfileViewModel,
    onDismiss: () -> Unit,
    onProfileUpdated: () -> Unit,
    onLogout: () -> Unit
) {

    val fullName = currentUser?.fullName ?: ""
    val initialSpecialization = currentUser?.specialization ?: ""
    val initialExperience = currentUser?.yearsOfExperience?.toString() ?: ""
    val initialDescription = currentUser?.professionalDescription ?: ""
    val initialCertifications = currentUser?.certifications ?: ""

    var specialization by remember(currentUser) { mutableStateOf(initialSpecialization) }
    var yearsOfExperience by remember(currentUser) { mutableStateOf(initialExperience) }
    var description by remember(currentUser) { mutableStateOf(initialDescription) }
    var certifications by remember(currentUser) { mutableStateOf(initialCertifications) }

    val isLoading by viewModel.isLoading.collectAsState()
    val updateMessage by viewModel.updateMessage.collectAsState()


    val isFormValid = specialization.isNotBlank() && yearsOfExperience.isNotBlank() && description.isNotBlank()

    val hasChanges = specialization != initialSpecialization ||
            yearsOfExperience != initialExperience ||
            description != initialDescription ||
            certifications != initialCertifications

    val canSave = isFormValid && hasChanges && !isLoading
    val inputStyle = TextStyle(
        color = Color(0xFF111827),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )

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
                    Text("Mi Perfil Profesional", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
                            modifier = Modifier.size(60.dp).clip(CircleShape).background(PrimaryDark),
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = fullName.take(1).uppercase().ifBlank { "P" }
                            Text(initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(fullName.ifBlank { "Profesor" }, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Rating: ⭐ ${currentUser?.averageRating ?: "0.0"}", fontSize = 12.sp, color = Color.Gray)
                            Text("Profesor", fontSize = 12.sp, color = PrimaryDark, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // SECCIÓN: DATOS PROFESIONALES
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Work, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Completar Perfil", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = specialization,
                        onValueChange = { specialization = it },
                        label = { Text("Especialidad (Ej. Inglés Básico, Conversación)*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        textStyle = inputStyle
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = yearsOfExperience,
                        onValueChange = {
                            // Solo permitimos números
                            if (it.all { char -> char.isDigit() }) yearsOfExperience = it
                        },
                        label = { Text("Años de Experiencia*") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        textStyle = inputStyle
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = certifications,
                        onValueChange = { certifications = it },
                        label = { Text("Certificaciones*") },
                        placeholder = { Text("Ej. TOEFL, Cambridge...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        textStyle = inputStyle
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción Profesional*") },
                        placeholder = { Text("Cuéntale a los alumnos sobre tu método de enseñanza...") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 4,
                        textStyle = inputStyle
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            val updateDto = TeacherProfileUpdateDto(
                                specialization = specialization,
                                yearsOfExperience = yearsOfExperience.toIntOrNull() ?: 0,
                                professionalDescription = description,
                                certifications = certifications
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
                        else Text("Guardar Perfil", fontWeight = FontWeight.Bold)
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