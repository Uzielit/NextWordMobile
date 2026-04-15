package com.utez.nextwordmobile.ui.screens.student

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.R
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationResponseDto
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.studentViewModel.StudentClassesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentClassesScreen(
    paddingValues: PaddingValues,
    viewModel: StudentClassesViewModel,
    studentId: String
) {
    val classesList by viewModel.classesList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf<ReservationResponseDto?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context= LocalContext.current

    val filters = listOf(
        FilterOption("Todas", Color.Gray),
        FilterOption("Pendiente", Color(0xFFFFA726)),
        FilterOption("Completada", Color(0xFF66BB6A)),
        FilterOption("Cancelada", Color(0xFFEF5350))
    )


    val filteredClasses = classesList.filter {
        it.topic.contains(searchQuery, ignoreCase = true) ||
                it.teacherName.contains(searchQuery, ignoreCase = true) ||
                it.status.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // 1. HEADER AZUL CON LOGO
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp).background(NextWordGradient).statusBarsPadding().padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nexwordlogo),
                        contentDescription = "Logo",
                        modifier = Modifier.height(35.dp).width(130.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Mis Clases", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 16.dp),
                placeholder = { Text("Buscar por tema o profe...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = PrimaryDark) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryDark,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            // 3. FILTROS RÁPIDOS
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filters.size) { index ->
                    val option = filters[index]
                    val isSelected = currentFilter == option.name

                    Surface(
                        modifier = Modifier.clickable { viewModel.setFilter(option.name) },
                        shape = CircleShape,
                        color = if (isSelected) option.color else Color.White,
                        border = if (!isSelected) BorderStroke(1.dp, Color.LightGray) else null,
                        shadowElevation = if (isSelected) 4.dp else 0.dp
                    ) {
                        Text(
                            text = option.name,
                            color = if (isSelected) Color.White else Color.DarkGray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // 4. LISTA DE CLASES
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryDark)
                }
            } else if (filteredClasses.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Class, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No hay clases", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("No tienes clases con estos criterios.", fontSize = 14.sp, color = Color.LightGray, textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    items(filteredClasses.size) { index ->
                        StudentClassHistoryCard(
                            reservation = filteredClasses[index],
                            filters = filters,
                            onClick = { selectedClass = filteredClasses[index] } // 🌟 ABRIR PESTAÑA AL HACER CLIC
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        //  5. EL BOTTOM SHEET DE DETALLES Y CANCELACIÓN
        if (selectedClass != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedClass = null },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                StudentClassDetailsSheet(
                    reservation = selectedClass!!,
                    onClose = { selectedClass = null },
                    onCancelConfirm = { motivo ->

                        // 🌟 LLAMADA AL VIEWMODEL TOTALMENTE DINÁMICA
                        viewModel.cancelarClase(
                            reservationId = selectedClass!!.reservationId,
                            reason = motivo,
                            studentId = studentId,
                            onSuccess = { mensaje ->
                                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                                selectedClass = null // Cierra la pestaña
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        )

                    }
                )
            }
        }
    }
}

// === COMPONENTES SECUNDARIOS ===

@Composable
fun StudentClassHistoryCard(reservation: ReservationResponseDto, filters: List<FilterOption>, onClick: () -> Unit) {
    val statusColor = filters.find { it.name.startsWith(reservation.status, ignoreCase = true) }?.color ?: Color.Gray
    val statusBg = statusColor.copy(alpha = 0.1f)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior: Tema y Estatus
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE3E8FA)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = reservation.topic, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827), maxLines = 1)
                        Text(text = "${reservation.date} • ${reservation.startTime}", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Box(modifier = Modifier.background(statusBg, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(text = reservation.status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(12.dp))

            // Fila inferior: Profesor
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Profesor", fontSize = 11.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = reservation.teacherName, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827), maxLines = 1)
                    }
                }
            }
        }
    }
}

// 🌟 EL CONTENIDO DEL BOTTOM SHEET (CON SISTEMA DE CANCELACIÓN)
@Composable
fun StudentClassDetailsSheet(
    reservation: ReservationResponseDto,
    onClose: () -> Unit,
    onCancelConfirm: (String) -> Unit // Recibe el motivo de cancelación
) {
    var isCanceling by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isCanceling) {
            // --- ESTADO NORMAL: VER DETALLES ---
            Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFE3E8FA)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = reservation.topic, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827), textAlign = TextAlign.Center)
            Text(text = "Detalles de la Clase", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow("Fecha:", reservation.date)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))
                    DetailRow("Horario:", reservation.startTime)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))
                    DetailRow("Profesor:", reservation.teacherName)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))
                    DetailRow("Estatus:", reservation.status)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Cancelar solo si está Pendiente o Confirmada
            val canCancel = reservation.status.equals("Pendiente", ignoreCase = true) || reservation.status.equals("Pagado", ignoreCase = true )
            if (canCancel) {
                OutlinedButton(
                    onClick = { isCanceling = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA32D2D)),
                    border = BorderStroke(1.dp, Color(0xFFA32D2D))
                ) {
                    Text("Cancelar Clase", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // --- ESTADO DE CANCELACIÓN: PEDIR MOTIVO ---
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFA32D2D), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Cancelar Clase", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Text(text = "Por favor, indícanos el motivo de la cancelación. Los créditos serán devueltos a tu monedero.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = cancelReason,
                onValueChange = { cancelReason = it },
                label = { Text("Motivo de cancelación") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFA32D2D)),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onCancelConfirm(cancelReason)
                    onClose() // Cerramos el panel tras confirmar
                },
                enabled = cancelReason.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA32D2D),
                    disabledContainerColor = Color(0xFFA32D2D).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmar Cancelación", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { isCanceling = false }) { // Botón para regresar
                Text("Volver a Detalles", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Text(text = value, color = Color(0xFF111827), fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

data class FilterOption(val name: String, val color: Color)