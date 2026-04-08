package com.example.saasfinanzas.features.plus.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navHostController: NavHostController) {

    var selectedTab by remember { mutableStateOf("Semanal") }

    // 🔥 SIMULACIÓN (luego lo conectas con tu usuario real)
    val isPremium = false

    // 🔒 Categorías FREE
    val categoriasFree = listOf(
        Triple("Comida", "$840.00", 0.8f),
        Triple("Transporte", "$320.00", 0.3f),
        Triple("Salud", "$200.00", 0.2f),
        Triple("Entretenimiento", "$450.00", 0.5f)
    )

    // 💎 Categorías PREMIUM
    val categoriasPremium = listOf(
        Triple("Comida", "$840.00", 0.8f),
        Triple("Transporte", "$320.00", 0.3f),
        Triple("Salud", "$200.00", 0.2f),
        Triple("Entretenimiento", "$450.00", 0.5f),
        Triple("Servicios", "$1,630.50", 0.9f),
        Triple("Educación", "$500.00", 0.4f)
    )

    val categorias = if (isPremium) categoriasPremium else categoriasFree

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reportes") },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "volver")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEAF2EC))
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // TABS
            item {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFDDE6DD))
                        .padding(4.dp)
                ) {
                    listOf("Semanal", "Mensual", "Anual").forEach { tab ->
                        val selected = tab == selectedTab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (selected) Color.White else Color.Transparent)
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(tab)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }

            // TOTAL GASTADO
            item {
                Card(shape = RoundedCornerShape(20.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Total gastado")
                        Text("$3,240.50", style = MaterialTheme.typography.headlineMedium)

                        Spacer(modifier = Modifier.height(8.dp))

                        // 🔒 COMPARACIÓN BLOQUEADA
                        if (isPremium) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFFD1F2E0))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("12% vs la semana pasada")
                            }
                        } else {
                            Text(
                                "🔒 Comparaciones disponibles en Premium",
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        Text(
                            "LUN   MAR   MIÉ   JUE   VIE   SÁB   DOM",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            // CATEGORÍAS DINÁMICAS
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDDEEDD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Categorías principales")

                        Spacer(modifier = Modifier.height(12.dp))

                        categorias.forEach { (title, amount, progress) ->
                            CategoryItem(title, amount, progress)
                        }

                        // 🔒 MENSAJE SI ES FREE
                        if (!isPremium) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "🔒 Más categorías en Premium",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // HISTORIAL HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Historial", style = MaterialTheme.typography.titleMedium)
                    Text("Ver todo", color = Color(0xFF1DB954))
                }
            }

            // HISTORIAL LISTA
            items(
                listOf(
                    "Supermercado" to "-$142.30",
                    "Luz eléctrica" to "-$89.00"
                )
            ) { (title, amount) ->
                HistoryItem(title, amount)
            }

            // 🔒 TARJETA PREMIUM (UPSSELL)
            if (!isPremium) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {

                            Text("Funciones Premium 🔒")

                            Text(
                                "Desbloquea comparaciones, más categorías y análisis avanzados."
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(onClick = { /* ir a pantalla de pago */ }) {
                                Text("Mejorar a Premium")
                            }
                        }
                    }
                }
            }

            // TARJETA PRESUPUESTO (solo premium)
            if (isPremium) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF00C853))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {

                            Text("PRESUPUESTO MENSUAL", color = Color.White)

                            Text(
                                "¡Lo estás haciendo muy bien esta semana!",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "Has gastado 20% menos en entretenimiento comparado con el mes pasado.",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun CategoryItem(title: String, amount: String, progress: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title)
            Text(amount)
        }

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun HistoryItem(title: String, amount: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title)
            Text(amount, color = Color.Red)
        }
    }
}

