package com.utez.nextwordmobile.ui.screens.admin

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.utez.nextwordmobile.R
import com.utez.nextwordmobile.ui.components.AdminBottomNavItem // 🌟 Import vital para navegación
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.adminViewModel.AdminHomeViewModel
import com.utez.nextwordmobile.viewModel.adminViewModel.AdminUpdateProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AdminHomeScreen(
    navController: NavController,
    viewModel: AdminHomeViewModel = viewModel(),
    updateProfileViewModel: AdminUpdateProfileViewModel,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isRefreshing by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }



    LaunchedEffect(Unit) {
        viewModel.fetchAdminStats(context)
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            coroutineScope.launch {
                viewModel.fetchAdminStats(context)
                isRefreshing = false
            }
        }
    )

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

                    // AVATAR ADMIN
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .clickable { showProfileDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AD", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                        text = "Panel de Administración",
                        color = Color.White,
                        fontSize = 22.sp,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Supervisa la actividad de profesores, alumnos e ingresos de la plataforma.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            // 🌟 CONTROL DE CARGA INICIAL
            if (uiState.isLoading && !isRefreshing) {
                // Muestra el spinner de carga en el centro si es la primera vez
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryDark)
                }
            } else {
                // 3. ESTADÍSTICAS REALES
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .offset(y = (-20).dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminStatCard(
                        value = uiState.stats.activeProfessors.toString(),
                        label = "Profes\nActivos",
                        icon = Icons.Default.CoPresent,
                        iconColor = Color(0xFF4A90E2),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        value = uiState.stats.classesToday.toString(),
                        label = "Clases\nHoy",
                        icon = Icons.Default.Event,
                        iconColor = Color(0xFFFFB400),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        value = "$${String.format("%,.0f", uiState.stats.monthlyIncome)}",
                        label = "Ingresos\nMes",
                        icon = Icons.Default.AttachMoney,
                        iconColor = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 4. SECCIÓN: GESTIÓN RÁPIDA (🌟 TEXTOS RESALTADOS EN NEGRO)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Accesos Directos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold, // 🌟 Más fuerte
                        color = Color(0xFF111827) // 🌟 Negro sólido para resaltar
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AdminShortcutRow("Directorio de Usuarios", Icons.Default.People) {
                            navController.navigate(AdminBottomNavItem.Usuarios.route) {
                                popUpTo(AdminBottomNavItem.Inicio.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                        AdminShortcutRow("Historial de Clases", Icons.Default.EventNote) {
                            navController.navigate(AdminBottomNavItem.Clases.route) {
                                popUpTo(AdminBottomNavItem.Inicio.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                        AdminShortcutRow("Reportes Financieros", Icons.Default.BarChart) {
                            navController.navigate(AdminBottomNavItem.Reportes.route) {
                                popUpTo(AdminBottomNavItem.Inicio.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 5. SECCIÓN: RESUMEN DE LA SEMANA (🌟 TEXTOS RESALTADOS EN NEGRO)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Resumen de la Semana",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold, // 🌟 Más fuerte
                        color = Color(0xFF111827) // 🌟 Negro sólido para resaltar
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AdminSummaryRow("Alumnos nuevos", "+${uiState.stats.newStudentsThisWeek}", Color(0xFF4CAF50))
                        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                        AdminSummaryRow("Clases completadas", uiState.stats.completedClassesThisWeek.toString(), Color.DarkGray)
                        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                        AdminSummaryRow("Clases canceladas", uiState.stats.cancelledClassesThisWeek.toString(), Color(0xFFE53935))
                        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                        AdminSummaryRow("Ingresos", "$${String.format("%,.0f", uiState.stats.weeklyIncome)}", Color(0xFF4CAF50))
                    }
                }

                Spacer(modifier = Modifier.height(100.dp)) // Espacio para la BottomBar
            }
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

    if (showProfileDialog) {
        AdminProfileScreen(
            currentUser = uiState.stats,
            viewModel = updateProfileViewModel,
            onDismiss = { showProfileDialog = false },
            onProfileUpdated = {
                showProfileDialog = false
                viewModel.fetchAdminStats(context) // Recarga los datos al cerrar
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Perfil actualizado correctamente",
                        duration = SnackbarDuration.Short
                    )
                }
            },
            onLogout = onLogout
        )
    }
}

// ==========================================
// COMPONENTES AUXILIARES PARA EL ADMIN
// ==========================================

@Composable
fun AdminStatCard(value: String, label: String, icon: ImageVector, iconColor: Color, modifier: Modifier = Modifier) {
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

@Composable
fun AdminShortcutRow(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(Color(0xFFE3E8FA), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontWeight = FontWeight.Medium, fontSize = 15.sp, modifier = Modifier.weight(1f), color = Color(0xFF111827))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}

@Composable
fun AdminSummaryRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}