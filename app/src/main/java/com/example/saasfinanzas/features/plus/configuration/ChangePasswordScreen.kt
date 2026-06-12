package com.example.saasfinanzas.features.plus.configuration

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

// 🔹 Tu verde oscuro oficial
private val greenColor = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navHostController: NavHostController) {

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    val viewModelConf: ConfigurationViewModel = hiltViewModel()
    val message by viewModelConf.menssage.collectAsState()

    LaunchedEffect(Unit) {

    }

    // 🔹 Envolvemos todo en el MaterialTheme para matar el morado base del sistema
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = greenColor,
            primaryContainer = greenColor.copy(alpha = 0.1f),
            onPrimaryContainer = greenColor
        )
    ) {
        Scaffold(
            containerColor = Color(0xFFF3F4F6),

            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Cambiar Contraseña", fontWeight = FontWeight.Bold) },
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
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // 🔹 ENCABEZADO
                item {
                    Column {
                        Text(
                            text = "Seguridad de la cuenta",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Protege tu patrimonio digital. Te recomendamos usar una contraseña única.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                // 🔹 FORMULARIO (Agrupado en una tarjeta premium)
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            PasswordField(
                                label = "CONTRASEÑA ACTUAL",
                                value = currentPassword,
                                onValueChange = { currentPassword = it },
                                isVisible = showCurrent,
                                onToggle = { showCurrent = !showCurrent },
                                icon = Icons.Default.Lock
                            )

                            HorizontalDivider(color = Color(0xFFF3F4F6))

                            PasswordField(
                                label = "NUEVA CONTRASEÑA",
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                isVisible = showNew,
                                onToggle = { showNew = !showNew },
                                icon = Icons.Default.Shield,
                                placeholder = "Crea una clave segura"
                            )

                            HorizontalDivider(color = Color(0xFFF3F4F6))

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
                    }
                }

                // 🔹 BOTÓN Y MENSAJES
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // 🔹 Mejora visual del mensaje de éxito/error
                        message?.let { msg ->
                            val isSuccess = msg.contains("actualizada")
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSuccess) Color(0xFFE8F5E9) else Color(0xFFFFEBEB),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = msg,
                                    color = if (isSuccess) greenColor else Color.Red,
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Al cambiar tu contraseña, se cerrarán todas las sesiones activas.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
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
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(icon, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),

            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = greenColor,
                focusedLabelColor = greenColor,
                cursorColor = greenColor,
                focusedLeadingIconColor = greenColor,
                focusedTrailingIconColor = greenColor,
                unfocusedBorderColor = Color(0xFFE5E7EB) // Borde gris suave sin foco
            )
        )
    }
}