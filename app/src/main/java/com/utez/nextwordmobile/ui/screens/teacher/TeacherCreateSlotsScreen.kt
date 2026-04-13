package com.utez.nextwordmobile.ui.screens.teacher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.teacherViewModel.TeacherSlotsViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.utez.nextwordmobile.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCreateSlotsScreen(
    paddingValues: PaddingValues,
    teacherId: String,
    viewModel: TeacherSlotsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis() - 86400000
            }
        }
    )

    val availableSlots by viewModel.slots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val formatterAPI = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formatterVisual = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))

    LaunchedEffect(selectedDate) {
        viewModel.fetchSlots(selectedDate.format(formatterAPI), teacherId)
    }

    Box(modifier = Modifier.fillMaxSize())
    {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = PrimaryDark,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Horario")
                }
            },
            containerColor = Color(0xFFF5F5F5)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NextWordGradient)
                        .padding(top = 16.dp, bottom = 24.dp)
                ) {
                    Column {
                        // Fila con SÓLO el Logo
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.nexwordlogo),
                                contentDescription = "Logo",
                                modifier = Modifier.height(30.dp).width(120.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Text("Mi Disponibilidad", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text("Selecciona una fecha para gestionar tus horarios.", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        val dateText = selectedDate.format(formatterVisual).replaceFirstChar { it.uppercase() }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { showDatePicker = true }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Calendario", tint = Color.White)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = dateText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Icon(Icons.Default.Edit, contentDescription = "Cambiar", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. LISTA DE HORARIOS
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                    Text("Horarios del día", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = PrimaryDark)
                    } else if (availableSlots.isEmpty()) {
                        Text("No tienes horarios registrados este día.", color = Color.Gray, fontSize = 14.sp)
                    } else {
                        availableSlots.forEach { slot ->
                            SlotCardDisplay(startTime = slot.startTime, endTime = slot.endTime, status = "Disponible")
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(120.dp)) // Espacio para el BottomBar y FAB
                }
            }
        }

        // 🌟 EL "PARCHE" DEL RELOJ
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(NextWordGradient)
                .align(Alignment.TopCenter)
        )
    }

    // 3. CALENDARIO MODAL
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                // 🌟 BOTÓN MEJORADO EN EL DATEPICKER
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                ) { Text("Seleccionar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar", color = Color.Gray) }
            },
            colors = DatePickerDefaults.colors(containerColor = Color.White)
        ) {
            DatePicker(
                state = datePickerState,
                title = { Text(" Selecciona una fecha", modifier = Modifier.padding(start = 24.dp, top = 24.dp)) },
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = PrimaryDark,
                    selectedDayContentColor = Color.White,
                    todayDateBorderColor = PrimaryDark,
                    todayContentColor = PrimaryDark
                )
            )
        }
    }

    // 4. BOTTOM SHEET DE CREACIÓN
    if (showCreateDialog) {
        ModalBottomSheet(
            onDismissRequest = { showCreateDialog = false },
            containerColor = Color.White
        ) {
            var showStartTimePicker by remember { mutableStateOf(false) }
            var showEndTimePicker by remember { mutableStateOf(false) }
            var sheetErrorMessage by remember { mutableStateOf<String?>(null) }

            val startTimeState = rememberTimePickerState(initialHour = 9, initialMinute = 0)
            val endTimeState = rememberTimePickerState(initialHour = 10, initialMinute = 0)

            val startText = String.format(Locale.getDefault(), "%02d:%02d", startTimeState.hour, startTimeState.minute)
            val endText = String.format(Locale.getDefault(), "%02d:%02d", endTimeState.hour, endTimeState.minute)

            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Text("Nuevo Horario", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryDark)
                Text("Día: ${selectedDate.format(formatterAPI)}", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hora Inicio", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedCard(
                            onClick = { showStartTimePicker = true; sheetErrorMessage = null },
                            colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, PrimaryDark.copy(alpha = 0.5f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(startText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = PrimaryDark)
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hora Fin", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedCard(
                            onClick = { showEndTimePicker = true; sheetErrorMessage = null },
                            colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, PrimaryDark.copy(alpha = 0.5f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(endText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = PrimaryDark)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🌟 TEXTO DE ERROR MÁS FUERTE
                if (sheetErrorMessage != null) {
                    Text(
                        text = sheetErrorMessage!!,
                        color = Color(0xFFD32F2F), // Rojo más fuerte
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Button(
                    onClick = {
                        sheetErrorMessage = null

                        val currentDateTime = java.time.LocalDateTime.now()
                        val selectedStartDateTime = java.time.LocalDateTime.of(
                            selectedDate,
                            java.time.LocalTime.of(startTimeState.hour, startTimeState.minute)
                        )
                        val selectedEndDateTime = java.time.LocalDateTime.of(
                            selectedDate,
                            java.time.LocalTime.of(endTimeState.hour, endTimeState.minute)
                        )

                        if (selectedStartDateTime.isBefore(currentDateTime)) {
                            sheetErrorMessage = "Error: No se puede crear un horario a esta hora."
                            return@Button
                        }
                        if (!selectedStartDateTime.isBefore(selectedEndDateTime)) {
                            sheetErrorMessage = "La hora de fin debe ser mayor a la de inicio."
                            return@Button
                        }

                        viewModel.createSlot(
                            teacherId = teacherId,
                            date = selectedDate.format(formatterAPI),
                            startTime = startText,
                            endTime = endText,
                            classType = "Inglés",
                            onSuccess = {
                                showCreateDialog = false
                                coroutineScope.launch { snackbarHostState.showSnackbar("Horario guardado con éxito 🎉") }
                            },
                            onError = { err ->
                                sheetErrorMessage = err
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                ) {
                    Text("Guardar Horario")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (showStartTimePicker) {
                NextWordTimePickerDialog(
                    onDismissRequest = { showStartTimePicker = false },
                    onConfirm = { showStartTimePicker = false }
                ) { TimePicker(state = startTimeState) }
            }
            if (showEndTimePicker) {
                NextWordTimePickerDialog(
                    onDismissRequest = { showEndTimePicker = false },
                    onConfirm = { showEndTimePicker = false }
                ) { TimePicker(state = endTimeState) }
            }
        }
    }
}

// COMPONENTES AUXILIARES
@Composable
fun SlotCardDisplay(startTime: String, endTime: String, status: String) {
    val isAvailable = status.lowercase() == "disponible"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (isAvailable) PrimaryDark else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$startTime - $endTime",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAvailable) Color.Black else Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = status,
                    color = if (isAvailable) Color(0xFF2E7D32) else Color(0xFFE65100),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 🌟 DIÁLOGO CON BOTÓN SÓLIDO
@Composable
fun NextWordTimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
            ) {
                Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar", color = Color.Gray)
            }
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        },
        containerColor = Color.White
    )
}