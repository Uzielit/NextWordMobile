package com.utez.nextwordmobile.ui.screens.teacher

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.utez.nextwordmobile.data.remote.api.studentApi.MessagingApiService
import com.utez.nextwordmobile.data.repository.teacher.TeacherProfileRepository
import com.utez.nextwordmobile.ui.components.NextWordTeacherBottomBar
import com.utez.nextwordmobile.ui.components.TeacherBottomNavItem
import com.utez.nextwordmobile.viewModel.teacherViewModel.TeacherHomeViewModel
import com.utez.nextwordmobile.data.remote.api.teacherApi.TeacherApiService
import com.utez.nextwordmobile.data.repository.MessagingRepository
import com.utez.nextwordmobile.viewModel.studentViewModel.InboxViewModel
import com.utez.nextwordmobile.viewModel.teacherViewModel.TeacherSlotsViewModel
import com.utez.nextwordmobile.viewModel.teacherViewModel.TeacherUpdateProfileViewModel
import kotlin.jvm.java

@Composable
fun TeacherDashboardScreen(
    onLogout: () -> Unit,
    onNavigateToChat: (String, String, String) -> Unit

) {
    val bottomNavController = rememberNavController()
    val context = LocalContext.current

    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val api = RetrofitClient.getAuthenticatedClient(context).create(TeacherApiService::class.java)
            val repo = TeacherProfileRepository(api)
            val apiMessaging = RetrofitClient.getAuthenticatedClient(context).create(
                MessagingApiService::class.java)
            val repoMessaging = MessagingRepository(apiMessaging)

            if (modelClass.isAssignableFrom(TeacherHomeViewModel::class.java)) {
                return TeacherHomeViewModel(repo) as T
            }
            if (modelClass.isAssignableFrom(TeacherSlotsViewModel::class.java)) {
                return TeacherSlotsViewModel(repo) as T
            }
            if (modelClass.isAssignableFrom(InboxViewModel::class.java)) {
                return InboxViewModel(repoMessaging) as T
            }
            if (modelClass.isAssignableFrom(TeacherUpdateProfileViewModel::class.java)) {
                return TeacherUpdateProfileViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val teacherHomeViewModel: TeacherHomeViewModel = viewModel(factory = factory)
    val inboxViewModel: InboxViewModel = viewModel(factory = factory)

    val teacherProfile by teacherHomeViewModel.teacherProfile.collectAsState()
    val currentTeacherId = teacherProfile?.id ?: ""

    val updateProfileViewModel: TeacherUpdateProfileViewModel = viewModel(factory = factory)

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
                TeacherHomeScreen(
                    paddingValues = paddingValues,
                    onNavigateToClasses = {
                        bottomNavController.navigate(TeacherBottomNavItem.Clases.route)
                    },
                    onNavigateToMessages = {
                        bottomNavController.navigate(TeacherBottomNavItem.Mensajes.route)
                    },
                    onNavigateToChat = { contactId, contactName ->
                        onNavigateToChat(contactId, contactName, currentTeacherId)
                    },
                    onLogout = onLogout,
                    viewModel = teacherHomeViewModel,
                    inboxViewModel = inboxViewModel,
                    updateProfileViewModel = updateProfileViewModel
                )
            }

            // 2. SLOTS
            composable(TeacherBottomNavItem.Slots.route) {

                val teacherSlotsViewModel: TeacherSlotsViewModel = viewModel(factory = factory)

                TeacherCreateSlotsScreen(
                    paddingValues = paddingValues,
                    teacherId = currentTeacherId,
                    viewModel = teacherSlotsViewModel
                )
            }

            // 3. CLASES
            composable(TeacherBottomNavItem.Clases.route) {
                TeacherClassesScreen(
                    paddingValues = paddingValues
                )
            }

            // 4. MENSAJES
            composable(TeacherBottomNavItem.Mensajes.route) {
                TeacherMessageScreen(
                    paddingValues = paddingValues,
                    viewModel = inboxViewModel,
                    onNavigateToChat = { contactId, contactName ->
                        onNavigateToChat(contactId, contactName, currentTeacherId)
                    }
                )
            }
        }
    }
}