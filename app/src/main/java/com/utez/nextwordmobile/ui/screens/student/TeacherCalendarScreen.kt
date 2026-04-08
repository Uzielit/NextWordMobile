package com.utez.nextwordmobile.ui.screens.student

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.studentViewModel.StudenReservationViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCalendarScreen(
    teacherId: String,
    teacherName: String,
    studentId: String,
    viewModel: StudenReservationViewModel,
    onNavigateBack: () -> Unit,
    onReservationSuccess: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 🌟 ESTADOS DEL CALENDARIO REAL
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Bloquea fechas pasadas
                return utcTimeMillis >= System.currentTimeMillis() - 86400000
            }
        })

    var selectedSlotId by remember { mutableStateOf<String?>(null) }
    var classTopic by remember { mutableStateOf("") }

    val availableSlots by viewModel.availableSlots.collectAsState()
    val isBooking by viewModel.isBookingLoading.collectAsState()

    // Llama al servidor cuando cambias de fecha
    LaunchedEffect(selectedDate) {
        val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        viewModel.fetchSlots(teacherId, formattedDate)
        selectedSlotId = null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 🌟 1. EL HEADER CON GRADIENTE ESTILO NEXTWORD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NextWordGradient)
                    .padding(top = paddingValues.calculateTopPadding()) // Respeta la barra de estado
                    .padding(bottom = 24.dp)
            ) {
                Column {
                    // Barra de navegación superior integrada
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                        }
                        Text(
                            text = "Agendar Clase",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Selector de fecha integrado en el fondo de color
                    val formatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", Locale("es", "ES"))
                    val dateText = selectedDate.format(formatter).replaceFirstChar { it.uppercase() }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f)) // Fondo semi-transparente elegante
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

            // 🌟 2. EL CONTENIDO SCROLLEABLE (Debajo del Header)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- TARJETA INFO DEL PROFESOR ---
                // La subimos un poco para que se superponga al gradiente
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-20).dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(PrimaryDark.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = teacherName.take(2).uppercase(),
                                color = PrimaryDark,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Column {
                            Text(text = teacherName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.School, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Inglés", fontSize = 14.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("•", fontSize = 14.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("50 Créditos", fontSize = 14.sp, color = PrimaryDark, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- HORARIOS DISPONIBLES ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Horarios Disponibles", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (availableSlots.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "El profesor no tiene horarios\ndisponibles para esta fecha.",
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    availableSlots.chunked(3).forEach { rowSlots ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            rowSlots.forEach { slot ->
                                val isSelected = selectedSlotId == slot.slotId
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) PrimaryDark else Color.White)
                                        .border(
                                            1.dp,
                                            if (isSelected) PrimaryDark else Color.LightGray.copy(alpha = 0.5f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedSlotId = slot.slotId }
                                        .padding(vertical = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = slot.startTime,
                                        color = if (isSelected) Color.White else Color.DarkGray,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                            repeat(3 - rowSlots.size) { Spacer(modifier = Modifier.weight(1f)) }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- TEMA DE LA CLASE ---
                Text("¿Qué te gustaría aprender hoy?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = classTopic,
                    onValueChange = { classTopic = it },
                    placeholder = { Text("Ej: Preparación para entrevista, conversación libre...", color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryDark,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(48.dp))

                // --- BOTÓN CONFIRMAR ---
                val isFormValid = selectedSlotId != null && classTopic.isNotBlank() && !isBooking

                Button(
                    onClick = {
                        if (isFormValid) {
                            viewModel.confirmBooking(
                                studentId = studentId,
                                slotId = selectedSlotId!!,
                                topic = classTopic,
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("¡Clase agendada con éxito!")
                                        onReservationSuccess()
                                    }
                                },
                                onError = { errorMsg ->
                                    scope.launch { snackbarHostState.showSnackbar(errorMsg) }
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) PrimaryDark else Color.LightGray.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (isFormValid) 4.dp else 0.dp
                    ),
                    enabled = isFormValid
                ) {
                    Text(
                        text = if (isBooking) "Procesando..." else "Confirmar y Pagar (50 Créditos)",
                        color = if (isFormValid) Color.White else Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // 🌟 DIÁLOGO DEL CALENDARIO
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                        }
                        showDatePicker = false
                    }) { Text("Seleccionar", color = PrimaryDark, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancelar", color = Color.Gray) }
                },
                colors = DatePickerDefaults.colors(containerColor = Color.White)
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = PrimaryDark,
                        selectedDayContentColor = Color.White,
                        todayDateBorderColor = PrimaryDark,
                        todayContentColor = PrimaryDark
                    )
                )
            }
        }
    }
}