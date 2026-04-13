package com.utez.nextwordmobile.ui.screens.teacher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
// import com.utez.nextwordmobile.viewModel.teacherViewModel.TeacherClassesViewModel

@Composable
fun TeacherClassesScreen(
    paddingValues: PaddingValues,
    // viewModel: TeacherClassesViewModel // Descomenta cuando lo conectes
) {

    val filters = listOf("Todas","Pendientes", "Completadas", "Canceladas", "Reprogramadas")
    var selectedFilter by remember { mutableStateOf(filters[0]) }

    // Simulación de carga y datos (Reemplazar con estados del ViewModel)
    val isLoading = false
    val classesList = emptyList<Any>() // Reemplaza Any con tu ReservationResponseDto

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        // 1. HEADER CORTO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NextWordGradient)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Mis Clases",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 2. FILTROS (Chips horizontales)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) PrimaryDark else Color.White)
                        .border(
                            1.dp,
                            if (isSelected) PrimaryDark else Color.LightGray.copy(alpha = 0.5f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            selectedFilter = filter
                            // 🌟 AQUÍ LLAMARÍAS AL BACKEND PARA FILTRAR
                            // viewModel.fetchClassesByStatus(filter)
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // 3. LISTA DE CLASES
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryDark)
            }
        } else if (classesList.isEmpty()) {
            // ESTADO VACÍO
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (selectedFilter == "Canceladas") Icons.Default.EventBusy else Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No hay clases $selectedFilter",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Las clases que coincidan con este filtro aparecerán aquí.",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        } else {
            // RENDERIZAR TARJETAS (Descomenta y adapta cuando tengas la lista real)
            /*
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(classesList) { clase ->
                    ClassDetailCard(
                        studentName = clase.studentName,
                        topic = clase.topic,
                        date = clase.date,
                        time = "${clase.startTime} - ${clase.endTime}",
                        status = selectedFilter
                    )
                }
            }
            */
        }
    }
}

// ==========================================
// COMPONENTE: TARJETA DE CLASE DETALLADA
// ==========================================
@Composable
fun ClassDetailCard(studentName: String, topic: String, date: String, time: String, status: String) {

    // Configuración de colores según el estado
    val statusColor = when (status.lowercase()) {
        "pendientes" -> Color(0xFFF2994A) // Naranja
        "completadas" -> Color(0xFF2E7D32) // Verde
        "canceladas" -> Color(0xFFD32F2F) // Rojo
        "reprogramadas" -> Color(0xFF1976D2) // Azul
        else -> Color.Gray
    }

    val statusBgColor = when (status.lowercase()) {
        "pendientes" -> Color(0xFFFFF4E5)
        "completadas" -> Color(0xFFE8F5E9)
        "canceladas" -> Color(0xFFFFEBEE)
        "reprogramadas" -> Color(0xFFE3F2FD)
        else -> Color(0xFFF5F5F5)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { /* Abrir opciones de clase */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila Superior: Nombre y Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).background(Color(0xFFE3E8FA), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(studentName.take(1).uppercase(), color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(studentName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Estudiante", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                // Badge de Estado
                Box(
                    modifier = Modifier.background(statusBgColor, RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(status.uppercase(), color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Fila Inferior: Tema, Fecha y Hora
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(topic.ifBlank { "Clase General" }, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(date, fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(time, fontSize = 13.sp, color = Color.Gray)
                }
            }
        }
    }
}