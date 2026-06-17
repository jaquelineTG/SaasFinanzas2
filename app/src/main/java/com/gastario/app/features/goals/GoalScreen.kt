package com.gastario.app.features.goals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.gastario.app.data.model.Meta
import com.gastario.app.features.user.UserViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


private val greenColor = Color(0xFF2E7D32)

// 🔥 Funciones auxiliares para formato profesional 🔥
private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(amount)
}

private fun formatDate(millis: Long): String {
    val format = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX"))
    return format.format(Date(millis))
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(navHostController: NavHostController, userViewModel: UserViewModel = hiltViewModel()) {
    val viewModel: GoalViewModel = hiltViewModel()
    val metas by viewModel.metas.collectAsState()

    // SIMULACIÓN DE ESTADO PREMIUM (Cambiar para probar)
    val isPremium by userViewModel.isPremium.collectAsState()

    // Escuchar cambios de navegación para recargar datos
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        viewModel.cargarMetas()
    }

    // Calcular límites de plan gratuito
    val calendar = Calendar.getInstance()
    val mesActual = calendar.get(Calendar.MONTH)
    val anioActual = calendar.get(Calendar.YEAR)

    val metasDelMes = metas.filter { meta ->
        val calMeta = Calendar.getInstance().apply { timeInMillis = meta.creadoEn }
        calMeta.get(Calendar.MONTH) == mesActual && calMeta.get(Calendar.YEAR) == anioActual
    }.size

    val limitReached = !isPremium && metasDelMes >= 2

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
                    title = { Text("Mis Metas de Ahorro", fontWeight = FontWeight.Bold, color = Color.Black) },
                    actions = {
                        IconButton(onClick = {
                            if (limitReached) {
                                navHostController.navigate("premium")
                            } else {
                                navHostController.navigate("añadir_metas")
                            }
                        }) {
                            Icon(
                                imageVector = if (limitReached) Icons.Filled.Lock else Icons.Filled.Add,
                                contentDescription = "Añadir meta",
                                tint = if (limitReached) Color.Gray else greenColor // Usando greenColor aquí
                            )
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
                Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 30.dp)
            ) {

                // BANNER PREMIUM (Diseño pulido)
                if (!isPremium) {
                    item {
                        val metasRestantes = (2 - metasDelMes).coerceAtLeast(0)
                        val cardColor = if (metasRestantes == 0) Color(0xFFFFEBEB) else greenColor.copy(alpha = 0.1f)
                        val textColor = if (metasRestantes == 0) Color(0xFFD32F2F) else greenColor

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth().clickable { navHostController.navigate("premium") },
                            colors = CardDefaults.elevatedCardColors(containerColor = cardColor),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Plan Básico Finanzas", fontWeight = FontWeight.Bold, color = textColor, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (metasRestantes == 0) {
                                        Text("Límite de metas alcanzado este mes. Actualiza a Premium.", fontSize = 13.sp, color = Color.DarkGray)
                                    } else {
                                        Text("Te quedan $metasRestantes metas por crear este mes.", fontSize = 13.sp, color = Color.DarkGray)
                                    }
                                }
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = textColor)
                            }
                        }
                    }
                }

                // LISTA DE METAS
                items(metas) { meta ->
                    ItemGoal(meta, navHostController)
                }

                if(metas.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(80.dp))
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Aún no tienes metas de ahorro.\n¡Visualiza tu futuro y crea una!",
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ItemGoal(meta: Meta, navHostController: NavHostController) {

    val progress = if (meta.montoObjetivo > 0) {
        (meta.montoAhorrado.toFloat() / meta.montoObjetivo.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val porcentaje = (progress * 100).toInt()

    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                navHostController.navigate("detail_goal/${meta.id}/${porcentaje}/${progress}")
            }
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // 🖼️ Mejoras en la imagen (Fondo, PlaceHolder y Crop)
                AsyncImage(
                    model = if (meta.imageUrl.isNotEmpty()) meta.imageUrl else android.R.drawable.ic_menu_gallery,
                    contentDescription = meta.nombre,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF3F4F6)),
                    contentScale = ContentScale.Crop, // 🔹 Importante para que no se deforme
                    alpha = if(meta.imageUrl.isNotEmpty()) 1f else 0.3f
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = meta.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Badge de fecha (Rediseñado)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EventNote, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        // 📅 Aquí es donde aplicamos el formato de fecha profesional
                        Text(
                            text = formatDate(meta.fechaLimite),
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECCIÓN FINANCIERA (Jerarquía mejorada)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Ahorrado", color = Color.Gray, fontSize = 12.sp)
                    // 💰 Formato de moneda profesional
                    Text(formatCurrency(meta.montoAhorrado), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = greenColor)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Objetivo", color = Color.Gray, fontSize = 12.sp)
                    // 💰 Formato de moneda profesional
                    Text(formatCurrency(meta.montoObjetivo), fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BARRA DE PROGRESO (Estilo moderno)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp) // Más gruesa
                        .clip(CircleShape), // Redondeada
                    color = greenColor,
                    trackColor = greenColor.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "$porcentaje%",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = greenColor
                )
            }
        }
    }
}