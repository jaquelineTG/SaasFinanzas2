package com.gastario.app.features.goals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gastario.app.data.model.Aporte

// 🔹 Tu verde oscuro oficial
private val greenColor = Color(0xFF2E7D32)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailGoal(navHostController: NavHostController, metaId: String?, porcentaje: String?, progress: String?) {

    val viewModel: GoalViewModel = hiltViewModel()
    val viewModelAportes: AporteViewModel = hiltViewModel()
    val aportes by viewModelAportes.aportes.collectAsState()
    val metas by viewModel.metas.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarMetas()
        viewModelAportes.cargarAportes()
    }
    val meta = metas.find { it.id == metaId }
    val progreso = 0.75f

    // 🔹 Envolvemos todo en el MaterialTheme para aplicar tu diseño
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = greenColor,
            primaryContainer = greenColor.copy(alpha = 0.1f),
            onPrimaryContainer = greenColor
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F4F6)) // 🔹 Fondo limpio directo en la LazyColumn
                .padding(horizontal = 24.dp), // 🔹 Margen lateral estándar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item { Spacer(modifier = Modifier.height(20.dp)) }

            /* 🔹 HEADER UNIFICADO (Igual que en AddAporte y AddGoal) */
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { navHostController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                    Text(
                        text = "Detalle de Meta",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            /* 🔹 INFORMACIÓN DE LA META */
            item {
                Text(
                    text = meta?.nombre ?: "Sin nombre",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Meta: $${meta?.montoObjetivo ?: "0.0"}",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }

            /* 🔹 CÍRCULO DE PROGRESO */
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(220.dp)
                ) {
                    CircularProgressIndicator(
                        progress = progreso,
                        strokeWidth = 20.dp,
                        color = greenColor,
                        trackColor = greenColor.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxSize()
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$porcentaje%",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = greenColor
                        )

                        Text(
                            text = "$$progress",
                            color = Color.Gray
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }

            /* 🔹 TÍTULO HISTORIAL */
            item {
                Text(
                    text = "Historial de Aportes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            /* 🔹 LISTA DE APORTES */
            itemsIndexed(aportes) { index, aporte ->
                AporteItem(aporte, index + 1)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            /* 🔹 BOTÓN AGREGAR APORTE */
            item {
                Button(
                    onClick = { navHostController.navigate("añadir_aporte/${metaId}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = greenColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Agregar aporte",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

@Composable
fun AporteItem(
    aporte: Aporte,
    contador: Int
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Aporte $contador",
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = aporte.fecha.toString(),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "+$${aporte.monto}",
                color = greenColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }
    }
}