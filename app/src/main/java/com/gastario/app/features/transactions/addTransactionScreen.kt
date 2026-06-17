package com.gastario.app.features.transactions

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gastario.app.data.model.Categoria
import com.gastario.app.data.model.Movimiento
import com.gastario.app.features.categorys.CategoryViewModel
import com.gastario.app.features.components.Alert
import com.gastario.app.features.components.PrimaryButton
import com.gastario.app.features.user.UserViewModel
import java.time.Instant
import java.time.ZoneId

val categoriasFree = listOf(
    Categoria("1", "Comida"),
    Categoria("2", "Transporte"),
    Categoria("3", "Salud"),
    Categoria("4", "Entretenimiento")
)


// 🔹 Tu verde oscuro oficial (privado para no chocar con otros archivos)
 val greenColor = Color(0xFF2E7D32)

@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransaccionScreen(
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel()
) {
    var monto by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var categoriaId by rememberSaveable { mutableStateOf("") }
    var categoriaNombre by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf(0L) }
    var isExpense by rememberSaveable { mutableStateOf(true) }
    var showDialogLimite by rememberSaveable { mutableStateOf(false) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showDialogRestantes by rememberSaveable { mutableStateOf(false) }
    var showDialogCategoria by rememberSaveable { mutableStateOf(false) }
    var isSaving by rememberSaveable { mutableStateOf(false) }
    val viewModel: TransactionViewModel = hiltViewModel()
    val viewModelCat: CategoryViewModel = hiltViewModel()
    val isPremium by userViewModel.isPremium.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.cargarMovimientos()
        viewModelCat.getCategory()
    }

    val categoriasdb by viewModelCat.categorias.collectAsState()
    val categorias = categoriasFree + categoriasdb
    val movimientos by viewModel.movimientos.collectAsState()
    val calendar = java.util.Calendar.getInstance()
    val mesActual = calendar.get(java.util.Calendar.MONTH)
    val anioActual = calendar.get(java.util.Calendar.YEAR)

    val movimientosMes = movimientos.filter { mov ->
        calendar.timeInMillis = mov.fecha
        val mesMov = calendar.get(java.util.Calendar.MONTH)
        val anioMov = calendar.get(java.util.Calendar.YEAR)
        mesMov == mesActual && anioMov == anioActual
    }

    // 🔹 Forzamos a que todo lo que esté aquí dentro use el verde como color primario
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
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = greenColor,
                                focusedLabelColor = greenColor,
                                cursorColor = greenColor,
                                focusedLeadingIconColor = greenColor
                            )
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            /* DESCRIPCIÓN */
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
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = greenColor,
                                focusedLabelColor = greenColor,
                                cursorColor = greenColor
                            )
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("CATEGORÍA", color = Color.Gray)

                            IconButton(onClick = {
                                if (isPremium) {
                                    navController.navigate("categorias")
                                } else {
                                    showDialogCategoria = true
                                }
                            }) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Agregar",
                                    tint = greenColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        SelectorCategoria(
                            categoria = categoriaNombre,
                            onCategoriaSelected = { id, nombre ->
                                categoriaId = id
                                categoriaNombre = nombre
                            },
                            categorias = categorias,
                            isPremium=isPremium
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
                PrimaryButton("Guardar Movimiento",isSaving) {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0

                    if (monto.isBlank() || descripcion.isBlank() || categoriaNombre.isBlank() || fecha == 0L) {
                        showDialog = true
                        return@PrimaryButton
                    }
                    isSaving=true

                    val movimiento = Movimiento(
                        id = "",
                        categoriaId = categoriaId,
                        categoriaNombre = categoriaNombre,
                        tipo = if (isExpense) "gasto" else "ingreso",
                        monto = montoDouble,
                        descripcion = descripcion,
                        fecha = fecha
                    )
                    viewModel.addMovimiento(movimiento) {
                        navController.popBackStack()
                    }
                }

                Alert(
                    text = "Agrega todos los datos del formulario",
                    title = "Datos incompletos",
                    showDialog = showDialog,
                    onDismiss = { showDialog = false }
                )

                Alert(
                    title = "Límite alcanzado 🔒",
                    text = "Ya usaste tus 50 movimientos del mes.\nDesbloquea movimientos ilimitados con Premium 💎",
                    showDialog = showDialogLimite,
                    onDismiss = { showDialogLimite = false; navController.navigate("premium") }
                )

                Alert(
                    title = "Límite alcanzado 🔒",
                    text = "Te quedan 5 movimientos en el plan gratis",
                    showDialog = showDialogRestantes,
                    onDismiss = { showDialogRestantes = false }
                )

                Alert(
                    title = "Desbloquea categorías personalizadas",
                    text = "Crea tus propias categorías como \"Café\", \"Gym\" o \"Salidas\" y organiza tus finanzas a tu manera.\n\nDisponible solo en Premium.",
                    showDialog = showDialogCategoria,
                    onDismiss = {
                        showDialogCategoria = false
                        navController.navigate("premium")
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorCategoria(
    categoria: String,
    onCategoriaSelected: (String, String) -> Unit,
    categorias: List<Categoria>,
    isPremium: Boolean
) {
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
                .fillMaxWidth(),
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
            modifier = Modifier.background(Color.White) // 🔹 Hacemos el fondo del menú completamente blanco
        ) {
            val listaMostrar = if (isPremium) categorias else categoriasFree

            listaMostrar.forEach {
                DropdownMenuItem(
                    text = { Text(it.nombre) },
                    onClick = {
                        onCategoriaSelected(it.id, it.nombre)
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
        ) {
            OutlinedTextField(
                value = fechaTexto,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Fecha") },
                placeholder = { Text("Selecciona una fecha") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color.Gray,
                    disabledTextColor = Color.Black,
                    disabledLabelColor = Color.Gray,
                    focusedBorderColor = greenColor,
                    focusedLabelColor = greenColor
                )
            )
        }

        if (showDialog) {
            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                // 🔹 Forzamos también el fondo del calendario a blanco para que combine
                colors = DatePickerDefaults.colors(containerColor = Color.White),
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val localDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.of("UTC"))
                                    .toLocalDate()

                                val correctedMillis = localDate
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli()

                                onFechaSelected(correctedMillis)
                            }
                            showDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = greenColor)
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                    ) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        todayContentColor = greenColor,
                        todayDateBorderColor = greenColor,
                        selectedDayContainerColor = greenColor,
                        selectedDayContentColor = Color.White,
                        currentYearContentColor = greenColor,
                        selectedYearContainerColor = greenColor,
                        selectedYearContentColor = Color.White
                    )
                )
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
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) greenColor else Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}