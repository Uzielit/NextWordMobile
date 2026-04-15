package com.utez.nextwordmobile.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Inicio : BottomNavItem("student_home", Icons.Default.Home, "Inicio")
    object Buscar : BottomNavItem("student_search", Icons.Default.Search, "Buscar")
    object Clases : BottomNavItem("student_classes", Icons.Default.Person, "Clases")
    object Mensajes : BottomNavItem("student_messages", Icons.Default.Message, "Mensajes")
}

//Barra navegacion de Profesores
sealed class TeacherBottomNavItem(val route: String, val icon: ImageVector, val title: String) {

    object Inicio : TeacherBottomNavItem("teacher_home", Icons.Default.Home, "Inicio")

    object Slots : TeacherBottomNavItem("teacher_slots", Icons.Default.CalendarMonth, "Slots")


    object Clases : TeacherBottomNavItem("teacher_classes", Icons.Default.Person, "Clases")

    object Mensajes : TeacherBottomNavItem("teacher_messages", Icons.Default.Message, "Mensajes")

}

// Barra navegacion de Administrador
sealed class AdminBottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Inicio : AdminBottomNavItem("admin_home", Icons.Default.Home, "Inicio")
    object Usuarios : AdminBottomNavItem("admin_users", Icons.Default.People, "Usuarios")
    object Clases : AdminBottomNavItem("admin_classes", Icons.Default.EventNote, "Clases")
    object Reportes : AdminBottomNavItem("admin_reports", Icons.Default.BarChart, "Reportes")
}