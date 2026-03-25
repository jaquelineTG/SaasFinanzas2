package com.example.saasfinanzas.features.components



import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {

    data class BottomNavItem(
        val route: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val label: String
    )
    val items = listOf(
        BottomNavItem("home", Icons.Outlined.Home, "Inicio"),
        BottomNavItem("movimientos", Icons.Outlined.ReceiptLong, "Movimientos"),
        BottomNavItem("metas", Icons.Outlined.EmojiEvents, "Metas"),
        BottomNavItem("presupuestos", Icons.Outlined.AccountBalanceWallet, "Presupuestos"),
        BottomNavItem("mas", Icons.Outlined.MoreHoriz, "Más")
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(containerColor = Color(0xFFF9F9F9)) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = if (selected) Color(0xFF00C853) else Color(0xFF666666)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis, //  "Presupue..."
                        fontSize = 10.sp,
                        color = if (selected) Color(0xFF00C853) else Color(0xFF666666)
                    )
                }
            )
        }
    }
}


