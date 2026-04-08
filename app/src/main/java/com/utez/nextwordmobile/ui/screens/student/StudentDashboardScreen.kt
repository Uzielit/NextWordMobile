package com.utez.nextwordmobile.ui.screens.student

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utez.nextwordmobile.data.remote.RetrofitClient
import com.utez.nextwordmobile.data.remote.api.studentApi.MessagingApiService
import com.utez.nextwordmobile.data.repository.MessagingRepository
import com.utez.nextwordmobile.ui.components.BottomNavItem
import com.utez.nextwordmobile.ui.components.NextWordBottomBar
import com.utez.nextwordmobile.viewModel.studentViewModel.InboxViewModel
import com.utez.nextwordmobile.viewModel.studentViewModel.StudentProfileViewModel

@Composable
fun StudentDashboardScreen(
    onNavigateToCalendar: (String, String, String) -> Unit,
    onNavigateToChat: (String, String, String) -> Unit) {

    val bottomNavController = rememberNavController()
    val context = LocalContext.current

    val profileViewModel: StudentProfileViewModel = viewModel()
    val studentProfile by profileViewModel.studentProfile.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.fetchMyProfile(context)
    }

    MaterialTheme(colorScheme = lightColorScheme()) {
        Scaffold(
            bottomBar = { NextWordBottomBar(navController = bottomNavController) },
            containerColor = Color(0xFFF5F5F5)
        ) { paddingValues ->


            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Inicio.route,
                modifier = Modifier
            ) {

                // 1. Pestaña INICIO
                composable(BottomNavItem.Inicio.route) {

                    StudentHomeScreen(
                        paddingValues = paddingValues,
                        onNavigateToSearch = {
                            bottomNavController.navigate(BottomNavItem.Buscar.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }

                        },
                        onNavigateToCalendar = onNavigateToCalendar
                    )
                }

                // 2. Pestaña BUSCAR
                composable(BottomNavItem.Buscar.route) {
                    StudentSearchScreen(
                        paddingValues = paddingValues,
                        onNavigateToCalendar = onNavigateToCalendar
                    )
                }

                // 3. Pestaña CLASES
                composable(BottomNavItem.Clases.route) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Mis Clases - Próximamente")
                    }
                }

                // 4. Pestaña MENSAJES

                composable(BottomNavItem.Mensajes.route) {
                    val factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val api = RetrofitClient.getAuthenticatedClient(context).create(MessagingApiService::class.java)
                            val repo = MessagingRepository(api)
                            return InboxViewModel(repo) as T
                        }
                    }
                    val inboxViewModel: InboxViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)


                    StudentMessageScreen(
                        paddingValues = paddingValues,
                        viewModel = inboxViewModel,
                        onNavigateToChat = { contactId, contactName ->
                            // Atrapamos el ID real y lo pasamos a la navegación
                            val myIdReal = studentProfile?.id ?: ""
                            onNavigateToChat(contactId, contactName, myIdReal)
                        }
                    )
                }

            }
        }
    }
}