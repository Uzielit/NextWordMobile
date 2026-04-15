package com.utez.nextwordmobile.ui.screens.teacher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.R
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationResponseDto
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.teacherViewModel.TeacherClassesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherClassesScreen(
    paddingValues: PaddingValues,
    viewModel: TeacherClassesViewModel
) {
    val filters = listOf("Todas", "Pendientes", "Completadas", "Canceladas", "Reprogramadas")
    var selectedFilter by remember { mutableStateOf(filters[0]) }

    val currentFilter by viewModel.currentFilter.collectAsState()
    val classesList by viewModel.classesList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 🌟 ESTADOS PARA BÚSQUEDA Y BOTTOM SHEET
    var searchQuery by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf<ReservationResponseDto?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 🌟 LÓGICA DE FILTRADO MÁGICO (Búsqueda + Filtro de estado)
    val filteredClasses = classesList.filter {
        (it.topic ?: "").contains(searchQuery, ignoreCase = true) ||
                (it.studentName ?: "").contains(searchQuery, ignoreCase = true) ||
                (it.status ?: "").contains(searchQuery, ignoreCase = true)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NextWordGradient)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.nexwordlogo),
                            contentDescription = "Logo",
                            modifier = Modifier.height(24.dp).width(100.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Mis Clases", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // 🌟 2. BARRA DE BÚSQUEDA ESTILO ADMIN
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp),
                placeholder = { Text("Buscar por tema o alumno...", color = Color.Gray) },
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

            // 3. FILTROS (Chips horizontales)
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
                                viewModel.setFilter(filter) // 🌟 Llamamos al ViewModel para filtrar
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

            // 4. LISTA DE CLASES Y ESTADOS VACÍOS
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryDark)
                }
            } else if (filteredClasses.isEmpty()) {
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
                        text = if (searchQuery.isNotEmpty()) "No se encontraron coincidencias." else "No hay clases $selectedFilter",
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
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredClasses) { clase ->
                        TeacherClassHistoryCard(
                            clase = clase,
                            onClick = { selectedClass = clase } // 🌟 ABRIR BOTTOM SHEET
                        )
                    }
                }
            }
        }

        // 🌟 5. EL BOTTOM SHEET DE DETALLES
        if (selectedClass != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedClass = null },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                TeacherClassDetailsSheet(clase = selectedClass!!)
            }
        }
    }
}

// ==========================================
// COMPONENTES: TARJETA Y BOTTOM SHEET
// ==========================================

@Composable
fun TeacherClassHistoryCard(clase: ReservationResponseDto, onClick: () -> Unit) {
    // Colores dinámicos adaptados a los estados
    val (statusBg, statusColor) = when (clase.status?.lowercase() ?: "") {
        "completada", "completado" -> Pair(Color(0xFFEAF3DE), Color(0xFF3B6D11)) // Verde
        "cancelada", "cancelado" -> Pair(Color(0xFFFCEBEB), Color(0xFFA32D2D)) // Rojo
        "confirmada", "confirmado" -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1)) // Azul
        "reprogramada" -> Pair(Color(0xFFE3F2FD), Color(0xFF1976D2)) // Azul claro
        else -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309)) // Naranja para Pendiente
    }

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
                        Text(text = clase.topic ?: "Clase de Inglés", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827), maxLines = 1)
                        Text(text = "${clase.date ?: "--"} • ${clase.startTime ?: ""}", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Box(modifier = Modifier.background(statusBg, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(text = clase.status ?: "Pendiente", color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(12.dp))

            // Fila inferior: Alumno
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Alumno", fontSize = 11.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = clase.studentName ?: "Alumno", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827), maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherClassDetailsSheet(clase: ReservationResponseDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFE3E8FA)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.MenuBook, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = clase.topic ?: "Clase de Inglés", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827), textAlign = TextAlign.Center)
        Text(text = "Detalles de la Reserva", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // Bloque de información
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow("Fecha:", clase.date ?: "--")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))
                DetailRow("Horario:", "${clase.startTime ?: "--"} - ${clase.endTime ?: "--"}")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))
                DetailRow("Alumno:", clase.studentName ?: "No especificado")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))
                DetailRow("Estatus Actual:", clase.status ?: "Pendiente")
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