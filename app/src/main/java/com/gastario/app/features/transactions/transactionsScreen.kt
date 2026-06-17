package com.gastario.app.features.transactions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gastario.app.data.model.Movimiento
import com.gastario.app.data.model.Categoria
import com.gastario.app.features.categorys.CategoryViewModel
import com.gastario.app.features.user.UserViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId




@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(navHostController: NavController,userViewModel: UserViewModel = hiltViewModel()) {

    var tipoSeleccionado by remember { mutableStateOf("todas") }
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }
    val viewModel: TransactionViewModel = hiltViewModel()
    val movimientos by viewModel.movimientos.collectAsState()
    var movimientoEditar by remember { mutableStateOf<Movimiento?>(null) }
    var mostrarConfirmacionEliminar by remember { mutableStateOf<Movimiento?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }
    var movimientoExpandidoId by remember { mutableStateOf<String?>(null) }

    val viewModelCat: CategoryViewModel = hiltViewModel()
    val isPremium by userViewModel.isPremium.collectAsState()
    LaunchedEffect(Unit) {
        viewModelCat.getCategory()
    }

    val categoriasdb by viewModelCat.categorias.collectAsState()
    val categorias = categoriasFree + categoriasdb

    val navBackStackEntry by navHostController.currentBackStackEntryAsState()

    LaunchedEffect(navBackStackEntry) {
        viewModel.cargarMovimientos()
    }

    val listaFiltrada = movimientos.filter { transaccion ->
        val filtroTipo = when (tipoSeleccionado) {
            "ingreso" -> transaccion.tipo == "ingreso"
            "gasto" -> transaccion.tipo == "gasto"
            else -> true
        }

        val filtroFecha = fechaSeleccionada?.let { fechaSeleccionada ->
            val fechaMovimiento = Instant.ofEpochMilli(transaccion.fecha)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            fechaMovimiento == fechaSeleccionada
        } ?: true

        filtroTipo && filtroFecha
    }

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Movimientos", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = {
                        navHostController.navigate("añadir_movimiento")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3F4F6),
                    scrolledContainerColor = Color(0xFFF3F4F6)))
        }
    ) { padding ->

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {

            item {
                FiltrosTransacciones(
                    tipoSeleccionado,
                    { tipoSeleccionado = it },
                    { fechaSeleccionada = it }
                )
            }

            items(listaFiltrada) { transaccion ->
                TransaccionItem(
                    categoriaNombre = transaccion.categoriaNombre,
                    monto = transaccion.monto,
                    descripcion = transaccion.descripcion,
                    tipo = transaccion.tipo,
                    expanded = movimientoExpandidoId == transaccion.id,
                    onExpand = {
                        movimientoExpandidoId =
                            if (movimientoExpandidoId == transaccion.id) null
                            else transaccion.id
                    },
                    onDelete = { mostrarConfirmacionEliminar = transaccion },
                    onEdit = { movimientoEditar = transaccion }
                )
            }
        }

        // 🔹 MODAL DE EDITAR (Diseño mejorado)
        movimientoEditar?.let { movimiento ->
            EditMovimientoDialog(
                movimiento = movimiento,
                onDismiss = { movimientoEditar = null },
                onGuardar = { descripcion, monto, categoria ->
                    viewModel.updateMovimiento(
                        movimiento.copy(
                            descripcion = descripcion,
                            monto = monto,
                            categoriaNombre = categoria
                        )
                    )
                    movimientoExpandidoId = null
                    movimientoEditar = null
                    mensajeExito = "Movimiento actualizado correctamente"
                },
                categorias = categorias,
                isPremium=isPremium
            )
        }

        // 🔹 MODAL DE ELIMINAR (Diseño mejorado)
        mostrarConfirmacionEliminar?.let { movimiento ->
            AlertDialog(
                onDismissRequest = { mostrarConfirmacionEliminar = null },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                title = { Text("Eliminar movimiento", fontWeight = FontWeight.Bold, color = Color.Black) },
                text = { Text("¿Estás seguro de eliminar este movimiento? Esta acción no se puede deshacer.", color = Color.DarkGray) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteMovimiento(movimiento.id)
                            mostrarConfirmacionEliminar = null
                            mensajeExito = "Movimiento eliminado correctamente"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(
                        onClick = { mostrarConfirmacionEliminar = null }
                    ) { Text("Cancelar", color = Color.Gray, fontWeight = FontWeight.Medium) }
                }
            )
        }

        // 🔹 MODAL DE ÉXITO (Diseño mejorado)
        mensajeExito?.let { mensaje ->
            AlertDialog(
                onDismissRequest = { mensajeExito = null },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                title = { Text("Éxito", fontWeight = FontWeight.Bold, color = greenColor) },
                text = { Text(mensaje, color = Color.DarkGray) },
                confirmButton = {
                    Button(
                        onClick = { mensajeExito = null },
                        colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            )
        }
    }
}

fun getIconByCategory(categoria: String): ImageVector {
    return when (categoria) {
        "Comida" -> Icons.Filled.Fastfood
        "Transporte" -> Icons.Filled.DirectionsCar
        "Salud" -> Icons.Filled.Favorite
        "Entretenimiento" -> Icons.Filled.Movie
        else -> Icons.Filled.AttachMoney
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransaccionItem(
    categoriaNombre: String,
    monto: Double,
    descripcion: String,
    tipo: String,
    expanded: Boolean,
    onExpand: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val offsetX by animateDpAsState(
        targetValue = if (expanded) (-100).dp else 0.dp,
        label = "offset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // BOTONES DETRÁS DE LA CARD
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF2196F3))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }

        // CARD
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.elevatedCardElevation(2.dp),
            shape = RoundedCornerShape(24.dp), // Diseño más redondeado
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = offsetX)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onExpand() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFE8F5E9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconByCategory(categoriaNombre),
                        contentDescription = null,
                        tint = greenColor // Ícono verde
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = descripcion, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = categoriaNombre, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Text(
                    text = "$${"%.2f".format(if (tipo == "gasto") -monto else monto)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (tipo == "gasto") Color(0xFFD32F2F) else greenColor
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FiltrosTransacciones(
    tipoSeleccionado: String,
    onTipoChange: (String) -> Unit,
    onFechaChange: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FechaFiltro(onFechaChange)
        Spacer(modifier = Modifier.width(10.dp))
        FiltroChip("Todas", selected = tipoSeleccionado == "todas") { onTipoChange("todas") }
        Spacer(modifier = Modifier.width(8.dp))
        FiltroChip("Ingresos", selected = tipoSeleccionado == "ingreso") { onTipoChange("ingreso") }
        Spacer(modifier = Modifier.width(8.dp))
        FiltroChip("Gastos", selected = tipoSeleccionado == "gasto") { onTipoChange("gasto") }
    }
}

@Composable
fun FiltroChip(
    texto: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) Color(0xFFD1FAE5) else Color.White
    val textColor = if (selected) greenColor else Color.Gray

    Box(
        modifier = Modifier
            .background(background, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(text = texto, color = textColor, style = MaterialTheme.typography.bodySmall, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaFiltro(
    onFechaSeleccionada: (LocalDate) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color.White, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showPicker = true }
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(text = "Fecha", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }

        if (showPicker) {
            val datePickerState = rememberDatePickerState()

            DatePickerDialog(
                onDismissRequest = { showPicker = false },
                // 🔹 Forzamos el fondo del cuadro a blanco
                colors = DatePickerDefaults.colors(containerColor = Color.White),
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                val fecha = Instant.ofEpochMilli(it)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                onFechaSeleccionada(fecha)
                            }
                            showPicker = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = greenColor) // Botón Aceptar Verde
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showPicker = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray) // Botón Cancelar Gris
                    ) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        // 🔹 Exterminamos TODO el morado del calendario
                        containerColor = Color.White,
                        titleContentColor = greenColor,
                        headlineContentColor = greenColor,
                        weekdayContentColor = Color.DarkGray,
                        subheadContentColor = Color.DarkGray,
                        navigationContentColor = greenColor,
                        yearContentColor = Color.Black,
                        currentYearContentColor = greenColor,
                        selectedYearContainerColor = greenColor,
                        selectedYearContentColor = Color.White,
                        dayContentColor = Color.Black,
                        selectedDayContainerColor = greenColor,
                        selectedDayContentColor = Color.White,
                        todayContentColor = greenColor,
                        todayDateBorderColor = greenColor
                    )
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EditMovimientoDialog(
    movimiento: Movimiento,
    onDismiss: () -> Unit,
    onGuardar: (String, Double, String) -> Unit,
    categorias: List<Categoria>,
    isPremium: Boolean
) {
    var descripcion by remember { mutableStateOf(movimiento.descripcion) }
    var monto by remember { mutableStateOf(movimiento.monto.toString()) }
    var expandedCategoria by remember { mutableStateOf(false) }
    var categoriaSeleccionada by remember { mutableStateOf(movimiento.categoriaNombre) }


    // 🔹 Envolvemos el diálogo en un MaterialTheme para evitar el morado en el menú desplegable
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = greenColor,
            primaryContainer = greenColor.copy(alpha = 0.1f),
            onPrimaryContainer = greenColor
        )
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color.White, // 🔹 Fondo blanco limpio
            shape = RoundedCornerShape(24.dp), // 🔹 Bordes premium
            title = { Text("Editar movimiento", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = greenColor,
                            focusedLabelColor = greenColor,
                            cursorColor = greenColor
                        )
                    )

                    OutlinedTextField(
                        value = monto,
                        onValueChange = { monto = it },
                        label = { Text("Monto") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = greenColor,
                            focusedLabelColor = greenColor,
                            cursorColor = greenColor
                        )
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedCategoria,
                        onExpandedChange = { expandedCategoria = !expandedCategoria }
                    ) {
                        OutlinedTextField(
                            value = categoriaSeleccionada,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria)
                            },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = greenColor,
                                focusedLabelColor = greenColor,
                                cursorColor = greenColor,
                                focusedTrailingIconColor = greenColor
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expandedCategoria,
                            onDismissRequest = { expandedCategoria = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            val listaCategorias = if (isPremium) categorias else categoriasFree
                            listaCategorias.forEach { categoria ->
                                DropdownMenuItem(
                                    text = { Text(categoria.nombre) },
                                    onClick = {
                                        categoriaSeleccionada = categoria.nombre
                                        expandedCategoria = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onGuardar(
                            descripcion,
                            monto.toDoubleOrNull() ?: 0.0,
                            categoriaSeleccionada
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Cancelar", color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }
        )
    }
}