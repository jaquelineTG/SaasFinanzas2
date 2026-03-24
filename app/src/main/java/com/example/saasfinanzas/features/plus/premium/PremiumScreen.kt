package com.example.saasfinanzas.features.plus.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import androidx.compose.foundation.lazy.LazyColumn

@Composable
fun PremiumScreen(navHostController: NavHostController) {

    Scaffold(

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEAF2EC))
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                navHostController.popBackStack()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Financial Journey")
                    }

                    Row {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                }
            }

            // HERO CARD
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDFF5E4))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.DarkGray)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFB7E4C7))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("ACCESO ELITE")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "Desbloquea todo tu potencial financiero",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Ve más allá del control de gastos. Empieza a construir tu riqueza con herramientas premium diseñadas para crecer.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // BENEFICIO GRANDE
            item {
                FeatureCard(
                    title = "Presupuestos ilimitados",
                    desc = "Gestiona todos tus proyectos sin límites.",
                    icon = Icons.Default.AllInclusive
                )
            }

            // BENEFICIOS PEQUEÑOS
            item {
                Row(modifier = Modifier.fillMaxWidth()) {

                    SmallFeatureCard(
                        title = "Categorías personalizadas",
                        icon = Icons.Default.Category,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    SmallFeatureCard(
                        title = "Sin anuncios",
                        icon = Icons.Default.Block,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // REPORTES
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00C853))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Reportes avanzados", color = Color.White)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Obtén análisis inteligentes sobre tus gastos y tendencias financieras.",
                            color = Color.White
                        )
                    }
                }
            }

            // PLAN
            item {
                Card(
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text("Plan mensual premium")

                        Text(
                            "$4.99 / mes",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Text("Cancela en cualquier momento. Sin cargos ocultos.")

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                        ) {
                            Text("Mejorar ahora")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Al suscribirte aceptas nuestros términos.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureCard(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(shape = RoundedCornerShape(20.dp)) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDFF5E4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(title)
                Text(desc, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun SmallFeatureCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title)
        }
    }
}

