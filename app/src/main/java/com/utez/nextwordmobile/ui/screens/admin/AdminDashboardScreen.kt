package com.utez.nextwordmobile.ui.screens.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.utez.nextwordmobile.ui.components.NextWordTeacherBottomBar
import com.utez.nextwordmobile.ui.components.TeacherBottomNavItem
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.utez.nextwordmobile.data.remote.RetrofitClient
import com.utez.nextwordmobile.data.remote.api.adminApi.AdminApiService
import com.utez.nextwordmobile.data.repository.admin.AdminRepository
import com.utez.nextwordmobile.ui.components.AdminBottomNavItem
import com.utez.nextwordmobile.ui.components.NextWordAdminBottomBar
import com.utez.nextwordmobile.viewModel.adminViewModel.AdminClassesViewModel
import com.utez.nextwordmobile.viewModel.adminViewModel.AdminHomeViewModel
import com.utez.nextwordmobile.viewModel.adminViewModel.AdminReportsViewModel
import com.utez.nextwordmobile.viewModel.adminViewModel.AdminUpdateProfileViewModel
import com.utez.nextwordmobile.viewModel.adminViewModel.AdminUsersViewModel

@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val context = LocalContext.current

    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val apiAdmin = RetrofitClient.getAuthenticatedClient(context).create(AdminApiService::class.java)
            val repoAdmin = AdminRepository(apiAdmin)

            if (modelClass.isAssignableFrom(AdminHomeViewModel::class.java)) {
                return AdminHomeViewModel(repoAdmin) as T
            }
            if (modelClass.isAssignableFrom(AdminUsersViewModel::class.java)) {
                return AdminUsersViewModel(repoAdmin) as T
            }
            if (modelClass.isAssignableFrom(AdminReportsViewModel::class.java)) {
                return AdminReportsViewModel(repoAdmin) as T
            }
            if (modelClass.isAssignableFrom(AdminClassesViewModel::class.java)) {
                return AdminClassesViewModel(repoAdmin) as T
            }
            if (modelClass.isAssignableFrom(AdminUpdateProfileViewModel::class.java)) {
                return AdminUpdateProfileViewModel(repoAdmin) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    Scaffold(
        bottomBar = { NextWordAdminBottomBar(navController = bottomNavController) },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        NavHost(
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            navController = bottomNavController,
            startDestination = AdminBottomNavItem.Inicio.route,
        ) {
            // 1. INICIO (DASHBOARD PRINCIPAL)
            composable(AdminBottomNavItem.Inicio.route) {
                val adminHomeViewModel: AdminHomeViewModel = viewModel(factory = factory)

                val updateProfileViewModel: AdminUpdateProfileViewModel = viewModel(factory = factory)

                AdminHomeScreen(
                    navController = bottomNavController,
                    viewModel = adminHomeViewModel,
                    updateProfileViewModel = updateProfileViewModel,
                    onLogout = onLogout
                )
            }

            // 2. USUARIOS (Directorio de Profes y Alumnos)
            composable(AdminBottomNavItem.Usuarios.route) {
                AdminUsersScreen(
                    viewModel = viewModel(factory = factory),
                    onNavigateToClasses = {
                        bottomNavController.navigate(AdminBottomNavItem.Clases.route) {
                            // popUpTo evita que se haga una pila infinita de pantallas
                            popUpTo(AdminBottomNavItem.Inicio.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // 3. CLASES (Historial y Reseñas)
            composable(AdminBottomNavItem.Clases.route) {
                val adminClassesViewModel: AdminClassesViewModel = viewModel(factory = factory)
                AdminClassesScreen(viewModel = adminClassesViewModel)
            }

            // 4. REPORTES (Gráficas)
            composable(AdminBottomNavItem.Reportes.route) {
                val adminReportsViewModel: AdminReportsViewModel = viewModel(factory = factory)
                AdminReportsScreen(viewModel = adminReportsViewModel)
            }
        }
    }
}