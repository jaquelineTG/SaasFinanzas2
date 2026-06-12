package com.example.saasfinanzas.features.budget

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.saasfinanzas.data.model.Presupuesto
import com.example.saasfinanzas.features.categorys.CategoryViewModel
import com.example.saasfinanzas.features.components.Alert
import com.example.saasfinanzas.features.components.PrimaryButton
import com.example.saasfinanzas.features.transactions.categoriasFree
import java.util.Calendar

// 🔹 Tu verde oscuro oficial (privado)
private val greenColor = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudget(navController: NavController) {

    val viewModel: BudgetViewModel = hiltViewModel()

    var expanded by remember { mutableStateOf(false) }

    var categoriaId by remember { mutableStateOf("") }
    var categoriaNombre by remember { mutableStateOf("") }

    var montoLimite by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()

    val meses = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    val anios = (2020..2030).map { it.toString() }

    var mesSeleccionado by remember { mutableStateOf(meses[calendar.get(Calendar.MONTH)]) }
    var anioSeleccionado by remember { mutableStateOf(calendar.get(Calendar.YEAR).toString()) }
    val mesNumero = meses.indexOf(mesSeleccionado) + 1
    val anioNumero = anioSeleccionado.toInt()
    var expandedMes by remember { mutableStateOf(false) }
    var expandedAnio by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialogCategoria by rememberSaveable { mutableStateOf(false) }

    val isPremium = true
    val viewModelCat: CategoryViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModelCat.getCategory()
    }

    val categoriasdb by viewModelCat.categorias.collectAsState()

    val categorias = categoriasFree + categoriasdb

    // 🔹 Forzamos el color verde primario para matar el morado en todos los menús desplegables
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = greenColor,
            primaryContainer = greenColor.copy(alpha = 0.1f),
            onPrimaryContainer = greenColor
        )
    ) {
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                }
                Text(
                    "Agregar Presupuesto",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            /* CATEGORIA */
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "CATEGORÍA",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )

                        IconButton(onClick = {
                            if (isPremium) {
                                navController.navigate("categorias")
                            } else {
                                showDialogCategoria = true
                            }
                        }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Agregar categoría",
                                tint = greenColor // Ícono en verde oscuro
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = categoriaNombre,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Selecciona una categoría") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = greenColor,
                                focusedLabelColor = greenColor,
                                cursorColor = greenColor,
                                focusedTrailingIconColor = greenColor
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White) // 🔹 Fondo 100% blanco
                        ) {
                            val listaCategorias = if (isPremium) categorias else categoriasFree

                            listaCategorias.forEach {
                                DropdownMenuItem(
                                    text = { Text(it.nombre) },
                                    onClick = {
                                        categoriaId = it.id
                                        categoriaNombre = it.nombre
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Alert(
                title = "Desbloquea categorías personalizadas",
                text = "Crea tus propias categorías como \"Café\", \"Gym\" o \"Salidas\" y organiza tus finanzas a tu manera.\n\nDisponible solo en Premium.",
                showDialog = showDialogCategoria,
                onDismiss = {
                    showDialogCategoria = false
                    navController.navigate("premium")
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            /* MONTO LIMITE */
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "MONTO LÍMITE",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = montoLimite,
                        onValueChange = { montoLimite = it },
                        leadingIcon = { Text("$") },
                        placeholder = { Text("0.00") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = greenColor,
                            focusedLabelColor = greenColor,
                            cursorColor = greenColor,
                            focusedLeadingIconColor = greenColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            /* MES Y AÑO */
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ElevatedCard(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("MES", color = Color.Gray)
                        Spacer(modifier = Modifier.height(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedMes,
                            onExpandedChange = { expandedMes = !expandedMes }
                        ) {
                            OutlinedTextField(
                                value = mesSeleccionado,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expandedMes)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = greenColor,
                                    focusedLabelColor = greenColor,
                                    cursorColor = greenColor,
                                    focusedTrailingIconColor = greenColor
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expandedMes,
                                onDismissRequest = { expandedMes = false },
                                modifier = Modifier.background(Color.White) // 🔹 Fondo 100% blanco
                            ) {
                                meses.forEach {
                                    DropdownMenuItem(
                                        text = { Text(it) },
                                        onClick = {
                                            mesSeleccionado = it
                                            expandedMes = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                ElevatedCard(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("AÑO", color = Color.Gray)
                        Spacer(modifier = Modifier.height(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedAnio,
                            onExpandedChange = { expandedAnio = !expandedAnio }
                        ) {
                            OutlinedTextField(
                                value = anioSeleccionado,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expandedAnio)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = greenColor,
                                    focusedLabelColor = greenColor,
                                    cursorColor = greenColor,
                                    focusedTrailingIconColor = greenColor
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expandedAnio,
                                onDismissRequest = { expandedAnio = false },
                                modifier = Modifier.background(Color.White) // 🔹 Fondo 100% blanco
                            ) {
                                anios.forEach {
                                    DropdownMenuItem(
                                        text = { Text(it) },
                                        onClick = {
                                            anioSeleccionado = it
                                            expandedAnio = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            /* BOTON */
            PrimaryButton("Añadir Presupuesto") {
                val montoDouble = montoLimite.toDoubleOrNull()

                if (
                    categoriaId.isBlank() ||
                    categoriaNombre.isBlank() ||
                    montoDouble == null || montoDouble <= 0.0 ||
                    mesNumero <= 0 ||
                    anioNumero <= 0
                ) {
                    showDialog = true
                    return@PrimaryButton
                }

                val presupuesto = Presupuesto(
                    id = "",
                    categoriaId = categoriaId,
                    categoriaNombre = categoriaNombre,
                    montoLimite = montoDouble,
                    mes = mesNumero,
                    anio = anioNumero,
                    creadoEn = System.currentTimeMillis()
                )

                viewModel.addBudget(presupuesto) {
                    navController.popBackStack()
                }
            }

            Alert(
                text = "Agrega todos los datos del formulario",
                title = "Datos incompletos",
                showDialog = showDialog,
                onDismiss = { showDialog = false }
            )
        }
    }
}