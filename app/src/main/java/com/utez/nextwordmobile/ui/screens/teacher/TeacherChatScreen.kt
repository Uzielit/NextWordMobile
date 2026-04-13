package com.utez.nextwordmobile.ui.screens.teacher

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.R
import com.utez.nextwordmobile.data.remote.dto.studentDto.messagingDto.MessageDto
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.studentViewModel.ChatDetailViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherChatScreen(
    contactName: String,
    myId: String,
    viewModel: ChatDetailViewModel,
    onNavigateBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val messages by viewModel.messages.collectAsState()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val activity = context as? Activity
        val originalMode = activity?.window?.attributes?.softInputMode ?: WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        onDispose { activity?.window?.setSoftInputMode(originalMode) }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val initials = contactName.split(" ").let { parts ->
                            if (parts.size > 1) "${parts[0].take(1)}${parts[1].take(1)}" else parts[0].take(2)
                        }
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE3E8FA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials.uppercase(), color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(contactName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryDark)
            )
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe un mensaje...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryDark,
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5)),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(messageText)
                                messageText = ""
                            }
                        },
                        containerColor = PrimaryDark,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(50.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar")
                    }
                }
            }
        }
    ) { paddingValues ->
        // 🌟 CONTENEDOR PRINCIPAL DEL CHAT CON LOGO DE FONDO
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA)) // Fondo claro limpio
                .padding(paddingValues)
        ) {
            // MARCA DE AGUA (Logo NextWord)
            Image(
                painter = painterResource(id = R.drawable.nexwordlogo), // Tu logo
                contentDescription = "Marca de agua",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.7f) // Ocupa el 70% del ancho de la pantalla
                    .alpha(0.08f), // Muy transparente para que no estorbe la lectura
                contentScale = ContentScale.Fit
            )

            // LISTA DE MENSAJES (Va por encima del logo)
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(messages.size) { index ->
                    val message = messages[index]
                    TeacherMessageBubble(message = message, isMine = message.senderId == myId)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun TeacherMessageBubble(message: MessageDto, isMine: Boolean) {
    val bubbleColor = if (isMine) Color(0xFFDCF8C6) else Color.White
    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isMine) RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp) else RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(color = bubbleColor, shape = shape, shadowElevation = 1.dp, modifier = Modifier.widthIn(max = 300.dp)) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(text = message.body, fontSize = 16.sp, color = Color.Black)
                Row(modifier = Modifier.align(Alignment.End).padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    val timeString = formatChatTimeTeacher(message.sentAt)
                    Text(text = timeString, fontSize = 10.sp, color = Color.Gray)

                    if (isMine) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = if (message.read == "1") Color(0xFF34B7F1) else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

fun formatChatTimeTeacher(isoString: String): String {
    return try {
        val zdt = ZonedDateTime.parse(isoString)
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale("es", "MX"))
        zdt.format(formatter).uppercase()
    } catch (e: Exception) {
        ""
    }
}