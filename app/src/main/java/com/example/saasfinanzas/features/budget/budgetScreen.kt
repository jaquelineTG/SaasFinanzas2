package com.example.saasfinanzas.features.budget

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.saasfinanzas.data.model.Categoria
import com.example.saasfinanzas.data.model.Presupuesto
import com.example.saasfinanzas.features.categorys.CategoryViewModel
import com.example.saasfinanzas.features.transactions.TransactionViewModel
import com.example.saasfinanzas.features.transactions.categoriasFree

// 🔹 Tu verde oscuro oficial (privado para evitar conflictos)
private val greenColor = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(navController: NavController) {
    val viewModel: BudgetViewModel = hiltViewModel()
    val presupuestos = viewModel.presupuestos.collectAsState()

    val viewmodelMov: TransactionViewModel = hiltViewModel()
    val movimientosSate = viewmodelMov.movimientos.collectAsState()
    val movimientos = movimientosSate.value

    // 🔹 ViewModel para obtener las categorías en el diálogo de edición
    val viewModelCat: CategoryViewModel = hiltViewModel()
    val categoriasdb by viewModelCat.categorias.collectAsState()
    val categorias = categoriasFree + categoriasdb

    var presupuestoEditar by remember { mutableStateOf<Presupuesto?>(null) }
    var presupuestoEliminar by remember { mutableStateOf<Presupuesto?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }
    var presupuestoExpandidoId by remember { mutableStateOf<String?>(null) }

    // 1. Escuchamos los cambios en la navegación
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // 2. Recargamos datos al entrar
    LaunchedEffect(navBackStackEntry) {
        viewModel.getBudgets()
        viewmodelMov.cargarMovimientos()
        viewModelCat.getCategory()
    }

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Presupuestos", fontWeight = FontWeight.SemiBold)
                },
                actions = {
                    IconButton(onClick = { navController.navigate("añadir_presupuestos") }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3F4F6),
                    scrolledContainerColor = Color(0xFFF3F4F6)
                )
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
                items(presupuestos.value) { presupuesto ->
                    var sumGastos = 0.0
                    movimientos.forEach {
                        if (it.categoriaId == presupuesto.categoriaId) {
                            sumGastos += it.monto
                        }
                    }
                    BudgetItem(
                        presupuesto = presupuesto,
                        sumGastos = sumGastos,
                        expanded = presupuestoExpandidoId == presupuesto.id,
                        onExpand = {
                            presupuestoExpandidoId = if (presupuestoExpandidoId == presupuesto.id) null else presupuesto.id
                        },
                        onDelete = { presupuestoEliminar = presupuesto },
                        onEdit = { presupuestoEditar = presupuesto }
                    )
                }
            }

            // 🔹 MODAL DE EDITAR
            presupuestoEditar?.let { presupuesto ->
                EditBudgetDialog(
                    presupuesto = presupuesto,
                    categorias = categorias,
                    onDismiss = { presupuestoEditar = null },
                    onGuardar = { categoria, monto ->
                        viewModel.updateBudget(
                            presupuesto.copy(
                                categoriaNombre = categoria,
                                montoLimite = monto
                            )
                        )
                        presupuestoExpandidoId = null
                        presupuestoEditar = null
                        mensajeExito = "Presupuesto actualizado correctamente"
                    }
                )
            }

            // 🔹 MODAL DE ELIMINAR
            presupuestoEliminar?.let { presupuesto ->
                AlertDialog(
                    onDismissRequest = { presupuestoEliminar = null },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(24.dp), // Bordes redondeados premium
                    title = {
                        Text("Eliminar presupuesto", fontWeight = FontWeight.Bold, color = Color.Black)
                    },
                    text = {
                        Text("¿Estás seguro de eliminar este presupuesto? Esta acción no se puede deshacer.", color = Color.DarkGray)
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteBudget(presupuesto.id)
                                presupuestoExpandidoId = null
                                presupuestoEliminar = null
                                mensajeExito = "Presupuesto eliminado correctamente"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), // Rojo sólido
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { presupuestoEliminar = null }
                        ) {
                            Text("Cancelar", color = Color.Gray, fontWeight = FontWeight.Medium)
                        }
                    }
                )
            }

            // 🔹 MODAL DE ÉXITO
            mensajeExito?.let { mensaje ->
                AlertDialog(
                    onDismissRequest = { mensajeExito = null },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    title = {
                        Text("Éxito", fontWeight = FontWeight.Bold, color = greenColor)
                    },
                    text = {
                        Text(mensaje, color = Color.DarkGray)
                    },
                    confirmButton = {
                        Button(
                            onClick = { mensajeExito = null },
                            colors = ButtonDefaults.buttonColors(containerColor = greenColor), // Verde sólido
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetDialog(
    presupuesto: Presupuesto,
    categorias: List<Categoria>,
    onDismiss: () -> Unit,
    onGuardar: (String, Double) -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf(presupuesto.categoriaNombre) }
    var monto by remember { mutableStateOf(presupuesto.montoLimite.toString()) }
    var expandedCategoria by remember { mutableStateOf(false) }

    // 🔹 Envolvemos en MaterialTheme para matar el morado
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = greenColor,
            primaryContainer = greenColor.copy(alpha = 0.1f),
            onPrimaryContainer = greenColor
        )
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color.White, // Fondo blanco limpio
            shape = RoundedCornerShape(24.dp), // Bordes premium
            title = {
                Text("Editar presupuesto", fontWeight = FontWeight.Bold, color = Color.Black)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // 🔹 Menú Desplegable para seleccionar la categoría
                    ExposedDropdownMenuBox(
                        expanded = expandedCategoria,
                        onExpandedChange = { expandedCategoria = !expandedCategoria }
                    ) {
                        OutlinedTextField(
                            value = categoriaSeleccionada,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
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
                            categorias.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.nombre) },
                                    onClick = {
                                        categoriaSeleccionada = cat.nombre
                                        expandedCategoria = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = monto,
                        onValueChange = { monto = it },
                        label = { Text("Monto límite") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = greenColor,
                            focusedLabelColor = greenColor,
                            cursorColor = greenColor
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onGuardar(categoriaSeleccionada, monto.toDoubleOrNull() ?: 0.0) },
                    colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }
        )
    }
}

@Composable
fun BudgetItem(
    presupuesto: Presupuesto,
    sumGastos: Double,
    expanded: Boolean,
    onExpand: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val progress: Float = (sumGastos.toFloat() / presupuesto.montoLimite.toFloat())
        .coerceIn(0f, 1f)

    val offsetX by animateDpAsState(
        targetValue = if (expanded) (-100).dp else 0.dp,
        label = "budget_offset"
    )

    Box(modifier = Modifier.fillMaxWidth()) {
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

        ElevatedCard(
            shape = RoundedCornerShape(24.dp), // Bordes consistentes
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = offsetX)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onExpand() }
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(color = Color(0xFFE6F9F0), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getIcon(presupuesto.categoriaNombre),
                                contentDescription = null,
                                tint = greenColor // Verde consistente
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
                        text = "$${sumGastos.toInt()} / $${presupuesto.montoLimite.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (progress >= 1f) Color.Red else greenColor,
                    trackColor = Color(0xFFE5E7EB),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                )
            }
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