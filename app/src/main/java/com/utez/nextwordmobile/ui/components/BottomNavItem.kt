package com.utez.nextwordmobile.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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