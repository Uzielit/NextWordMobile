package com.utez.nextwordmobile.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.ui.theme.NextWordGradient
import com.utez.nextwordmobile.ui.theme.PrimaryDark

@Composable
fun StudentHomeScreen(

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(NextWordGradient)
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Next Word", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Panel de Estudiante", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("UZ", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = (-30).dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF3B5998))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Bienvenido, Uziel", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Continúa tu camino de aprendizaje", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {  },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Reservar Nueva Clase", color = Color(0xFF3B5998), fontWeight = FontWeight.Bold)
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = (-10).dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard("20-04-2026", "Próxima\nClase", Icons.Default.CalendarMonth, Color(0xFF4A90E2))
            StatCard("0", "Clases\nCompletadas", Icons.Default.MenuBook, Color(0xFF50E3C2))
            StatCard("24h", "Horas de\nEstudio", Icons.Default.AccessTime, Color(0xFFB864F2))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🌟 4. PRÓXIMAS CLASES
        Text(
            text = "Próximas Clases",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFFE3E8FA), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("LG", color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Inglés", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Profesor: Laura Gómez", fontSize = 12.sp, color = Color.Gray)
                    Text("2026-06-20 • 10:00 AM", fontSize = 12.sp, color = PrimaryDark, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        Text(
            text = "Profesores Recomendados",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RecommendedTeacher("JP", "Juan Pérez", "45 clases")
            RecommendedTeacher("AP", "Ana Paz", "30 clases")
            RecommendedTeacher("LM", "Luis Mora", "12 clases")
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}


@Composable
fun StatCard(value: String, label: String, icon: ImageVector, iconColor: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = label, fontSize = 10.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun RecommendedTeacher(initials: String, name: String, stats: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(PrimaryDark, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = stats, fontSize = 10.sp, color = Color.Gray)
    }
}