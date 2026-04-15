package com.utez.nextwordmobile.ui.screens.admin

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
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
import com.utez.nextwordmobile.data.remote.SessionManager
import com.utez.nextwordmobile.data.remote.dto.adminDto.MonthlyIncomeResponse
import com.utez.nextwordmobile.data.remote.dto.adminDto.TransactionResponse
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark
import com.utez.nextwordmobile.viewModel.adminViewModel.AdminReportsViewModel

@Composable
fun AdminReportsScreen(viewModel: AdminReportsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        viewModel.fetchReports(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // 1. HEADER AZUL CON LOGO Y BOTÓN PREMIUM
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NextWordGradient)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Títulos a la Izquierda
                Column(modifier = Modifier.weight(1f)) {
                    Image(
                        painter = painterResource(id = R.drawable.nexwordlogo),
                        contentDescription = "Logo",
                        modifier = Modifier.height(24.dp).width(100.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Reportes Financieros",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Botón de Exportar
                Button(
                    onClick = {
                        val token = SessionManager(context).fetchAuthToken() ?: ""
                        val url = "http://192.168.100.8:8080/api/admin/reports/export/pdf"

                        val request = DownloadManager.Request(Uri.parse(url))
                            .setTitle("Reporte NextWord")
                            .setDescription("Descargando reporte financiero...")
                            .addRequestHeader("Authorization", "Bearer $token")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS,
                                "Reporte_NextWord_${System.currentTimeMillis()}.pdf"
                            )
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true)

                        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        downloadManager.enqueue(request)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = PrimaryDark
                    ),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "Exportar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PDF",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryDark)
            }
        } else if (uiState.reportData != null) {

            // 2. GRÁFICA DE BARRAS NATIVA
            Text(text = "Ingresos de los últimos 6 meses", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827), modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(250.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomCenter) {
                    CustomBarChart(data = uiState.reportData!!.chartData)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. DETALLE DE TRANSACCIONES (Tabla)
            Text(text = "Últimas Transacciones", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827), modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.reportData!!.recentTransactions.isEmpty()) {
                Text(text = "No hay transacciones recientes.", color = Color.Gray, modifier = Modifier.padding(horizontal = 24.dp))
            } else {
                uiState.reportData!!.recentTransactions.forEach { tx ->
                    TransactionCard(tx)
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

// === COMPONENTES GRÁFICOS ===

@Composable
fun CustomBarChart(data: List<MonthlyIncomeResponse>) {
    val maxAmount = data.maxOfOrNull { it.amount }?.toFloat()?.takeIf { it > 0 } ?: 1f

    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { startAnimation = true }

    val chronologicalData = data.reversed()

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        chronologicalData.forEach { monthData ->
            val amountFloat = monthData.amount.toFloat()

            val maxBarHeight = 0.70f
            val targetFraction = if (amountFloat == 0f) 0.02f else (amountFloat / maxAmount) * maxBarHeight

            val barHeightFraction by animateFloatAsState(
                targetValue = if (startAnimation) targetFraction else 0f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                label = "BarHeight"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                if (startAnimation && amountFloat > 0) {
                    val label = if (amountFloat >= 1000) "$${String.format("%.1f", amountFloat / 1000)}k" else "$${amountFloat.toInt()}"
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        color = PrimaryDark,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // La barra coloreada
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .fillMaxHeight(barHeightFraction)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(NextWordGradient)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = monthData.month,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun TransactionCard(tx: TransactionResponse) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFEAF3DE)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF3B6D11))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Pago por ${tx.topic}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF111827), maxLines = 1)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "${tx.studentName} • ${tx.date}", fontSize = 12.sp, color = Color.Gray, maxLines = 1)
            }
            Text(text = "+$${tx.amount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF3B6D11))
        }
    }
}