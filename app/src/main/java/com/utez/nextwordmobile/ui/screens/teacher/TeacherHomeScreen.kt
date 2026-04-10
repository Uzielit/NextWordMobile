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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utez.nextwordmobile.R
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.teacherViewModel.TeacherHomeViewModel
// IMPORTANTE: Asegúrate de importar tu ViewModel correcto aquí
// import com.utez.nextwordmobile.viewModel.teacherViewModel.TeacherProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TeacherHomeScreen(
    paddingValues: PaddingValues,
    onNavigateToClasses: () -> Unit,
    onNavigateToMessages: () -> Unit,
    viewModel: TeacherHomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val teacherProfile by viewModel.teacherProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    // val agendaList by viewModel.agendaList.collectAsState() // Lo usaremos después

    val nombreProfesor = teacherProfile?.fullName ?: "Cargando..."
    val primerNombre = nombreProfesor.substringBefore(" ")
    val valoracion = teacherProfile?.averageRating ?: 0.0
    val clasesHoyCount = 0

    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            coroutineScope.launch {

                 viewModel.fetchMyProfile(context)
                //viewModel.fetchMyAgenda(context)
                delay(800)
                isRefreshing = false
            }
        }
    )

    // Carga inicial
    LaunchedEffect(Unit) {
       viewModel.fetchMyProfile(context)
        // viewModel.fetchMyAgenda(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
        ) {
            // 1. EL HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(NextWordGradient)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nexwordlogo),
                        contentDescription = "Logo",
                        modifier = Modifier.height(30.dp).width(120.dp),
                        contentScale = ContentScale.Fit
                    )

                    // AVATAR CON INICIALES REALES
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .clickable { /* Abrir configuración de profesor */ },
                        contentAlignment = Alignment.Center
                    ) {
                        val iniciales = if (nombreProfesor != "Cargando..." && nombreProfesor.contains(" ")) {
                            val partes = nombreProfesor.split(" ")
                            "${partes[0].take(1)}${partes[1].take(1)}".uppercase()
                        } else "PR"
                        Text(iniciales, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. TARJETA DE BIENVENIDA
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-40).dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3B5998)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Bienvenido, Prof. $primerNombre",
                        color = Color.White,
                        fontSize = 22.sp,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Revisa tu agenda y prepárate para impartir tus clases del día.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            // 3. ESTADÍSTICAS REALES
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-20).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    value = clasesHoyCount.toString(),
                    label = "Clases\npara Hoy",
                    icon = Icons.Default.Event,
                    iconColor = Color(0xFF4A90E2),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = if (valoracion > 0.0) valoracion.toString() else "Nuevo", // 🌟 Si es nuevo, dice "Nuevo" en vez de 0.0
                    label = "Mi\nValoración",
                    icon = Icons.Default.Star,
                    iconColor = Color(0xFFFFB400),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. SECCIÓN: CLASES DE HOY
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Clases de Hoy", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Ver agenda", fontSize = 12.sp, color = PrimaryDark, modifier = Modifier.clickable { onNavigateToClasses() })
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🌟 RENDERIZADO CONDICIONAL DE CLASES REALES
            if (clasesHoyCount > 0) {
                // Aquí deberías hacer un forEach de tu `agendaList`
                /* agendaList.forEach { clase ->
                    TeacherClassCard(...)
                } */
            } else {
                // Estado vacío: Sin clases
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryDark.copy(alpha = 0.05f)),
                    border = BorderStroke(1.dp, PrimaryDark.copy(alpha = 0.1f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EventAvailable, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No tienes clases programadas para hoy", fontWeight = FontWeight.Bold, color = Color.DarkGray, textAlign = TextAlign.Center)
                        Text("Aprovecha para actualizar tus horarios disponibles.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 5. SECCIÓN: MENSAJES RECIENTES
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Mensajes Recientes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Ver todos", fontSize = 12.sp, color = PrimaryDark, modifier = Modifier.clickable { onNavigateToMessages() })
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    // Aquí recorrerías tus mensajes reales
                    RecentMessageRow(name = "Carlos Rodríguez", message = "Podemos repasar los temas de...", time = "Hace 5 min", unread = true)
                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    RecentMessageRow(name = "Ana Sofía", message = "Muchas gracias por la clase profe.", time = "Ayer", unread = false)
                }
            }

            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding() + 40.dp))
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.White,
            contentColor = PrimaryDark,
            scale = true
        )
    }
}

// ==========================================
// COMPONENTES AUXILIARES PARA EL PROFESOR
// ==========================================

@Composable
fun TeacherClassCard(studentName: String, time: String, duration: String, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp).clickable { /* Abrir detalle de clase */ },
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
                Text(studentName.take(1).uppercase(), color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Clase de Inglés", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(studentName, fontSize = 14.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$time • $duration", fontSize = 12.sp, color = PrimaryDark, fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier.background(Color(0xFFFFF4E5), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(status, color = Color(0xFFF2994A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RecentMessageRow(name: String, message: String, time: String, unread: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { /* Navegar al chat */ }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(Color(0xFFE3E8FA), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(name.take(1).uppercase(), color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = if (unread) FontWeight.Bold else FontWeight.Medium, fontSize = 15.sp)
            Text(
                text = message,
                fontSize = 13.sp,
                color = if (unread) Color.DarkGray else Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(time, fontSize = 11.sp, color = Color.LightGray, modifier = Modifier.padding(top = 2.dp))
        }

        if (unread) {
            Box(modifier = Modifier.size(10.dp).background(PrimaryDark, CircleShape))
        }
    }
}

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
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = label, fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center, lineHeight = 12.sp)
        }
    }
}