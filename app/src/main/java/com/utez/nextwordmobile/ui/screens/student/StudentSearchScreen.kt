package com.utez.nextwordmobile.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentSearchScreen() {
    var searchQuery by remember { mutableStateOf("") }


    var showTeacherDetails by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {


        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NextWordGradient)
                    .padding(top = 40.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
            ) {
                Column {
                    Text("Next Word", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Panel de Estudiante", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar profesores") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. LISTA DE PROFESORES
            LazyColumn(
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(3) {
                    TeacherCard(
                        onVerHorariosClick = {

                            showTeacherDetails = true
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }


        if (showTeacherDetails) {
            TeacherDetailBottomSheet(
                onDismiss = { showTeacherDetails = false }
            )
        }
    }
}

@Composable
fun TeacherCard(onVerHorariosClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFE3E8FA), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("JP", color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Inglés", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Juan Pérez", fontSize = 14.sp, color = Color.DarkGray)
                        Text("45 clases", fontSize = 12.sp, color = Color.Gray)
                    }
                }


                Column(horizontalAlignment = Alignment.End) {
                    Text("50", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Créditos", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Profesor con 10 años de experiencia", fontSize = 14.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onVerHorariosClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5998)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ver Horarios Disponibles", color = Color.White)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDetailBottomSheet(onDismiss: () -> Unit) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() } 
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Cabecera del Bottom Sheet
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFE3E8FA), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("JP", color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Inglés", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Juan Pérez", fontSize = 14.sp, color = Color.DarkGray)
                    Text("45 clases", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Etiquetas de Info
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoTag("45", "Clases dadas", Modifier.weight(1f))
                InfoTag("50", "Créditos / clase", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Sobre mí", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Lingüista con doctorado y 10 años transformando el aprendizaje del idioma. Mi pasión es derribar las barreras de comunicación.",
                fontSize = 14.sp, color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🌟 APLICAMOS TU REVISIÓN: Certificaciones en lugar de Educación
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Certificaciones", fontSize = 12.sp, color = Color.Gray)
                    Text("Doctorado en Lingüística Aplicada", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Experiencia", fontSize = 12.sp, color = Color.Gray)
                    Text("10 años de experiencia", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* TODO: Pasar al calendario */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5998)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continuar a Reservar", color = Color.White)
            }
        }
    }
}

@Composable
fun InfoTag(value: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryDark)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}