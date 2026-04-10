package com.utez.nextwordmobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.ui.components.AuthToggle
import com.utez.nextwordmobile.ui.components.NextWordButton
import com.utez.nextwordmobile.ui.components.NextWordTextField
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.R
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utez.nextwordmobile.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import android.util.Patterns
import com.utez.nextwordmobile.data.remote.SessionManager
import kotlinx.coroutines.CoroutineScope
import java.util.TimeZone



//PRINCIPAL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToHome: (Int) -> Unit = {},
    onNavigateToVerification: (String) -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {}
) {
    var showLoginFields by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NextWordGradient)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Image(
                    painter = painterResource(id = R.drawable.nexwordlogo),
                    contentDescription = "Logo",
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .size(170.dp)
                        .padding(bottom = 10.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AuthToggle(
                            isLoginSelected = showLoginFields,
                            onOptionSelected = { showLoginFields = it }
                        )

                        Spacer(modifier = Modifier.height(24.dp))


                        if (showLoginFields) {
                            LoginSection(
                                viewModel = viewModel,
                                onNavigateToHome = onNavigateToHome,
                                snackbarHostState = snackbarHostState,
                                scope = scope,
                                onNavigateToForgotPassword = onNavigateToForgotPassword
                            )
                        } else {
                            RegisterSection(viewModel, onNavigateToVerification, snackbarHostState, scope)
                        }
                    }
                }
            }
        }
    }
}


// SECCIÓN DE INICIAR SESIÓN

