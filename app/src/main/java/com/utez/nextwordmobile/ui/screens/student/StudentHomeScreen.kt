package com.utez.nextwordmobile.ui.screens.student

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.R
import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherDto
import com.utez.nextwordmobile.viewModel.studentViewModel.StudenReservationViewModel
import com.utez.nextwordmobile.viewModel.studentViewModel.StudentProfileViewModel

@Composable
fun StudentHomeScreen(
    paddingValues: PaddingValues,
    onNavigateToSearch: () -> Unit,
    onNavigateToCalendar: (String, String, String) -> Unit,
    StudentProfileViewModel: StudentProfileViewModel = viewModel()

) {
    val context = LocalContext.current

    val studentProfile by StudentProfileViewModel.studentProfile.collectAsState()
    val teachers by StudentProfileViewModel.teachersList.collectAsState()
    val agendaList by StudentProfileViewModel.agendaList.collectAsState()
    val historialList by StudentProfileViewModel.historialList.collectAsState()



    // Datos del Alumno
    val nombreAlumno = studentProfile?.fullName ?: "Cargando..."
    val primerNombre = nombreAlumno.substringBefore(" ")
    val creditos = studentProfile?.walletBalance ?: 0.0

    // Estados de las ventanas flotantes (BottomSheets)
    var selectedTeacher by remember { mutableStateOf<TeacherDto?>(null) }
    var showTeacherProfile by remember { mutableStateOf(false) }
    var showReservationDetails by remember { mutableStateOf(false) }


    // LÓGICA DE LA PRÓXIMA CLASE
    val nextClass = agendaList.firstOrNull()
    val hasUpcomingClass = nextClass != null

    val nextClassTeacher = nextClass?.participantName ?: ""
    val nextClassDate = nextClass?.date ?: "--"
    val nextClassTime = nextClass?.startTime ?: "--"
    val clasesTomadas = historialList.size


    LaunchedEffect(Unit) {
        StudentProfileViewModel.fetchMyProfile(context)
        StudentProfileViewModel.fetchTeachers(context)
        StudentProfileViewModel.fetchMyAgenda(context)
        StudentProfileViewModel.fetchMyHistory(context)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        //  EL HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp) // Aumentamos la altura
                .background(NextWordGradient)
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .padding( top = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top // Alineamos arriba
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nexwordlogo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(30.dp)
                        .width(120.dp),
                    contentScale = ContentScale.Fit
                )

                // Avatar (Ya no será tapado)
                Box(
                    modifier = Modifier
                        .size(45.dp) // Ligeramente más grande
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("UZ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = (-40).dp), // Subimos un poco para el efecto de superposición
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF3B5998)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) { // Más padding interno
                Text(
                    text = "Bienvenido, $primerNombre",
                    color = Color.White,
                    fontSize = 22.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold
                )
                Text("Continúa tu aprendizaje.", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onNavigateToSearch, // Sigue yendo a buscar
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth() // Botón más ancho
                ) {
                    Text("Buscar Profesores", color = Color(0xFF3B5998), fontWeight = FontWeight.Bold) // Cambié el texto para dar más sentido
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = (-20).dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                value = if (hasUpcomingClass) nextClassDate else "--",
                label = "Próxima\nClase",
                icon = Icons.Default.CalendarMonth,
                iconColor = Color(0xFF4A90E2),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                creditos.toString(),
                "Creditos\nDisponibles",
                Icons.Default.AccountBalanceWallet,
                Color(0xFF50E3C2),
                Modifier.weight(1f)
            )
            StatCard(
                value = clasesTomadas.toString(),
                label = "Capacitaciones\nTomadas",
                icon = Icons.Default.Star,
                iconColor = Color(0xFFB864F2),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🌟 4. PRÓXIMA CLASE (Nueva sección para llenar el espacio vacío)
        Text(
            text = "Tu Próxima Clase",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))



        if (hasUpcomingClass) {
            val parts = nextClassTeacher.split(" ")
            val initialsClass = if (parts.size > 1) "${parts[0].take(1)}${parts[1].take(1)}" else parts[0].take(2)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable { showReservationDetails = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(50.dp).background(Color(0xFFE3E8FA), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initialsClass.uppercase(), color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Inglés", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(nextClassTeacher, fontSize = 14.sp, color = Color.Gray)
                        Text("$nextClassDate • $nextClassTime", fontSize = 14.sp, color = PrimaryDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // ESTADO VACÍO
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryDark.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, PrimaryDark.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No tienes clases programadas", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Text("Explora los profesores y agenda tu primera clase.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        //  5. PROFESORES RECOMENDADOS
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Profesores Recomendados", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "Ver todos", fontSize = 12.sp, color = PrimaryDark, modifier = Modifier.clickable { onNavigateToSearch() })
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(teachers.size) { index ->
                val teacher = teachers[index]

                val parts = teacher.fullName.split(" ")
                val initials = if (parts.size > 1) "${parts[0].take(1)}${parts[1].take(1)}" else parts[0].take(2)

                RecommendedTeacher(
                    initials = initials.uppercase(),
                    name = teacher.fullName,
                    rating = teacher.averageRating.toString(),
                    onClick = {

                        selectedTeacher = teacher
                        showTeacherProfile = true
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding() + 40.dp))
    }

    if (showTeacherProfile ) {
        TeacherDetailBottomSheet(
            teacher = selectedTeacher!!, // Pasamos el objeto completo
            studentId = studentProfile?.id ?: "",
            onDismiss = { showTeacherProfile = false },
            onNavigateToCalendar = { tId, tName, sId ->
                showTeacherProfile = false
                onNavigateToCalendar(tId, tName, sId)
            }
        )
    }

    if (showReservationDetails && nextClass != null) {
        ReservationDetailBottomSheet(
            reservation = nextClass,
            onDismiss = { showReservationDetails = false }
        )
    }
}

// ==========================================
// COMPONENTES AUXILIARES ACTUALIZADOS
// ==========================================
@Composable
fun StatCard(value: String, label: String, icon: ImageVector, iconColor: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = label, fontSize = 10.sp, color = Color.Gray, textAlign = Center, lineHeight = 11.sp)
        }
    }
}

@Composable
fun RecommendedTeacher(initials: String, name: String, rating: String, onClick: () -> Unit) {
    // 🌟 REVISIÓN MINIMALISTA: Diseño vertical Cupertino-style
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() } // Clic abre perfil
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(PrimaryDark.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = initials, color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        // 🌟 REVISIÓN: Estrellas de rating
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = rating, fontSize = 10.sp, color = Color.Gray)
        }
    }
}