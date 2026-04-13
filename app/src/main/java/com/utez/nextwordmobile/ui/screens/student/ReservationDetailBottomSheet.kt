package com.utez.nextwordmobile.ui.screens.student

import android.text.TextUtils.split
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.data.remote.dto.studentDto.ReservationResponseDto
import com.utez.nextwordmobile.ui.theme.PrimaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailBottomSheet(
    reservation: ReservationResponseDto,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val uriHandler = LocalUriHandler.current // Para abrir enlaces web

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
            val parts = reservation.teacherName.split(" ")
            val initials = if (parts.size > 1) "${parts[0].take(1)}${parts[1].take(1)}" else parts[0].take(2)

            // Cabecera con el Profe
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(60.dp).background(Color(0xFFE3E8FA), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initials.uppercase(), color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Clase de ${reservation.topic ?: "Inglés"}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(reservation.teacherName, fontSize = 14.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Detalles de fecha y hora
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoTag(reservation.date, "Fecha", Modifier.weight(1f))
                InfoTag("${reservation.startTime} - ${reservation.endTime}", "Horario", Modifier.weight(1.5f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Estado de la Reserva", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(reservation.status, fontSize = 14.sp, color = PrimaryDark, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para unirse a Google Meet
            Button(
                onClick = {
                    val url = reservation.meetLink
                    if (!url.isNullOrBlank()) {
                        // Si falta el https:// se lo agregamos por seguridad
                        val finalUrl = if (!url.startsWith("http")) "https://$url" else url
                        uriHandler.openUri(finalUrl)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5998)),
                shape = RoundedCornerShape(12.dp),
                enabled = !reservation.meetLink.isNullOrBlank() // Solo se activa si hay link
            ) {
                Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (reservation.meetLink.isNullOrBlank()) "Enlace no disponible" else "Unirse a la Clase",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}