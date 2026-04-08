package com.utez.nextwordmobile.ui.screens.student

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.R
import com.utez.nextwordmobile.data.remote.dto.studentDto.messagingDto.InboxDto

import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark

import com.utez.nextwordmobile.viewModel.studentViewModel.InboxViewModel


@Composable
fun StudentMessageScreen(
    paddingValues: PaddingValues,
    onNavigateToChat: (String, String) -> Unit,
    viewModel: InboxViewModel
) {
    val inboxList by viewModel.inboxList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.fetchInbox()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        // 🌟 1. EL HEADER AZUL CON EL LOGO A LA IZQUIERDA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(NextWordGradient)
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart), // Logo centrado a la izquierda
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nexwordlogo),
                    contentDescription = "Logo",
                    modifier = Modifier.height(35.dp).width(130.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Mensajes",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 🌟 2. LA BANDEJA DE ENTRADA
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryDark)
            }
        } else if (inboxList.isEmpty()) {
            // Diseño de estado vacío si no hay mensajes
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Message, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Aún no tienes mensajes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text("Tus chats aparecerán aquí.", fontSize = 14.sp, color = Color.LightGray)
            }
        } else {
            // Lista de chats al estilo WhatsApp
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                items(inboxList.size) { index ->
                    val chat = inboxList[index]
                    ChatListItem(
                        chat = chat,
                        onClick = { onNavigateToChat(chat.contactId, chat.name) }
                    )
                    if (index < inboxList.size - 1) {
                        Divider(modifier = Modifier.padding(start = 88.dp, end = 24.dp), color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

// 🌟 COMPONENTE INDIVIDUAL DEL CHAT (La fila)
@Composable
fun ChatListItem(chat: InboxDto, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar (Por si no hay foto, ponemos sus iniciales)
        val initials = chat.name.split(" ").let { parts ->
            if (parts.size > 1) "${parts[0].take(1)}${parts[1].take(1)}" else parts[0].take(2)
        }

        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFE3E8FA)),
            contentAlignment = Alignment.Center
        ) {
            Text(initials.uppercase(), color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Textos del Chat
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Formateo simple de hora (Simulado si el string es muy largo)
                Text(
                    text = chat.dateLastMessage.take(10), // Tomamos solo la fecha por ahora para no complicar el formateo
                    fontSize = 12.sp,
                    color = if (chat.unreadCount > 0) Color(0xFF25D366) else Color.Gray // Verde si hay no leídos
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Las palomitas si yo lo envié
                if (chat.sentByMe) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Enviado",
                        tint = Color(0xFF34B7F1), // Azul WhatsApp
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Text(
                    text = chat.lastMessage,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Circulito de mensajes no leídos
                if (chat.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(Color(0xFF25D366), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chat.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}