package com.utez.nextwordmobile.ui.components

import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.utez.nextwordmobile.ui.theme.PrimaryDark

@Composable
fun NextWordTeacherBottomBar(navController: NavController) {
    val items = listOf(
        TeacherBottomNavItem.Inicio,
        TeacherBottomNavItem.Slots,
        TeacherBottomNavItem.Clases,
        TeacherBottomNavItem.Mensajes
    )


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 20.dp)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            windowInsets = NavigationBarDefaults.windowInsets,
            modifier = Modifier.height(70.dp)
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(

                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.padding(top = 8.dp),
                            tint = if (isSelected) PrimaryDark else Color(0xFF999999)
                        )
                    },

                    label = {
                        Text(
                            text = item.title,
                            fontSize = 10.sp,
                            color = if (isSelected) PrimaryDark else Color(0xFF999999)
                        )
                    },
                    alwaysShowLabel = true,
                    selected = isSelected,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    ),
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
