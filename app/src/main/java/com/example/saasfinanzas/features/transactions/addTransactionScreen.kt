package com.example.saasfinanzas.features.transactions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransaccionScreen(
    navHostController: NavController,

) {

    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf("") }
    var categoriaNombre by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(0L) }
    var isExpense by remember { mutableStateOf(true) }
    val viewModel: TransactionViewModel = hiltViewModel()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("New Movement", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.Default.Cancel, contentDescription = "Cerrar")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item { Spacer(modifier = Modifier.height(20.dp)) }

            // 🔹 Tipo (Income / Expense)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFEFEF), RoundedCornerShape(16.dp))
                        .padding(4.dp)
                ) {

                    ToggleButton(
                        text = "Income",
                        selected = !isExpense,
                        onClick = { isExpense = false }
                    )

                    ToggleButton(
                        text = "Expense",
                        selected = isExpense,
                        onClick = { isExpense = true }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }

            // 🔹 Monto
            item {
                OutlinedTextField(
                    value = monto,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            monto = it
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Amount") },
                    placeholder = { Text("$ 0.00") },
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            // 🔹 Descripción
            item {
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    placeholder = { Text("e.g. Monthly Salary") },
                    shape = RoundedCornerShape(16.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            // 🔹 Categoria
            item {
                SelectorCategoria(
                    categoria = categoriaNombre,
                    onCategoriaSelected = { id, nombre ->
                        categoriaId = id
                        categoriaNombre = nombre
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            // 🔹 Fecha
            item {
                SelectorFecha(
                    fecha = fecha,
                    onFechaSelected = { fecha = it }
                )
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }

            //  BOTÓN GUARDAR
            item {
                PrimaryButton("Add Movement") {

                    val montoDouble = monto.toDoubleOrNull() ?: 0.0

                    if (monto.isBlank() || descripcion.isBlank() || categoriaNombre.isBlank() || fecha == 0L) {
                        println("Faltan datos")
                        return@PrimaryButton
                    }

                    val movimiento = Movimiento(
                        categoriaId = categoriaId,
                        categoriaNombre = categoriaNombre,
                        tipo = if (isExpense) "gasto" else "ingreso",
                        monto = montoDouble,
                        descripcion = descripcion,
                        fecha = fecha
                    )

                    viewModel.addMovimiento(movimiento)

                    navHostController.popBackStack()
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorCategoria(
    categoria: String,
    onCategoriaSelected: (String,String) -> Unit
) {
    data class Categoria(
        val id: String,
        val nombre: String
    )
    val categorias = listOf(
        Categoria("1", "Comida"),
        Categoria("2", "Transporte"),
        Categoria("3", "Salud"),
        Categoria("4", "Entretenimiento")
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
            categorias.forEach {
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
            .clickable { onClick() }
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