package com.gastario.app.features.auth
import com.gastario.app.R
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// 🔹 Tu verde oscuro oficial (reemplaza al greenPrimary anterior)
private val greenColor = Color(0xFF2E7D32)

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Estados para manejar errores de validación
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val viewModel: AuthViewModel = hiltViewModel()
    val state by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state) {
        when (state) {
            is AuthState.Success -> {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
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
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.White // Aseguramos un fondo limpio
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Image(
                    painter = painterResource(R.drawable.welcome1),
                    contentDescription = "Logo de la App",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Bienvenido de nuevo",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B3D2F)
                    )
                )
                Text(
                    text = "Ingresa tus credenciales para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Campo de Email
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null // Limpia el error al escribir
                    },
                    label = { Text("Correo electrónico") },
                    placeholder = { Text("ejemplo@correo.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = greenColor) },
                    isError = emailError != null,
                    supportingText = {
                        if (emailError != null) {
                            Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    // 🔹 Exterminamos el morado del input
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = greenColor,
                        focusedLabelColor = greenColor,
                        cursorColor = greenColor,
                        focusedLeadingIconColor = greenColor
                    )
                )

                Spacer(modifier = Modifier.height(8.dp)) // Reducido por el supportingText

                // Campo de Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null // Limpia el error al escribir
                    },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = greenColor) },
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility")
                        }
                    },
                    isError = passwordError != null,
                    supportingText = {
                        if (passwordError != null) {
                            Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    // 🔹 Exterminamos el morado del input
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = greenColor,
                        focusedLabelColor = greenColor,
                        cursorColor = greenColor,
                        focusedLeadingIconColor = greenColor,
                        focusedTrailingIconColor = greenColor
                    )
                )

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clickable { /* Acción */ },
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = greenColor
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón Entrar
                Button(
                    onClick = {
                        // LÓGICA DE VALIDACIÓN LOGIN
                        var isValid = true

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
                        }

                        if (isValid) {
                            viewModel.login(email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = state !is AuthState.Loading,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    if (state is AuthState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Entrar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            val token = doGoogleSignIn(context)
                            if (token != null) {
                                viewModel.loginWithGoogle(token)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = state !is AuthState.Loading
                ) {
                    Text("Continuar con Google", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿Aún no tienes cuenta? ", color = Color.Gray)
                    Text(
                        text = "Regístrate",
                        color = greenColor,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.clickable { navController.navigate("register") }
                    )
                }
            }
        }
    }
}