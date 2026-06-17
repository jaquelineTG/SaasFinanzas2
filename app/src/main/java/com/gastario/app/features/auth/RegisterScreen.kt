package com.gastario.app.features.auth

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

// 🔹 Tu verde oscuro oficial
private val greenColor = Color(0xFF2E7D32)

@Composable
fun RegisterScreen(navHostController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var acceptedTerms by remember { mutableStateOf(false) }

    // Estados para manejar errores de validación
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val viewModel: AuthViewModel = hiltViewModel()
    val state by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state) {
        when (state) {
            is AuthState.Success -> {
                navHostController.navigate("home") {
                    popUpTo("register") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar((state as AuthState.Error).message)
            }
            else -> Unit
        }
    }

    // 🔹 Envolvemos todo en el MaterialTheme para que los efectos de toque y foco sean verdes
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = greenColor,
            primaryContainer = greenColor.copy(alpha = 0.1f),
            onPrimaryContainer = greenColor
        )
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.White // Aseguramos un fondo limpio
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(horizontal = 28.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Crea tu cuenta",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B3D2F)
                    )
                )
                Text(
                    text = "Empieza a controlar tu dinero hoy",
                    color = greenColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(32.dp))

                // FORMULARIO - NOMBRE
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Nombre completo") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = greenColor) },
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = greenColor,
                        focusedLabelColor = greenColor,
                        cursorColor = greenColor,
                        focusedLeadingIconColor = greenColor
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // FORMULARIO - CORREO
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = greenColor) },
                    isError = emailError != null,
                    supportingText = { emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = greenColor,
                        focusedLabelColor = greenColor,
                        cursorColor = greenColor,
                        focusedLeadingIconColor = greenColor
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // FORMULARIO - CONTRASEÑA
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                        confirmPasswordError = null
                    },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = greenColor) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = if (passwordVisible) greenColor else Color.Gray)
                        }
                    },
                    isError = passwordError != null,
                    supportingText = { passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = greenColor,
                        focusedLabelColor = greenColor,
                        cursorColor = greenColor,
                        focusedLeadingIconColor = greenColor,
                        focusedTrailingIconColor = greenColor
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // FORMULARIO - CONFIRMAR CONTRASEÑA
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = null
                    },
                    label = { Text("Confirmar contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = greenColor) },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = confirmPasswordError != null,
                    supportingText = { confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = greenColor,
                        focusedLabelColor = greenColor,
                        cursorColor = greenColor,
                        focusedLeadingIconColor = greenColor
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // BOTÓN REGISTRO
                Button(
                    onClick = {
                        // LÓGICA DE VALIDACIÓN REGISTRO
                        var isValid = true

                        if (name.isBlank()) {
                            nameError = "El nombre es requerido"
                            isValid = false
                        }

                        if (email.isBlank()) {
                            emailError = "El correo es requerido"
                            isValid = false
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailError = "Ingresa un correo válido"
                            isValid = false
                        }

                        if (password.isBlank()) {
                            passwordError = "La contraseña es requerida"
                            isValid = false
                        } else if (password.length < 6) {
                            passwordError = "La contraseña debe tener al menos 6 caracteres"
                            isValid = false
                        }

                        if (confirmPassword != password) {
                            confirmPasswordError = "Las contraseñas no coinciden"
                            isValid = false
                        }

                        // 🔹 Llamada al ViewModel
                        if (isValid) {
                            viewModel.register(email, password, name)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                    enabled = state !is AuthState.Loading
                ) {
                    if (state is AuthState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Registrarse", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // DIVISOR SOCIAL
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                    Text(
                        text = " O regístrate con ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val token = doGoogleSignIn(context)
                                if (token != null) {
                                    viewModel.loginWithGoogle(token)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = state !is AuthState.Loading
                    ) {
                        Text("Google", color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.padding(bottom = 20.dp)) {
                    Text("¿Ya tienes una cuenta? ", color = Color.Gray)
                    Text(
                        text = "Inicia sesión",
                        color = greenColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navHostController.navigate("login") }
                    )
                }
            }
        }
    }
}