package com.example.saasfinanzas.features.budget


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class Presupuesto(
    val categoriaNombre: String,
    val montoLimite: Float,
    val gastoActual: Float
)

val presupuestos = listOf(
    Presupuesto("Comida", 500f, 340f),
    Presupuesto("Transporte", 300f, 135f),
    Presupuesto("Entretenimiento", 200f, 45f),
    Presupuesto("Compras", 100f, 82f)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(navController: NavController) {

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Presupuestos",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Categorías",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(presupuestos) { presupuesto ->
                    BudgetItem(presupuesto)
                }
            }
        }
    }
}


@Composable
fun BudgetItem(presupuesto: Presupuesto) {

    val progress = (presupuesto.gastoActual / presupuesto.montoLimite)
        .coerceIn(0f, 1f)

    ElevatedCard(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // 🔹 Círculo verde
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = Color(0xFFE6F9F0),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getIcon(presupuesto.categoriaNombre),
                            contentDescription = null,
                            tint = Color(0xFF22C55E)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = presupuesto.categoriaNombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "$${presupuesto.gastoActual.toInt()} / $${presupuesto.montoLimite.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF22C55E),
                trackColor = Color(0xFFE5E7EB)
            )
        }
    }
}


@Composable
fun getIcon(nombre: String) = when (nombre) {
    "Comida" -> Icons.Default.Fastfood
    "Transporte" -> Icons.Default.DirectionsCar
    "Entretenimiento" -> Icons.Default.Movie
    else -> Icons.Default.ShoppingBag
}



