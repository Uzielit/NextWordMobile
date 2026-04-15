package com.utez.nextwordmobile.ui.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Class
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.R
import com.utez.nextwordmobile.data.remote.dto.adminDto.ClassHistoryResponse
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.adminViewModel.AdminClassesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminClassesScreen(viewModel: AdminClassesViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 🌟 ESTADOS PARA BÚSQUEDA Y BOTTOM SHEET
    var searchQuery by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf<ClassHistoryResponse?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.fetchClassHistory(context)
    }

    // 🌟 LÓGICA DE FILTRADO MÁGICO
    val filteredClasses = uiState.classes.filter {
        it.topic.contains(searchQuery, ignoreCase = true) ||
                it.studentName.contains(searchQuery, ignoreCase = true) ||
                it.teacherName.contains(searchQuery, ignoreCase = true) ||
                it.status.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))
        ) {
            // 1. HEADER AZUL CON LOGO
            Box(
                modifier = Modifier.fillMaxWidth().background(NextWordGradient).statusBarsPadding().padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Image(painter = painterResource(id = R.drawable.nexwordlogo), contentDescription = "Logo", modifier = Modifier.height(24.dp).width(100.dp), contentScale = ContentScale.Fit)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Historial de Clases", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // 🌟 2. BARRA DE BÚSQUEDA
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                placeholder = { Text("Buscar por tema, alumno o profe...", color = Color.Gray) },
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

            // 3. LISTA DE CLASES
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.isLoading) {
                    item { Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryDark) } }
                } else if (filteredClasses.isEmpty()) {
                    item {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No se encontraron coincidencias." else "No hay clases registradas en el sistema.",
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                } else {
                    items(filteredClasses) { clase ->
                        ClassHistoryCard(
                            clase = clase,
                            onClick = { selectedClass = clase } // 🌟 ABRIR PESTAÑA AL HACER CLIC
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }

        // 🌟 4. EL BOTTOM SHEET DE DETALLES
        if (selectedClass != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedClass = null },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                ClassDetailsSheet(clase = selectedClass!!)
            }
        }
    }
}

// === COMPONENTES ===

@Composable
fun ClassHistoryCard(clase: ClassHistoryResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }, // 🌟 HACEMOS LA TARJETA CLICKEABLE
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
                        Text(text = clase.topic, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827), maxLines = 1)
                        Text(text = clase.datetime, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                // Colores dinámicos adaptados a las reservas
                val (statusBg, statusColor) = when (clase.status.lowercase()) {
                    "completada" -> Pair(Color(0xFFEAF3DE), Color(0xFF3B6D11)) // Verde
                    "cancelada" -> Pair(Color(0xFFFCEBEB), Color(0xFFA32D2D)) // Rojo
                    "confirmada" -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1)) // Azul
                    else -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309)) // Naranja para Pendiente
                }

                Box(modifier = Modifier.background(statusBg, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(text = clase.status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(12.dp))

            // Fila inferior: Alumno y Profesor
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Alumno", fontSize = 11.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = clase.studentName, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827), maxLines = 1)
                    }
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(text = "Profesor", fontSize = 11.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Class, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = clase.teacherName, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827), maxLines = 1)
                    }
                }
            }
        }
    }
}

// 🌟 NUEVO: EL CONTENIDO DEL BOTTOM SHEET
@Composable
fun ClassDetailsSheet(clase: ClassHistoryResponse) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFE3E8FA)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.MenuBook, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = clase.topic, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
        Text(text = "Detalles de la Reserva", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // Bloque de información
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                DetailRow("Fecha y Hora:", clase.datetime)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))
                DetailRow("Alumno:", clase.studentName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))
                DetailRow("Profesor:", clase.teacherName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))
                DetailRow("Estatus Actual:", clase.status)
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
