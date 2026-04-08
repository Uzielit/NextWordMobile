package com.utez.nextwordmobile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utez.nextwordmobile.data.remote.RetrofitClient
import com.utez.nextwordmobile.data.remote.api.ReservationApiService
import com.utez.nextwordmobile.data.remote.api.studentApi.MessagingApiService
import com.utez.nextwordmobile.data.repository.MessagingRepository
import com.utez.nextwordmobile.data.repository.ReservationRepository
import com.utez.nextwordmobile.ui.screens.AuthScreen
import com.utez.nextwordmobile.ui.screens.ForgotPasswordScreen
import com.utez.nextwordmobile.ui.screens.ResetPasswordScreen
import com.utez.nextwordmobile.ui.screens.VerificationMailScreen
import com.utez.nextwordmobile.ui.screens.student.StudentDashboardScreen
import com.utez.nextwordmobile.ui.screens.student.StudentMessageScreen
import com.utez.nextwordmobile.ui.screens.student.TeacherCalendarScreen
import com.utez.nextwordmobile.viewModel.studentViewModel.InboxViewModel
import com.utez.nextwordmobile.viewModel.studentViewModel.StudenReservationViewModel


sealed class AppScreens(val route: String) {
    object Auth : AppScreens("auth")



    // ruta con parametro para correo
    object Verification : AppScreens("verification/{email}") {
        fun createRoute(email: String) = "verification/$email"
    }

    object ForgotPassword : AppScreens("forgot_password")

    object ResetPassword : AppScreens("reset_password/{email}") {
        fun createRoute(email: String) = "reset_password/$email"
    }

    //Rutas de estudiantes
    object StudentDashboard : AppScreens("student_dashboard")
    object TeacherCalendar : AppScreens("teacher_calendar/{teacherId}/{teacherName}/{studentId}") {
        fun createRoute(teacherId: String, teacherName: String, studentId: String): String {
            return "teacher_calendar/$teacherId/${android.net.Uri.encode(teacherName)}/$studentId"
        }
    }

    object Inbox : AppScreens("inbox")

    object ChatDetail : AppScreens("chat_detail/{contactId}/{contactName}") {
        fun createRoute(contactId: String, contactName: String): String {
            return "chat_detail/$contactId/${android.net.Uri.encode(contactName)}"
        }
    }

}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Auth.route
    ) {

        composable(AppScreens.Auth.route) {
            AuthScreen(
                onNavigateToHome = {
                    navController.navigate(AppScreens.StudentDashboard.route) {
                        popUpTo(AppScreens.Auth.route) { inclusive = true }
                    }
                },
                onNavigateToVerification = { emailRegistrado ->
                    navController.navigate(AppScreens.Verification.createRoute(emailRegistrado))
                },
                onNavigateToForgotPassword = {
                    navController.navigate(AppScreens.ForgotPassword.route)
                }
            )
        }

        composable(AppScreens.Verification.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            VerificationMailScreen(
                email = email,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerifySuccess = {

                    navController.navigate(AppScreens.StudentDashboard.route) {
                        popUpTo(AppScreens.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreens.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToReset = { email ->
                    navController.navigate(AppScreens.ResetPassword.createRoute(email))
                }
            )
        }

        // 4. PANTALLA DE CAMBIAR CONTRASEÑA
        composable(AppScreens.ResetPassword.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            ResetPasswordScreen(
                email = email,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResetSuccess = {
                    navController.navigate(AppScreens.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(AppScreens.StudentDashboard.route) {
            StudentDashboardScreen(
                onNavigateToCalendar = { teacherId, teacherName, studentId ->
                    navController.navigate(AppScreens.TeacherCalendar.createRoute(teacherId, teacherName, studentId))
                },
                        onNavigateToChat = { contactId, contactName ->
                    navController.navigate(AppScreens.ChatDetail.createRoute(contactId, contactName))
                }
            )
        }

        composable(
            route = AppScreens.TeacherCalendar.route,
            arguments = listOf(
                androidx.navigation.navArgument("teacherId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("teacherName") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("studentId") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            // 1. Extraemos los datos de la ruta
            val teacherId = backStackEntry.arguments?.getString("teacherId") ?: ""
            val teacherName = backStackEntry.arguments?.getString("teacherName") ?: ""
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""

            // 2. Creamos el ViewModel manualmente (Porque requiere el Repository)
            val context = LocalContext.current
            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val api = RetrofitClient.getAuthenticatedClient(context).create(
                        ReservationApiService::class.java)
                    val repo = ReservationRepository(api)
                    return StudenReservationViewModel(repo) as T
                }
            }
            val reservationViewModel: StudenReservationViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)

            // 3. ¡Invocamos a la pantalla sin errores!
            TeacherCalendarScreen(
                teacherId = teacherId,
                teacherName = teacherName,
                studentId = studentId,
                viewModel = reservationViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onReservationSuccess = {
                    navController.popBackStack() // Regresa al Home tras pagar con éxito
                }
            )
        }

        composable(
            route = AppScreens.ChatDetail.route,
            arguments = listOf(
                androidx.navigation.navArgument("contactId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("contactName") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId") ?: ""
            val contactName = backStackEntry.arguments?.getString("contactName") ?: ""

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chat con $contactName\nID: $contactId\n(Pantalla de Chat en construcción 🚧)")
            }
        }





    }


}