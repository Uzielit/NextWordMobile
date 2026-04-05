package com.utez.nextwordmobile.ui.screens.student

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utez.nextwordmobile.ui.components.BottomNavItem
import com.utez.nextwordmobile.ui.components.NextWordBottomBar

@Composable
fun StudentDashboardScreen() {

    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { NextWordBottomBar(navController = bottomNavController) },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->


        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Inicio.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            // 1. Pestaña INICIO
            composable(BottomNavItem.Inicio.route) {
                StudentHomeScreen()
            }

            // 2. Pestaña BUSCAR
            composable(BottomNavItem.Buscar.route) {
                StudentSearchScreen()
            }

            // 3. Pestaña CLASES
            composable(BottomNavItem.Clases.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Mis Clases - Próximamente")
                }
            }

            // 4. Pestaña MENSAJES
            composable(BottomNavItem.Mensajes.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Mensajes - Próximamente")
                }
            }
        }
    }
}