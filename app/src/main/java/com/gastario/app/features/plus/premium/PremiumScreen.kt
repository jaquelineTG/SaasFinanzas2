package com.gastario.app.features.plus.premium

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth

// 🔹 Tu verde oscuro oficial
private val greenColor = Color(0xFF2E7D32)

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    navHostController: NavHostController,
    viewModel: PremiumViewModel = hiltViewModel(),
    userViewModel: com.gastario.app.features.user.UserViewModel = hiltViewModel()
) {
    // Escuchamos en tiempo real si el usuario ya es premium desde Firestore
    val yaEsPremium by userViewModel.isPremium.collectAsState()

    // Escuchamos si Google Play acaba de aprobar un pago nuevo
    val compraExitosa by viewModel.compraExitosa.collectAsState()

    // Obtenemos la Activity actual (la necesita Google Play para dibujar la ventana de cobro)
    val activity = LocalContext.current as Activity

    // Estados de control de la UI
    var planSeleccionado by remember { mutableStateOf("anual") }
    var mostrarModalExito by remember { mutableStateOf(false) }

    // Disparador automático cuando se confirma el pago real
    LaunchedEffect(compraExitosa) {
        if (compraExitosa) {
            mostrarModalExito = true
            viewModel.resetCompraExitosa()
        }
    }

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
                    title = { Text("SaaSFinanzas Premium", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navHostController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "volver")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFF3F4F6)
                    )
                )
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 🔹 HERO CARD
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = greenColor),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ACCESO ILIMITADO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                if (yaEsPremium) "¡Ya eres miembro Premium!" else "Toma el control absoluto de tu dinero",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "Herramientas de IA, reportes exportables y almacenamiento seguro en la nube para crecer tu riqueza.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // 🔹 LISTA DE BENEFICIOS
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text("Todo lo que incluye tu cuenta:", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)

                        FeatureCheckItem(icon = Icons.Default.AutoAwesome, title = "Análisis Financiero con IA", desc = "Consejos personalizados sobre tus gastos.")
                        FeatureCheckItem(icon = Icons.Default.AllInclusive, title = "Presupuestos y Metas sin límite", desc = "Crea y gestiona todos los proyectos que necesites.")
                        FeatureCheckItem(icon = Icons.Default.CloudUpload, title = "Respaldo de recibos en la nube", desc = "Adjunta fotos a tus movimientos y guárdalos seguros.")
                        FeatureCheckItem(icon = Icons.Default.PieChart, title = "Reportes Premium y Excel", desc = "Gráficos anuales y exportación en 1 clic.")
                    }
                }

                if (yaEsPremium) {
                    // 🔹 BOTÓN GESTIÓN DE SUSCRIPCIÓN (Para usuarios activos)
                    item {
                        ElevatedCard(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = greenColor, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Tu suscripción está activa", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "La facturación y renovación de tu plan se administra de forma segura a través de Google Play Store.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))

                                val contexto = LocalContext.current
                                OutlinedButton(
                                    onClick = {
                                        val intent = android.content.Intent(
                                            android.content.Intent.ACTION_VIEW,
                                            android.net.Uri.parse("https://play.google.com/store/account/subscriptions?package=${contexto.packageName}")
                                        )
                                        contexto.startActivity(intent)
                                    },
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color.Red),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                                ) {
                                    Text("Gestionar o Cancelar Suscripción", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // 🔹 SELECCIÓN DE PLANES
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PlanCard(
                                modifier = Modifier.weight(1f),
                                title = "Mensual",
                                price = viewModel.precioMensual,
                                period = "/ mes",
                                isSelected = planSeleccionado == "mensual",
                                onClick = { planSeleccionado = "mensual" }
                            )

                            PlanCard(
                                modifier = Modifier.weight(1f),
                                title = "Anual",
                                price = viewModel.precioAnual,
                                period = "/ año",
                                badge = "AHORRA 30%",
                                isSelected = planSeleccionado == "anual",
                                onClick = { planSeleccionado = "anual" }
                            )
                        }
                    }

                    // 🔹 BOTÓN DE PAGO REAL CON GOOGLE PLAY
                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid }

                        Button(
                            onClick = {
                                if (currentUserId != null) {
                                    // ⚠️ ESTOS SON LOS IDs EXACTOS QUE DEBES CREAR EN GOOGLE PLAY CONSOLE
                                    val idProductoPlay = if (planSeleccionado == "anual") "suscripcion_anual" else "suscripcion_mensual"

                                    viewModel.iniciarFlujoCompra(activity, idProductoPlay)
                                } else {
                                    Log.e("BILLING", "Error: No hay usuario autenticado.")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            enabled = currentUserId != null
                        ) {
                            Text(
                                text = if (planSeleccionado == "anual") "Empezar Plan Anual" else "Empezar Plan Mensual",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "Cancela cuando quieras desde Google Play. Al suscribirte aceptas nuestros términos y condiciones.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }

    // 🔹 DIÁLOGO DE COMPRA REALIZADA CON ÉXITO
    if (mostrarModalExito) {
        AlertDialog(
            onDismissRequest = { /* Obligamos a hacer clic en el botón */ },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarModalExito = false
                        navHostController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = greenColor)
                ) {
                    Text("¡Excelente!", color = Color.White)
                }
            },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = greenColor, modifier = Modifier.size(48.dp)) },
            title = { Text("¡Ya eres Premium!", fontWeight = FontWeight.Bold) },
            text = { Text("Gracias por tu compra. Tu suscripción se ha procesado correctamente y el acceso a todas las herramientas está activo.") },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun FeatureCheckItem(icon: ImageVector, title: String, desc: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(greenColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = greenColor, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(title, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp)
            Text(desc, color = Color.Gray, fontSize = 13.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
fun PlanCard(
    modifier: Modifier = Modifier,
    title: String,
    price: String,
    period: String,
    badge: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) greenColor else Color.LightGray
    val bgColor = if (isSelected) greenColor.copy(alpha = 0.05f) else Color.White

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .background(greenColor, RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(badge, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                Spacer(modifier = Modifier.height(30.dp))
            }

            Text(title, color = if (isSelected) greenColor else Color.Gray, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(price, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color.Black)
            Text(period, color = Color.Gray, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(if (isSelected) greenColor else Color.Transparent, CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent, CircleShape)
                        .padding(2.dp)) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray, CircleShape))
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                            .background(Color.White, CircleShape))
                    }
                }
            }
        }
    }
}