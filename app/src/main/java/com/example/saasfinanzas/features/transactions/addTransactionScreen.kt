package com.example.saasfinanzas.features.transactions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.features.components.PrimaryButton
import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
import java.time.ZoneId


data class CategoriaFree(
    val id: String,
    val nombre: String
)
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransaccionScreen(
    navController: NavController
) {

    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf("") }
    var categoriaNombre by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(0L) }
    var isExpense by remember { mutableStateOf(true) }

    val viewModel: TransactionViewModel = hiltViewModel()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item { Spacer(modifier = Modifier.height(20.dp)) }

        /* 🔹 HEADER */
        item {
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
                    "Agregar Movimiento",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        /* 🔹 TIPO */
        item {
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
                ) {

                    ToggleButton(
                        text = "Ingreso",
                        selected = !isExpense,
                        onClick = { isExpense = false }
                    )

                    ToggleButton(
                        text = "Gasto",
                        selected = isExpense,
                        onClick = { isExpense = true }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        /* 🔹 MONTO */
        item {
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(Modifier.padding(16.dp)) {

                    Text("MONTO", color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = monto,
                        onValueChange = {
                            if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                                monto = it
                            }
                        },
                        leadingIcon = { Text("$") },
                        placeholder = { Text("0.00") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        /* 🔹 DESCRIPCIÓN */
        item {
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(Modifier.padding(16.dp)) {

                    Text("DESCRIPCIÓN", color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        placeholder = { Text("Ej. Pago de renta") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        /* 🔹 CATEGORÍA */
        item {
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(Modifier.padding(16.dp)) {

                    Text("CATEGORÍA", color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))

                    SelectorCategoria(
                        categoria = categoriaNombre,
                        onCategoriaSelected = { id, nombre ->
                            categoriaId = id
                            categoriaNombre = nombre
                        }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        /* 🔹 FECHA */
        item {
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(Modifier.padding(16.dp)) {

                    Text("FECHA", color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))

                    SelectorFecha(
                        fecha = fecha,
                        onFechaSelected = { fecha = it }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }

        /* 🔹 BOTÓN */
        item {
            PrimaryButton("Guardar Movimiento") {

                val montoDouble = monto.toDoubleOrNull() ?: 0.0

                if (
                    monto.isBlank() ||
                    descripcion.isBlank() ||
                    categoriaNombre.isBlank() ||
                    fecha == 0L
                ) {
                    println("Faltan datos")
                    return@PrimaryButton
                }

                val movimiento = Movimiento(
                    id = "",
                    categoriaId = categoriaId,
                    categoriaNombre = categoriaNombre,
                    tipo = if (isExpense) "gasto" else "ingreso",
                    monto = montoDouble,
                    descripcion = descripcion,
                    fecha = fecha
                )

                viewModel.addMovimiento(movimiento)

                navController.popBackStack()
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorCategoria(
    categoria: String,
    onCategoriaSelected: (String,String) -> Unit
) {

    val categoriasFree = listOf(
        CategoriaFree("1", "Comida"),
        CategoriaFree("2", "Transporte"),
        CategoriaFree("3", "Salud"),
        CategoriaFree("4", "Entretenimiento")
    )

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = categoria,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría") },
            placeholder = { Text("Selecciona una categoría") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categoriasFree.forEach {
                DropdownMenuItem(
                    text = { Text(it.nombre) },
                    onClick = {
                        onCategoriaSelected(it.nombre,it.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFecha(
    fecha: Long,
    onFechaSelected: (Long) -> Unit
) {

    var showDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    val fechaTexto = fecha.takeIf { it != 0L }?.let {
        Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .toString()
    } ?: ""

    Column {

        OutlinedTextField(
            value = fechaTexto,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha") },
            placeholder = { Text("Selecciona una fecha") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
        )

        if (showDialog) {
            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onFechaSelected(it)
                        }
                        showDialog = false
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun RowScope.ToggleButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .background(
                if (selected) Color.White else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable(
                indication = null, //quita efecto (sombra/ripple)
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color(0xFF22C55E) else Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}