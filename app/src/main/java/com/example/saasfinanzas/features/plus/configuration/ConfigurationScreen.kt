package com.example.saasfinanzas.features.plus.configuration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.saasfinanzas.features.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(navHostController: NavHostController) {

    var transactionAlerts by remember { mutableStateOf(true) }
    var budgetAlerts by remember { mutableStateOf(false) }
    val viewModel: AuthViewModel = hiltViewModel()

    Scaffold(
        containerColor = Color(0xFFF3F4F6),

        topBar = {
            CenterAlignedTopAppBar(
            title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = {navHostController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "volver")
                    }
                },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFF3F4F6),
                scrolledContainerColor = Color(0xFFF3F4F6)
            ))
        }




    ){ padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFEAF2EC)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {



            // PERFIL
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {

                            Image(
                                painter = painterResource(com.example.saasfinanzas.R.drawable.perfil1),
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1DB954)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Alexandria Sterling", style = MaterialTheme.typography.titleMedium)
                    Text("alex.sterling@curator.io", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                    ) {
                        Text("MIEMBRO PREMIUM", color = Color.White)
                    }
                }
            }

            // SECURITY
            item {
                SectionCard("Seguridad", Icons.Outlined.Security) {

                    RowItem(Icons.Outlined.Lock, "Cambiar Contraseña")

                }
            }

            // ALERTS
            item {
                SectionCard("Alerts", Icons.Outlined.Notifications) {

                    SwitchItem(
                        text = "Recordatorio de Metas",
                        checked = transactionAlerts,
                        onCheckedChange = { transactionAlerts = it }
                    )

                    SwitchItem(
                        text = "Alerta Limite de Presupuesto",
                        checked = budgetAlerts,
                        onCheckedChange = { budgetAlerts = it }
                    )
                }
            }


            // LOGOUT
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable {viewModel.logout()

                            navHostController.navigate("login") {
                                popUpTo(navHostController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            } }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF1F5F2))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleSmall)
        }

        Spacer(modifier = Modifier.height(12.dp))

        content()
    }
}

@Composable
fun RowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(it, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text, modifier = Modifier.weight(1f))
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
    }
}

@Composable
fun SwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(it, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = Color(0xFF1DB954)
            ))
    }
}

