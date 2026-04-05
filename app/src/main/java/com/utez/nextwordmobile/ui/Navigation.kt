package com.utez.nextwordmobile.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utez.nextwordmobile.ui.screens.AuthScreen
import com.utez.nextwordmobile.ui.screens.ForgotPasswordScreen
import com.utez.nextwordmobile.ui.screens.ResetPasswordScreen
import com.utez.nextwordmobile.ui.screens.VerificationMailScreen
import com.utez.nextwordmobile.ui.screens.student.StudentDashboardScreen


sealed class AppScreens(val route: String) {
    object Auth : AppScreens("auth")
    object Home : AppScreens("home")


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

}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Auth.route
    ) {

        // 1. PANTALLA DE LOGIN Y REGISTRO
        composable(AppScreens.Auth.route) {
            AuthScreen(
                onNavigateToHome = {
                    // 🌟 1. AL INICIAR SESIÓN, LO MANDAMOS AL DASHBOARD
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

        // 2. PANTALLA VERIFICACIÓN OTP
        composable(AppScreens.Verification.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            VerificationMailScreen(
                email = email,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerifySuccess = {
                    // 🌟 2. SI VERIFICA SU CUENTA CON ÉXITO, TAMBIÉN AL DASHBOARD
                    navController.navigate(AppScreens.StudentDashboard.route) {
                        popUpTo(AppScreens.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. PANTALLA DE PEDIR CORREO PARA RECUPERAR
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

        // 🌟 5. EL NUEVO CONTENEDOR DEL ESTUDIANTE
        composable(AppScreens.StudentDashboard.route) {

            StudentDashboardScreen()
        }
    }
}