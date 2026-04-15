package com.utez.nextwordmobile.ui.screens.admin

import android.R.attr.name
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.utez.nextwordmobile.data.remote.dto.adminDto.CreateTeacherRequest
import com.utez.nextwordmobile.data.remote.dto.adminDto.UserDirectoryResponse
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.adminViewModel.AdminUsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    viewModel: AdminUsersViewModel,
    onNavigateToClasses: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Profesores", "Alumnos")

    // Controles para los BottomSheets
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCreateSheet by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<UserDirectoryResponse?>(null) } // Controla el sheet de detalles

    LaunchedEffect(Unit) {
        viewModel.fetchUsers(context)
    }
    LaunchedEffect(uiState.createMessage) {
        if (uiState.createMessage == "Profesor creado exitosamente") {
            showCreateSheet = false
            viewModel.clearMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))
        ) {
            // 1. HEADER AZUL CON LOGO
            Box(
                modifier = Modifier.fillMaxWidth().background(NextWordGradient).statusBarsPadding().padding(horizontal = 24.dp, vertical = 16.dp)
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
                        Text("Directorio de Usuarios", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // 2. PESTAÑAS (Tabs)
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = PrimaryDark,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]), color = PrimaryDark, height = 3.dp)
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal, color = if (selectedTabIndex == index) PrimaryDark else Color.Gray) }
                    )
                }
            }

            // 3. LISTA DE USUARIOS
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val filteredUsers = uiState.users.filter { if (selectedTabIndex == 0) it.roleId == 2 else it.roleId == 1 }

                if (uiState.isLoading) {
                    item { CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally)) }
                } else if (filteredUsers.isEmpty()) {
                    item { Text("No hay usuarios en esta categoría.", color = Color.Gray) }
                } else {
                    items(filteredUsers) { user ->
                        UserItemCard(
                            user = user,
                            onClick = { selectedUser = user } // Al hacer clic, abrimos detalles
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }

        // 4. BOTÓN FLOTANTE (Agregar Profe)
        if (selectedTabIndex == 0) {
            FloatingActionButton(
                onClick = { showCreateSheet = true },
                containerColor = PrimaryDark,
                contentColor = Color.White,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 24.dp, bottom = 90.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Profesor")
            }
        }
    }

    // 5. BOTTOM SHEET: CREAR PROFESOR
    if (showCreateSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showCreateSheet = false
                viewModel.clearMessage()
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            CreateTeacherForm(
                isCreating = uiState.isCreating,
                errorMessage = uiState.createMessage, // Pasamos el mensaje del ViewModel
                onSubmit = { request ->
                    viewModel.createTeacher(context, request)
                }
            )
        }
    }

    // 6. BOTTOM SHEET: DETALLES DEL USUARIO
    if (selectedUser != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedUser = null },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            UserDetailsSheet(
                user = selectedUser!!,
                onToggleStatus = {
                    viewModel.toggleTeacherStatus(context, selectedUser!!.userId, selectedUser!!.status)
                    selectedUser = null // Cerramos tras la acción
                },
                onNavigateToClasses = {
                    selectedUser = null
                    onNavigateToClasses() // Viajamos a la pantalla de clases
                }
            )
        }
    }
}

// === COMPONENTES DE VISTA ===

@Composable
fun UserDetailsSheet(
    user: UserDirectoryResponse,
    onToggleStatus: () -> Unit,
    onNavigateToClasses: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 40.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto o Avatar Grande
        Box(
            modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFFE3E8FA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = user.fullName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
        Text(text = user.email, fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // Si es profesor, mostramos el botón de desactivar/activar
        if (user.roleId == 2) {
            val isActivo = user.status == "Activo"
            Button(
                onClick = onToggleStatus,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActivo) Color(0xFFFEE2E2) else Color(0xFFEAF3DE),
                    contentColor = if (isActivo) Color(0xFFA32D2D) else Color(0xFF3B6D11)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isActivo) "Desactivar Profesor" else "Reactivar Profesor", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Botón para ver clases (Para alumnos y profesores)
        OutlinedButton(
            onClick = onNavigateToClasses,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.History, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver Historial de Clases")
        }
    }
}


@Composable
fun UserItemCard(user: UserDirectoryResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE3E8FA)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryDark)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827))
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = user.email, fontSize = 13.sp, color = Color.Gray)
                }
            }

            val isGoodStatus = user.status == "Activo" || user.status == "Registrado"
            val statusColor = if (isGoodStatus) Color(0xFF3B6D11) else Color(0xFFA32D2D)
            val statusBg = if (isGoodStatus) Color(0xFFEAF3DE) else Color(0xFFFCEBEB)

            Box(modifier = Modifier.background(statusBg, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(text = user.status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// === COMPONENTES EXTRA ===

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTeacherForm(
    isCreating: Boolean,
    errorMessage: String?, // 🌟 NUEVO: Para recibir el mensaje de error del ViewModel
    onSubmit: (CreateTeacherRequest) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 40.dp, top = 8.dp)
    ) {
        Text("Registrar Nuevo Profesor", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
        Spacer(modifier = Modifier.height(4.dp))
        Text("Solo se permiten correos @nextword.com.mx", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        // 🌟 ZONA DE ERROR (Si el backend manda un mensaje, lo mostramos en rojo)
        if (!errorMessage.isNullOrEmpty() && errorMessage != "Profesor creado exitosamente") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFCEBEB), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(text = errorMessage, color = Color(0xFFA32D2D), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 🌟 DEFINIMOS COLORES SÓLIDOS PARA LOS TEXTFIELDS
        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryDark,
            unfocusedBorderColor = Color.LightGray,
            focusedTextColor = Color(0xFF111827), // Texto negro sólido
            unfocusedTextColor = Color(0xFF111827),
            focusedLabelColor = PrimaryDark,
            unfocusedLabelColor = Color.Gray
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña Temporal") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onSubmit(CreateTeacherRequest(name, email, password)) },
            enabled = !isCreating && name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isCreating) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                // 🌟 TEXTO BLANCO A LA FUERZA
                Text("Crear Cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
