package com.utez.nextwordmobile.ui.screens.teacher

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utez.nextwordmobile.data.remote.RetrofitClient
import com.utez.nextwordmobile.data.repository.teacher.TeacherProfileRepository
import com.utez.nextwordmobile.ui.components.NextWordTeacherBottomBar
import com.utez.nextwordmobile.ui.components.TeacherBottomNavItem
import com.utez.nextwordmobile.viewModel.teacherViewModel.TeacherHomeViewModel
import com.utez.nextwordmobile.data.remote.api.teacherApi.TeacherApiService
import kotlin.jvm.java

@Composable
fun TeacherDashboardScreen(
    onLogout: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val context = LocalContext.current


    Scaffold(
        bottomBar = { NextWordTeacherBottomBar(navController = bottomNavController) },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        NavHost(
            navController = bottomNavController,
            startDestination = TeacherBottomNavItem.Inicio.route,
        ) {
            // 1. INICIO
            composable(TeacherBottomNavItem.Inicio.route) {
                val factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        val api = RetrofitClient.getAuthenticatedClient(context)
                            .create(TeacherApiService::class.java)
                        val repo = TeacherProfileRepository(api)
                        return TeacherHomeViewModel(repo) as T
                    }
                }
                val teacherViewModel: TeacherHomeViewModel = viewModel(factory = factory)
                TeacherHomeScreen(
                    paddingValues = paddingValues,
                    onNavigateToClasses = {
                        bottomNavController.navigate(TeacherBottomNavItem.Clases.route)
                    },
                    onNavigateToMessages = {
                        bottomNavController.navigate(TeacherBottomNavItem.Mensajes.route)
                    },
                    viewModel = teacherViewModel

                )
            }

            // 2. SLOTS
            composable(TeacherBottomNavItem.Slots.route) {
                // TeacherCreateSlotsScreen()
            }

            // 3. CLASES
            composable(TeacherBottomNavItem.Clases.route) {
                //TeacherClassesScreen()
            }

            // 4. MENSAJES
            composable(TeacherBottomNavItem.Mensajes.route) {
                //TeacherMessagesScreen()
            }
        }
    }
}