package com.example.saasfinanzas.features.plus.configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navHostController: NavHostController) {

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    val viewModelConf:ConfigurationViewModel=hiltViewModel()
    val message by viewModelConf.menssage.collectAsState()
    LaunchedEffect(Unit) {


    }

    Scaffold(
        containerColor = Color(0xFFF3F4F6),

        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cambiar Contraseña") },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3F4F6),
                    scrolledContainerColor = Color(0xFFF3F4F6)
                )
            )
        }


    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7F6))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {



            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text(
                    text = "Seguridad de la cuenta",
                    fontSize = 26.sp
                )
            }

            item {
                Text(
                    text = "Protege tu patrimonio digital. Te recomendamos usar una contraseña única.",
                    color = Color.Gray
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                PasswordField(
                    label = "CONTRASEÑA ACTUAL",
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    isVisible = showCurrent,
                    onToggle = { showCurrent = !showCurrent },
                    icon = Icons.Default.Lock
                )
            }

            item {
                PasswordField(
                    label = "NUEVA CONTRASEÑA",
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    isVisible = showNew,
                    onToggle = { showNew = !showNew },
                    icon = Icons.Default.Shield,
                    placeholder = "Crea una clave segura"
                )
            }

//            item {
//                // Barra de seguridad
//                Row {
//                    repeat(4) { index ->
//                        Box(
//                            modifier = Modifier
//                                .weight(1f)
//                                .height(6.dp)
//                                .padding(end = 4.dp)
//                                .background(
//                                    if (index < 3) Color(0xFF2E7D32) else Color.LightGray,
//                                    RoundedCornerShape(50)
//                                )
//                        )
//                    }
//                }
//
//                Text(
//                    text = "Seguridad: Fuerte",
//                    color = Color(0xFF2E7D32),
//                    fontSize = 12.sp
//                )
//            }

            item {
                PasswordField(
                    label = "CONFIRMAR NUEVA CONTRASEÑA",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    isVisible = showConfirm,
                    onToggle = { showConfirm = !showConfirm },
                    icon = Icons.Default.CheckCircle,
                    placeholder = "Repite tu nueva clave"
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Button(
                    onClick = {

                            viewModelConf.CambiarContraseña(
                                currentPassword,
                                newPassword,
                                confirmPassword
                            )

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B8F3A)
                    )
                ) {
                    Text("Guardar Cambios", fontSize = 16.sp)
                }
                message?.let {
                    Text(
                        text = it,
                        color = if (it.contains("actualizada")) Color(0xFF1B8F3A) else Color.Red
                    )
                }
            }

            item {
                Text(
                    text = "Al cambiar tu contraseña, se cerrarán todas las sesiones activas.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
@Composable
fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggle: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String = ""
) {

    Column {
        Text(label, fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(icon, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = onToggle) {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                }
            },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        )
    }
}