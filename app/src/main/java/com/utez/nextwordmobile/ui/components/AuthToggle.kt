package com.utez.nextwordmobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utez.nextwordmobile.ui.theme.BackgroundGray
import com.utez.nextwordmobile.ui.theme.PrimaryDark

@Composable
fun AuthToggle(
    isLoginSelected: Boolean,
    onOptionSelected: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundGray)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        ToggleItem(
            text = "Iniciar Sesión",
            isSelected = isLoginSelected,
            modifier = Modifier.weight(1f),
            onClick = { onOptionSelected(true) }
        )

        ToggleItem(
            text = "Crear Cuenta",
            isSelected = !isLoginSelected,
            modifier = Modifier.weight(1f),
            onClick = { onOptionSelected(false) }
        )
    }
}

@Composable
private fun ToggleItem(
    text: String,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))

            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) PrimaryDark else Color.Gray,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}