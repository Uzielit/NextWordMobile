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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark

import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utez.nextwordmobile.R
import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherDto
import com.utez.nextwordmobile.viewModel.studentViewModel.StudentProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentSearchScreen(
    paddingValues: PaddingValues,
    onNavigateToCalendar: (String, String, String) -> Unit,
    viewModel: StudentProfileViewModel = viewModel()

) {
    val context = LocalContext.current

    val studentProfile by viewModel.studentProfile.collectAsState()
    val teachers by viewModel.teachersList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    // Estados para saber a qué profe tocaste
    var selectedTeacher by remember { mutableStateOf<TeacherDto?>(null) }

    var selectedTeacherId by remember { mutableStateOf("") }
    var selectedTeacherName by remember { mutableStateOf("") }
    var showTeacherDetails by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchTeachers(context)
        viewModel.fetchMyProfile(context)
    }


    val filteredTeachers = teachers.filter {
        it.fullName.contains(searchQuery, ignoreCase = true) ||
                (it.specialization?.contains(searchQuery, ignoreCase = true) == true)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NextWordGradient)
                    .padding(top = 40.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nexwordlogo),
                    contentDescription = "Logo",
                    modifier = Modifier.height(40.dp).width(140.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por nombre o especialidad...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.LightGray
                ),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. LISTA DINÁMICA DE PROFESORES
            LazyColumn(
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                if (filteredTeachers.isEmpty()) {
                    item {
                        Text(
                            text = "No se encontraron profesores.",
                            color = Color.Gray,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                } else {
                    items(filteredTeachers.size) { index ->
                        val teacher = filteredTeachers[index]
                        TeacherCard(
                            teacher = teacher,
                            onVerHorariosClick = {
                                // 🌟 AQUÍ ESTABA EL ERROR: Faltaba guardar el objeto
                                selectedTeacher = teacher
                                showTeacherDetails = true
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }


        // 3. LLAMAMOS AL BOTTOM SHEET UNIFICADO
    if (showTeacherDetails && selectedTeacher != null) {
        TeacherDetailBottomSheet(
            teacher = selectedTeacher!!,
            // OTRO SEGURO: Si el perfil tarda en cargar, mandamos "SIN_ID" en lugar de "" para que no explote la navegación
            studentId = studentProfile?.id?.takeIf { it.isNotBlank() } ?: "SIN_ID",
            onDismiss = { showTeacherDetails = false },
            onNavigateToCalendar = { tId, tName, sId ->
                showTeacherDetails = false
                onNavigateToCalendar(tId, tName, sId)
            }
        )
    }
}
}

@Composable
fun TeacherCard(teacher: TeacherDto, onVerHorariosClick: () -> Unit) {

    val parts = teacher.fullName.split(" ")
    val initials = if (parts.size > 1) "${parts[0].take(1)}${parts[1].take(1)}" else parts[0].take(2)

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
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
                        modifier = Modifier.size(60.dp).background(Color(0xFFE3E8FA), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials.uppercase(), color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(teacher.specialization ?: "Inglés", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(teacher.fullName, fontSize = 14.sp, color = Color.DarkGray)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(14.dp))
                            Text(" ${teacher.averageRating}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("50", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Créditos", fontSize = 12.sp, color = Color.Gray)
                }
            }

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