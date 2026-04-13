package com.utez.nextwordmobile.ui.screens.student

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utez.nextwordmobile.data.remote.RetrofitClient
import com.utez.nextwordmobile.data.remote.api.studentApi.ReservationApiService
import com.utez.nextwordmobile.data.remote.api.studentApi.MessagingApiService
import com.utez.nextwordmobile.data.remote.api.studentApi.StudentApiService
import com.utez.nextwordmobile.data.repository.MessagingRepository
import com.utez.nextwordmobile.data.repository.ReservationRepository
import com.utez.nextwordmobile.data.repository.StudentProfileRepository
import com.utez.nextwordmobile.ui.components.BottomNavItem
import com.utez.nextwordmobile.ui.components.NextWordStudentBottomBar
import com.utez.nextwordmobile.viewModel.studentViewModel.InboxViewModel
import com.utez.nextwordmobile.viewModel.studentViewModel.StudentClassesViewModel
import com.utez.nextwordmobile.viewModel.studentViewModel.StudentProfileViewModel

@Composable
fun StudentDashboardScreen(
    onNavigateToCalendar: (String, String, String) -> Unit,
    onNavigateToChat: (String, String, String) -> Unit,
    onLogout: () -> Unit) {

    val bottomNavController = rememberNavController()
    val context = LocalContext.current
    val profileFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val profileApi = RetrofitClient.getAuthenticatedClient(context).create(StudentApiService::class.java)
            val profileRepo = StudentProfileRepository(profileApi)

            val reservationApi = RetrofitClient.getAuthenticatedClient(context).create(ReservationApiService::class.java)
            val reservationRepo = ReservationRepository(reservationApi)

            return StudentProfileViewModel(profileRepo, reservationRepo) as T
        }
    }

    val profileViewModel: StudentProfileViewModel = viewModel(factory = profileFactory)

    val studentProfile by profileViewModel.studentProfile.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.fetchMyProfile(context)
    }

    MaterialTheme(colorScheme = lightColorScheme()) {
        Scaffold(
            bottomBar = { NextWordStudentBottomBar(navController = bottomNavController) },
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
                        onNavigateToCalendar = onNavigateToCalendar,
                        onLogout = onLogout,
                        StudentProfileViewModel = profileViewModel
                    )
                }

                // 2. Pestaña BUSCAR
                composable(BottomNavItem.Buscar.route) {
                    StudentSearchScreen(
                        paddingValues = paddingValues,
                        onNavigateToCalendar = onNavigateToCalendar,
                        viewModel = profileViewModel
                    )
                }

                // 3. Pestaña CLASES
                composable(BottomNavItem.Clases.route) {
                    val factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val api = RetrofitClient.getAuthenticatedClient(context).create(
                                ReservationApiService::class.java)
                            val repo = ReservationRepository(api)
                            return StudentClassesViewModel(repo) as T
                        }
                    }
                    val classesViewModel: StudentClassesViewModel = viewModel(factory = factory)

                    StudentClassesScreen(
                        paddingValues = paddingValues,
                        viewModel = classesViewModel
                    )
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
                    val inboxViewModel: InboxViewModel = viewModel(factory = factory)


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