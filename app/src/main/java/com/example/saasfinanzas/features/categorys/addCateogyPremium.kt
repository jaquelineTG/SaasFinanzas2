package com.example.saasfinanzas.features.categorys

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
// import androidx.hilt.navigation.compose.hiltViewModel
import com.example.saasfinanzas.data.model.Categoria
import com.example.saasfinanzas.features.components.Alert
import com.example.saasfinanzas.features.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategory(navController: NavController) {

   val viewModel: CategoryViewModel = hiltViewModel()

    // Estado para el Nombre
    var nombre by remember { mutableStateOf("") }

    // Estados para el Tipo (Ingreso / Gasto)
    val opcionesTipo = listOf("ingreso", "gasto")
    var tipoSeleccionado by remember { mutableStateOf("") }

    // Estados para el Ícono visual
    val availableIcons = listOf(
        "fastfood" to Icons.Default.Fastfood,
        "restaurant" to Icons.Default.Restaurant,
        "coffee" to Icons.Default.Coffee,
        "car" to Icons.Default.DirectionsCar,
        "bus" to Icons.Default.DirectionsBus,
        "train" to Icons.Default.Train,
        "flight" to Icons.Default.Flight,
        "movie" to Icons.Default.Movie,
        "sports" to Icons.Default.SportsSoccer,
        "fitness" to Icons.Default.FitnessCenter,
        "health" to Icons.Default.Favorite,
        "medical" to Icons.Default.LocalHospital,
        "home" to Icons.Default.Home,
        "electricity" to Icons.Default.Bolt,
        "water" to Icons.Default.WaterDrop,
        "work" to Icons.Default.Work,
        "school" to Icons.Default.School,
        "book" to Icons.Default.MenuBook,
        "shopping" to Icons.Default.ShoppingBag,
        "store" to Icons.Default.Store,
        "gift" to Icons.Default.CardGiftcard,
        "pets" to Icons.Default.Pets,
        "childcare" to Icons.Default.ChildCare,
        "phone" to Icons.Default.PhoneAndroid,
        "internet" to Icons.Default.Wifi,
        "bank" to Icons.Default.AccountBalance,
        "savings" to Icons.Default.Savings,
        "creditcard" to Icons.Default.CreditCard,
        "travel" to Icons.Default.Luggage,
        "music" to Icons.Default.MusicNote,
        "subscriptions" to Icons.Default.Subscriptions
    )
    // Por defecto no hay ninguno seleccionado, o puedes poner: availableIcons.first().first
    var iconoSeleccionado by remember { mutableStateOf("") }

    // Estado para la alerta de validación
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .padding(24.dp)
    ) {

        /* HEADER */
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
            }

            Text(
                "Agregar Categoría",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        /* NOMBRE DE LA CATEGORÍA */
        ElevatedCard(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "NOMBRE DE CATEGORÍA",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholder = { Text("Ej: Supermercado") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        /* TIPO DE CATEGORÍA (Botones lado a lado / Chips) */
        ElevatedCard(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "TIPO",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    opcionesTipo.forEach { tipo ->
                        val isSelected = tipoSeleccionado == tipo
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { tipoSeleccionado = tipo }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tipo.replaceFirstChar { it.uppercase() },
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        /* SELECCIÓN DE ÍCONO VISUAL (LazyRow) */
        ElevatedCard(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "ÍCONO",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(availableIcons) { (key, iconVector) ->
                        val isSelected = iconoSeleccionado == key

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { iconoSeleccionado = key },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = key,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        /* BOTÓN DE GUARDAR */
        PrimaryButton("Añadir Categoría") {

            // Validaciones
            if (nombre.isBlank() || tipoSeleccionado.isBlank() || iconoSeleccionado.isBlank()) {
                showDialog = true
                return@PrimaryButton
            }

            val nuevaCategoria = Categoria(
                id = "",
                nombre = nombre,
                icono = iconoSeleccionado,
                tipo = tipoSeleccionado
            )

            // Lógica del ViewModel
        viewModel.addCategory(nuevaCategoria) {
                navController.popBackStack()
            }


            navController.popBackStack()
        }

        /* ALERTA DE VALIDACIÓN */
        Alert(
            text = "Por favor, completa todos los datos del formulario (Nombre, Tipo e Ícono).",
            title = "Datos incompletos",
            showDialog = showDialog,
            onDismiss = { showDialog = false }
        )
    }
}