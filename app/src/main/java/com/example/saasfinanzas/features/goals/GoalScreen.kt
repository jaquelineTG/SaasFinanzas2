package com.example.saasfinanzas.features.goals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.example.saasfinanzas.data.model.Meta
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(navHostController: NavHostController) {
    val viewModel: GoalViewModel = hiltViewModel()
    val metas by viewModel.metas.collectAsState()

    // SIMULACIÓN DE ESTADO PREMIUM
    val isPremium = false

    // Escuchar cambios de navegación (Igual que arreglamos en las otras pantallas)
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        viewModel.cargarMetas()
    }

    // Calcular cuántas metas han creado este mes
    val calendar = Calendar.getInstance()
    val mesActual = calendar.get(Calendar.MONTH)
    val anioActual = calendar.get(Calendar.YEAR)

    val metasDelMes = metas.filter { meta ->
        val calMeta = Calendar.getInstance().apply { timeInMillis = meta.creadoEn }
        calMeta.get(Calendar.MONTH) == mesActual && calMeta.get(Calendar.YEAR) == anioActual
    }.size

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Metas de Ahorro", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = {
                        if (!isPremium && metasDelMes >= 2) {
                            navHostController.navigate("premium")
                        } else {
                            navHostController.navigate("añadir_metas")
                        }
                    }) {
                        Icon(
                            imageVector = if (!isPremium && metasDelMes >= 2) Icons.Filled.Lock else Icons.Filled.Add,
                            contentDescription = "Añadir meta",
                            tint = if (!isPremium && metasDelMes >= 2) Color.Gray else Color.Black
                        )
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
            Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {

            // BANNER PREMIUM
            if (!isPremium) {
                item {
                    val metasRestantes = (2 - metasDelMes).coerceAtLeast(0)
                    val cardColor = if (metasRestantes == 0) Color(0xFFFFEBEB) else Color(0xFFEAF2EC)
                    val textColor = if (metasRestantes == 0) Color.Red else Color(0xFF1B3D2F)

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { navHostController.navigate("premium") },
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Plan Gratuito", fontWeight = FontWeight.Bold, color = textColor)
                                if (metasRestantes == 0) {
                                    Text("Límite de metas alcanzado. Toca para ser Premium.", fontSize = 13.sp, color = Color.DarkGray)
                                } else {
                                    Text("Te quedan $metasRestantes metas este mes.", fontSize = 13.sp, color = Color.DarkGray)
                                }
                            }
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
                    Text(
                        text = "Aún no tienes metas de ahorro. ¡Anímate a crear una!",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun ItemGoal(meta: Meta, navHostController: NavHostController) {

    val progress = (meta.montoAhorrado.toFloat() / meta.montoObjetivo.toFloat())
        .coerceIn(0f, 1f)

    val porcentaje = (progress * 100).toInt()

    ElevatedCard(

        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable{
                navHostController.navigate("detail_goal/${meta.id}/${porcentaje}/${progress}")

            }
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
//imagen como imagen por defecto
//                Image(
//                    painter = rememberAsyncImagePainter(
//                        model = if (meta.imageUrl.isNotEmpty()) meta.imageUrl else R.drawable.ic_image_placeholder
//                    ),
//                    contentDescription = meta.nombre,
//                    modifier = Modifier
//                        .size(70.dp)
//                        .clip(RoundedCornerShape(12.dp))
//                )

                Image(
                    painter = rememberAsyncImagePainter(meta.imageUrl),
                    contentDescription = meta.nombre,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = meta.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = "Objetivo: $${meta.montoObjetivo}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "Ahorrado: $${meta.montoAhorrado}",
                        fontSize = 14.sp,
                        color = Color(0xFF22C55E)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFD1FAE5),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = meta.fechaLimite.toString(),
                        color = Color(0xFF065F46),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = Color(0xFF22C55E),
                    trackColor = Color(0xFFE5E7EB),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "$porcentaje%",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}