@Composable
fun LoginSection(
    viewModel: AuthViewModel,
    onNavigateToHome: (Int) -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onNavigateToForgotPassword: () -> Unit
) {
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var errorMail by remember { mutableStateOf<String?>(null) }
    var errorPassword by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    Column {
        NextWordTextField(
            value = loginEmail,
            onValueChange = {
                loginEmail = it
                errorMail = null
            },
            label = "Email*",
            isError = errorMail != null,
            errorMessage = errorMail
        )
        Spacer(modifier = Modifier.height(16.dp))
        NextWordTextField(
            value = loginPassword,
            onValueChange = {
                loginPassword = it
                errorPassword = null
            },
            label = "Contraseña*",
            isError = errorPassword != null,
            errorMessage = errorPassword,
            isPassword = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                Text("Recordarme", fontSize = 12.sp)
            }
            Text(
                text = "¿Olvidaste tu\ncontraseña?",
                fontSize = 12.sp,
                color = PrimaryDark,
                modifier = Modifier.clickable {
                    onNavigateToForgotPassword()
                }

            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        NextWordButton(text = "Iniciar Sesión", onClick = {
            var isValid = true

            if (loginEmail.isBlank()) {
                errorMail = "Este campo es obligatorio"; isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()) {
                errorMail = "Ingresa un correo electrónico válido."; isValid = false
            }
            if (loginPassword.isBlank()) {
                errorPassword = "Este campo es obligatorio"; isValid = false
            } else if (loginPassword.length < 8) {
                errorPassword = "Mínimo 8 caracteres."; isValid = false
            }
            if (isValid) {
                viewModel.login(
                    email = loginEmail,
                    password = loginPassword,
                    onSuccess = { token, roleId ->
                        sessionManager.saveAuthToken(token)
                        onNavigateToHome(roleId)},
                    onError = { errorMensaje ->
                        scope.launch { snackbarHostState.showSnackbar(errorMensaje) }
                    }
                )
            } else {
                scope.launch { snackbarHostState.showSnackbar("Por favor, completa todos los campos.") }
            }
        })
    }
}


//  SECCIÓN DE REGISTRO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterSection(
    viewModel: AuthViewModel,
    onNavigateToVerification: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    // Estados del Alumno
    var registerName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPhone by remember { mutableStateOf("") }
    var registerDob by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var registerConfirmPassword by remember { mutableStateOf("") }

    // Estados de Errores
    var nameError by remember { mutableStateOf<String?>(null) }
    var mailError by remember { mutableStateOf<String?>(null) }
    var DobError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // DatePicker State
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= System.currentTimeMillis()
            override fun isSelectableYear(year: Int): Boolean = year <= LocalDate.now().year
        }
    )

    // Estados del Tutor
    var tutorName by remember { mutableStateOf("") }
    var tutorEmail by remember { mutableStateOf("") }
    var tutorPhone by remember { mutableStateOf("") }
    val isMinor = ifMinor(registerDob)

    Column {
        Text("Tus Datos", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDark)
        Spacer(modifier = Modifier.height(8.dp))

        NextWordTextField(
            value = registerName,
            onValueChange = { registerName = it; nameError = null },
            label = "Nombre completo*",
            isError = nameError != null,
            errorMessage = nameError
        )
        Spacer(modifier = Modifier.height(16.dp))

        NextWordTextField(
            value = registerEmail,
            onValueChange = { registerEmail = it; mailError = null },
            label = "Email*",
            isError = mailError != null,
            errorMessage = mailError
        )
        Spacer(modifier = Modifier.height(16.dp))

        NextWordTextField(
            value = registerPhone,
            onValueChange = { registerPhone = it; phoneError = null },
            label = "Teléfono(10 dígitos)*",
            isError = phoneError != null,
            errorMessage = phoneError
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Input Fecha
        Box {
            NextWordTextField(
                value = registerDob,
                onValueChange = {},
                label = "Fecha de nacimiento*",
                isError = DobError != null,
                errorMessage = DobError
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clickable { showDatePicker = true }
            )
        }
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            registerDob = sdf.format(Date(millis))
                            DobError = null
                        }
                        showDatePicker = false
                    }) { Text("Aceptar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                }
            ) { DatePicker(state = datePickerState) }
        }
        Spacer(modifier = Modifier.height(16.dp))

        NextWordTextField(
            value = registerPassword,
            onValueChange = { registerPassword = it; passwordError = null },
            label = "Contraseña*",
            isPassword = true,
            isError = passwordError != null,
            errorMessage = passwordError
        )
        Spacer(modifier = Modifier.height(16.dp))

        NextWordTextField(
            value = registerConfirmPassword,
            onValueChange = { registerConfirmPassword = it; passwordError = null },
            label = "Confirmar Contraseña*",
            isPassword = true,
            isError = passwordError != null,
            errorMessage = passwordError
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Datos del Tutor
        if (isMinor) {
            Text("Datos de tu Tutor", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDark)
            Spacer(modifier = Modifier.height(8.dp))
            NextWordTextField(
                value = tutorName,
                onValueChange = {
                tutorName = it },
                label = "Nombre del tutor*")
            Spacer(modifier = Modifier.height(16.dp))
            NextWordTextField(value = tutorEmail,
                onValueChange = { tutorEmail = it },
                label = "Email del tutor*")
            Spacer(modifier = Modifier.height(16.dp))
            NextWordTextField(value = tutorPhone,
                onValueChange = { tutorPhone = it },
                label = "Teléfono del tutor*")
            Spacer(modifier = Modifier.height(24.dp))
        }

        NextWordButton(text = "Crear Cuenta", onClick = {
            var isValid = true
            if (registerName.isBlank()) { nameError = "El nombre completo es obligatorio."; isValid = false }

            if (registerEmail.isBlank()) {
                mailError = "Este campo es obligatorio"; isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(registerEmail).matches()) {
                mailError = "Ingresa un correo electrónico válido."; isValid = false
            }

            if (registerPhone.length != 10 || !registerPhone.all { it.isDigit() }) {
                phoneError = "El teléfono debe tener 10 números."; isValid = false
            } else if (registerPhone.isBlank()) {
                phoneError = "Este campo es obligatorio"; isValid = false
            }

            if (registerDob.isBlank()) {
                DobError = "La fecha de nacimiento es obligatoria."; isValid = false
            } else {
                try {
                    val fechaNac = LocalDate.parse(registerDob, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    if (fechaNac.isAfter(LocalDate.now())) { DobError = "Formato de fecha inválido."; isValid = false }
                } catch (e: Exception) { DobError = "Formato de fecha inválido."; isValid = false }
            }

            if (registerPassword.length < 8) {
                passwordError = "Mínimo 8 caracteres."; isValid = false
            } else if (registerPassword != registerConfirmPassword) {
                passwordError = "Las contraseñas no coinciden."; isValid = false
            }

            if (isValid) {
                viewModel.registerStudent(
                    email = registerEmail,
                    password = registerPassword,
                    fullname = registerName,
                    phone = registerPhone,
                    dob = registerDob,
                    tutorName = tutorName,
                    tutorEmail = tutorEmail,
                    tutorPhone = tutorPhone,
                    onSuccess = { onNavigateToVerification(registerEmail) },
                    onError = { error -> scope.launch { snackbarHostState.showSnackbar(error) } }
                )
            } else {
                scope.launch { snackbarHostState.showSnackbar("Por favor, completa todos los campos.") }
            }
        })
    }
}

fun ifMinor(fechaNacimiento: String): Boolean {
    if (fechaNacimiento.isBlank()) return false
    return try {
        val fechaNac = LocalDate.parse(fechaNacimiento, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val hoy = LocalDate.now()
        if (fechaNac.isAfter(hoy)) return false
        val edad = ChronoUnit.YEARS.between(fechaNac, hoy)
        edad < 18
    } catch (e: Exception) {
        false
    }
}


