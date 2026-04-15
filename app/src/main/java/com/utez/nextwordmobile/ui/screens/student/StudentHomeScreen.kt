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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.R
import com.utez.nextwordmobile.data.remote.RetrofitClient
import com.utez.nextwordmobile.data.remote.api.studentApi.StudentApiService
import com.utez.nextwordmobile.data.remote.dto.teacherDto.TeacherDto
import com.utez.nextwordmobile.data.repository.StudentProfileRepository
import com.utez.nextwordmobile.viewModel.studentViewModel.StudentProfileViewModel
import com.utez.nextwordmobile.viewModel.studentViewModel.StudentUpdateProfileViewModel
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.KeyboardType
import com.utez.nextwordmobile.ui.components.ReviewDialog

import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun StudentHomeScreen(
    paddingValues: PaddingValues,
    onNavigateToSearch: () -> Unit,
    onNavigateToCalendar: (String, String, String) -> Unit,
    onLogout: () -> Unit,
    StudentProfileViewModel: StudentProfileViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val studentProfile by StudentProfileViewModel.studentProfile.collectAsState()
    val teachers by StudentProfileViewModel.teachersList.collectAsState()
    val agendaList by StudentProfileViewModel.agendaList.collectAsState()
    val historialList by StudentProfileViewModel.historialList.collectAsState()

    var showProfileEditScreen by remember { mutableStateOf(false) }

    // Datos del Alumno
    val nombreAlumno = studentProfile?.fullName ?: "Cargando..."
    val primerNombre = nombreAlumno.substringBefore(" ")
    val creditos = studentProfile?.walletBalance ?: 0.0

    // Estados
    var selectedTeacher by remember { mutableStateOf<TeacherDto?>(null) }
    var showTeacherProfile by remember { mutableStateOf(false) }
    var showReservationDetails by remember { mutableStateOf(false) }
    var showRechargeSheet by remember { mutableStateOf(false) }

    val nextClass = agendaList.firstOrNull()
    val hasUpcomingClass = nextClass != null
    val nextClassTeacher = nextClass?.teacherName ?: ""
    val nextClassDate = nextClass?.date ?: "--"
    val nextClassTime = nextClass?.startTime ?: "--"
    val clasesTomadas = historialList.size


    var isRefreshing by remember { mutableStateOf(false) }

    val classToReview = remember(historialList) {
        historialList.find { it.status == "COMPLETED" }
    }

    var showReviewModal by remember { mutableStateOf(false) }

    LaunchedEffect(classToReview) {
        if (classToReview != null) {
            showReviewModal = true
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            coroutineScope.launch {
                StudentProfileViewModel.fetchMyProfile(context)
                StudentProfileViewModel.fetchTeachers(context)
                StudentProfileViewModel.fetchMyAgenda(context)
                StudentProfileViewModel.fetchMyHistory(context)
                delay(800)
                isRefreshing = false
            }
        }
    )
    LaunchedEffect(Unit) {
        StudentProfileViewModel.fetchMyProfile(context)
        StudentProfileViewModel.fetchTeachers(context)
        StudentProfileViewModel.fetchMyAgenda(context)
        StudentProfileViewModel.fetchMyHistory(context)
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
            // EL HEADER
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
                    Image(painter = painterResource(
                        id = R.drawable.nexwordlogo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(30.dp)
                            .width(120.dp), contentScale = ContentScale.Fit)

                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .clickable { showProfileEditScreen = true },
                        contentAlignment = Alignment.Center
                    ) {
                        val inicialesReales = if (nombreAlumno != "Cargando..." && nombreAlumno.contains(" ")) {
                            val partes = nombreAlumno.split(" ")
                            "${partes[0].take(1)}${partes[1].take(1)}".uppercase()
                        } else "UZ"
                        Text(inicialesReales, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // TARJETA DE BIENVENIDA
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).offset(y = (-40).dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3B5998)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = "Bienvenido, $primerNombre",
                        color = Color.White,
                        fontSize = 22.sp,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold)
                    Text("Continúa tu aprendizaje.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateToSearch,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Buscar Profesores", color = Color(0xFF3B5998), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ESTADÍSTICAS
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).offset(y = (-20).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(value = if (hasUpcomingClass) nextClassDate else "--",
                    label = "Próxima\nCapacitación",
                    icon = Icons.Default.CalendarMonth,
                    iconColor = Color(0xFF4A90E2),
                    modifier = Modifier.weight(1f))
                StatCard(creditos.toString(), "Creditos\nDisponibles",
                    Icons.Default.AccountBalanceWallet,
                    Color(0xFF50E3C2),
                    Modifier
                        .weight(1f)
                        .clickable { showRechargeSheet = true })
                StatCard(value = clasesTomadas.toString(),
                    label = "Capacitaciones\nTomadas",
                    icon = Icons.Default.Star,
                    iconColor = Color(0xFFB864F2),
                    modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Tu Próxima Capacitación",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp))
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
                    Row(modifier = Modifier
                        .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xFFE3E8FA),
                                RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center) {
                            Text(initialsClass.uppercase(),
                                color = PrimaryDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Inglés", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(nextClassTeacher,
                                fontSize = 14.sp, color = Color.Gray)
                            Text("$nextClassDate • $nextClassTime",
                                fontSize = 14.sp,
                                color = PrimaryDark,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryDark.copy(alpha = 0.05f)),
                    border = BorderStroke(1.dp,
                        PrimaryDark.copy(alpha = 0.1f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(32.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No tienes capacitaciones programadas",
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray)
                        Text("Explora los profesores y agenda tu primera capacitación.",
                            fontSize = 12.sp, color = Color.Gray,
                            textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PROFESORES RECOMENDADOS
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Profesores Recomendados", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Ver todos",
                    fontSize = 12.sp,
                    color = PrimaryDark,
                    modifier = Modifier.clickable { onNavigateToSearch() })
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                items(teachers.size) { index ->
                    val teacher = teachers[index]
                    val parts = teacher.fullName.split(" ")
                    val initials = if (parts.size > 1) "${parts[0].take(1)}${parts[1].take(1)}" else parts[0].take(2)
                    RecommendedTeacher(initials = initials.uppercase(),
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


        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.White,
            contentColor = PrimaryDark,
            scale = true
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = paddingValues.calculateBottomPadding() + 16.dp)
        )

    }

    if (showTeacherProfile) {
        TeacherDetailBottomSheet(
            teacher = selectedTeacher!!,
            studentId = studentProfile?.id ?: "",
            onDismiss = { showTeacherProfile = false },
            onNavigateToCalendar =
                { tId, tName, sId -> showTeacherProfile = false; onNavigateToCalendar(tId, tName, sId) })
    }

    if (showReservationDetails && nextClass != null) {
        ReservationDetailBottomSheet(reservation = nextClass, onDismiss = { showReservationDetails = false })
    }

    if (showProfileEditScreen) {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val api = RetrofitClient.getAuthenticatedClient(context).create(StudentApiService::class.java)
                val repo = StudentProfileRepository(api)
                return StudentUpdateProfileViewModel(repo) as T
            }
        }
        val updateViewModel: StudentUpdateProfileViewModel = viewModel(factory = factory)

        StudentUpdateProfileScreen(
            currentUser = studentProfile,
            viewModel = updateViewModel,
            onDismiss = { showProfileEditScreen = false },
            onProfileUpdated = {

                showProfileEditScreen = false
                StudentProfileViewModel.fetchMyProfile(context)
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


    if (showReviewModal && classToReview != null) {

        val teacherNameForReview = classToReview.teacherName
        val idForReview = classToReview.reservationId

        ReviewDialog(
            teacherName = teacherNameForReview,
            isLoading = false,
            onDismiss = { showReviewModal = false },
            onSubmit = { rating, comment ->
                StudentProfileViewModel.submitReview(
                    reservationId = idForReview,
                    rating = rating,
                    comment = comment,
                    onSuccess = {
                        showReviewModal = false
                        coroutineScope.launch {
                            StudentProfileViewModel.fetchMyHistory(context)
                        }
                    }
                )
            }
        )
    }
    var isClaiming by remember { mutableStateOf(false) }

    if (showRechargeSheet) {
        RechargeBottomSheet(
            onDismiss = { showRechargeSheet = false },
            isLoading = isClaiming,
            onSubmitId = { transId ->
                isClaiming = true
                StudentProfileViewModel.claimPayment(
                    paymentIdStr = transId,
                    onSuccess = { msg ->
                        isClaiming = false
                        showRechargeSheet = false
                        coroutineScope.launch {
                            StudentProfileViewModel.fetchMyProfile(context)
                            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
                        }
                    },
                    onError = { errorMsg ->
                        isClaiming = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(errorMsg, duration = SnackbarDuration.Long)
                        }
                    }
                )
            }
        )
    }
}

//Componente reutilizados
    @Composable
    fun StatCard(
        value: String,
        label: String,
        icon: ImageVector,
        iconColor: Color,
        modifier: Modifier = Modifier
    ) {
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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    textAlign = Center,
                    lineHeight = 11.sp
                )
            }
        }
    }

    @Composable
    fun RecommendedTeacher(initials: String, name: String, rating: String, onClick: () -> Unit) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(PrimaryDark.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = PrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB400),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = rating, fontSize = 10.sp, color = Color.Gray)
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeBottomSheet(
    onDismiss: () -> Unit,
    onSubmitId: (String) -> Unit,
    isLoading: Boolean
) {
    var transactionId by remember { mutableStateOf("") }
    val uriHandler = LocalUriHandler.current


    val mercadoPagoLink = "https://link.mercadopago.com.mx/v1nextw"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 40.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(60.dp).background(Color(0xFFEAF3DE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF3B6D11), modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Recargar Monedero", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Text("Compra créditos para agendar tus capacitaciones.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))

            // PASO 1: Pagar
            Button(
                onClick = { uriHandler.openUri(mercadoPagoLink) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009EE3)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("1. Ir a pagar a MercadoPago", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PASO 2: Reclamar
            Text("¿Ya pagaste?", fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = transactionId,
                onValueChange = { if (it.all { char -> char.isDigit() }) transactionId = it },
                label = { Text("Ingresa el No. de Operación") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onSubmitId(transactionId) },
                enabled = transactionId.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("2. Reclamar Saldo", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}