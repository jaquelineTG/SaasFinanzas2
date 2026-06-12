package com.example.saasfinanzas.features.plus.configuration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import com.example.saasfinanzas.features.auth.AuthViewModel

// 🔹 Tu verde oscuro oficial
private val greenColor = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(navHostController: NavHostController) {
    val viewModelConf: ConfigurationViewModel = hiltViewModel()

    // 1. Escuchamos el estado de las alertas
    val transactionAlerts by viewModelConf.transactionAlerts.collectAsState()
    val budgetAlerts by viewModelConf.budgetAlerts.collectAsState()

    val viewModel: AuthViewModel = hiltViewModel()
    val currentUser by viewModelConf.currentUser.collectAsState()

    // 🌟 ESTADO PREMIUM (Cámbialo a 'false' para probar el bloqueo y el modal)
    val isPremium = false

    // Estado para controlar el diálogo de venta de notificaciones
    var showPremiumAlert by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModelConf.userData()
    }

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = greenColor,
            primaryContainer = greenColor.copy(alpha = 0.1f),
            onPrimaryContainer = greenColor
        )
    ) {
        Scaffold(
            containerColor = Color(0xFFF3F4F6), // Fondo base limpio
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Configuración", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navHostController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "volver")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFF3F4F6),
                        scrolledContainerColor = Color(0xFFF3F4F6)
                    )
                )
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // 🔹 PERFIL DE USUARIO
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(com.example.saasfinanzas.R.drawable.perfil1),
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = currentUser?.nombre ?: "Usuario",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentUser?.correo ?: "cargando...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Etiqueta Premium / Upsell
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isPremium) greenColor else Color.Gray)
                                .clickable { if (!isPremium) navHostController.navigate("premium") }
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isPremium) "MIEMBRO PREMIUM" else "MEJORAR A PREMIUM",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 🔹 SEGURIDAD
                item {
                    SectionCard("Seguridad", Icons.Outlined.Security) {
                        RowItem(
                            icon = Icons.Outlined.Lock,
                            text = "Cambiar Contraseña",
                            onClick = { navHostController.navigate("cambiarContraseña") }
                        )
                    }
                }

                // 🔹 ALERTAS Y NOTIFICACIONES (Bloqueadas para Free)
                item {
                    SectionCard("Notificaciones Inteligentes", Icons.Outlined.Notifications) {
                        SwitchItem(
                            text = "Recordatorio de Metas",
                            checked = transactionAlerts,
                            isPremiumAccess = isPremium,
                            onLockedClick = { showPremiumAlert = true },
                            onCheckedChange = { newValue ->
                                viewModelConf.saveAlertsConfig(transaction = newValue, budget = budgetAlerts)
                            }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(horizontal = 40.dp))

                        SwitchItem(
                            text = "Alerta Límite Presupuesto",
                            checked = budgetAlerts,
                            isPremiumAccess = isPremium,
                            onLockedClick = { showPremiumAlert = true },
                            onCheckedChange = { newValue ->
                                viewModelConf.saveAlertsConfig(transaction = transactionAlerts, budget = newValue)
                            }
                        )
                    }
                }

                // 🔹 CERRAR SESIÓN
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.logout()
                                navHostController.navigate("login") {
                                    popUpTo(navHostController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFD32F2F))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Cerrar Sesión", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }

            // 🔹 MODAL UPSELL DE NOTIFICACIONES PREMIUM
            if (showPremiumAlert) {
                AlertDialog(
                    onDismissRequest = { showPremiumAlert = false },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Notificaciones Pro", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)
                        }
                    },
                    text = {
                        Text(
                            "Recibe avisos automáticos en tu celular para recordarte tus metas de ahorro y evitar que te pases de tu presupuesto.\n\n¡Disponible solo en Premium! 💎",
                            color = Color.DarkGray,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showPremiumAlert = false
                                navHostController.navigate("premium")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Ver planes Premium", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPremiumAlert = false }) {
                            Text("Más tarde", color = Color.Gray, fontWeight = FontWeight.Medium)
                        }
                    }
                )
            }
        }
    }
}

// ==========================================
// COMPONENTES AUXILIARES
// ==========================================

@Composable
fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFE8F5E9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = greenColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
fun RowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, modifier = Modifier.weight(1f), fontSize = 15.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}

@Composable
fun SwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    text: String,
    checked: Boolean,
    isPremiumAccess: Boolean = true,
    onLockedClick: () -> Unit = {},
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Si no es premium, al tocar toda la fila lanzamos el modal de venta
                if (!isPremiumAccess) onLockedClick()
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(it, contentDescription = null, tint = if (isPremiumAccess) Color.Gray else Color.LightGray)
            Spacer(modifier = Modifier.width(16.dp))
        }

        // TEXTO + CANDADO
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                color = if (isPremiumAccess) Color.DarkGray else Color.Gray,
                fontWeight = FontWeight.Medium
            )
            if (!isPremiumAccess) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.Lock, contentDescription = "Premium", tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
            }
        }

        // SWITCH
        Switch(
            checked = if (isPremiumAccess) checked else false,
            onCheckedChange = {
                if (isPremiumAccess) onCheckedChange(it) else onLockedClick()
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = greenColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray,
                uncheckedBorderColor = Color.LightGray,
                disabledCheckedTrackColor = greenColor.copy(alpha = 0.5f),
                disabledUncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )
    }
}