package com.example.saasfinanzas.features.budget


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.data.model.Presupuesto
import com.example.saasfinanzas.features.transactions.TransactionViewModel

//data class Presupuesto(
//    val categoriaNombre: String,
//    val montoLimite: Float,
//    val gastoActual: Float
//)
//
//val presupuestos = listOf(
//    Presupuesto("Comida", 500f, 340f),
//    Presupuesto("Transporte", 300f, 135f),
//    Presupuesto("Entretenimiento", 200f, 45f),
//    Presupuesto("Compras", 100f, 82f)
//)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(navController: NavController) {
    val viewModel: BudgetViewModel = hiltViewModel()
    val presupuestos = viewModel.presupuestos.collectAsState()

    val viewmodelMov: TransactionViewModel = hiltViewModel()
    val movimientosSate = viewmodelMov.movimientos.collectAsState()
    val movimientos = movimientosSate.value
    var presupuestoEditar by remember {
        mutableStateOf<Presupuesto?>(null)
    }
    var presupuestoEliminar by remember {
        mutableStateOf<Presupuesto?>(null)
    }

    var mensajeExito by remember {
        mutableStateOf<String?>(null)
    }

    var presupuestoExpandidoId by remember {
        mutableStateOf<String?>(null)
    }
    // 1. Escuchamos los cambios en la navegación
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // 2. Le pasamos navBackStackEntry al LaunchedEffect en lugar de Unit
    LaunchedEffect(navBackStackEntry) {
        viewModel.getBudgets()
        viewmodelMov.cargarMovimientos()
    }



    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "Presupuestos",
                                fontWeight = FontWeight.SemiBold
                            )
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


                        var sumGastos=0.0
                        movimientos.forEach {
                            if(it.categoriaId==presupuesto.categoriaId){
                              sumGastos=sumGastos+it.monto;
                        }

                    }
                    BudgetItem(
                        presupuesto = presupuesto,
                        sumGastos = sumGastos,

                        expanded = presupuestoExpandidoId == presupuesto.id,

                        onExpand = {
                            presupuestoExpandidoId =
                                if (presupuestoExpandidoId == presupuesto.id)
                                    null
                                else
                                    presupuesto.id
                        },

                        onDelete = {
                            presupuestoEliminar = presupuesto
                        },

                        onEdit = {
                            presupuestoEditar = presupuesto
                        }
                    )
                }
            }

            presupuestoEditar?.let { presupuesto ->

                EditBudgetDialog(
                    presupuesto = presupuesto,

                    onDismiss = {
                        presupuestoEditar = null
                    },



                    onGuardar = { categoria, monto ->

                        viewModel.updateBudget(
                            presupuesto.copy(
                                categoriaNombre = categoria,
                                montoLimite = monto
                            )
                        )

                        presupuestoExpandidoId = null
                        presupuestoEditar = null

                        mensajeExito =
                            "Presupuesto actualizado correctamente"
                    }
                )
            }

            presupuestoEliminar?.let { presupuesto ->

                AlertDialog(
                    onDismissRequest = {
                        presupuestoEliminar = null
                    },

                    containerColor = Color(0xFFE8F5E9),
                    titleContentColor = Color(0xFF1B5E20),
                    textContentColor = Color(0xFF2E7D32),

                    title = {
                        Text("Eliminar presupuesto")
                    },

                    text = {
                        Text("¿Estás seguro de eliminar este presupuesto?")
                    },

                    confirmButton = {

                        TextButton(
                            onClick = {

                                viewModel.deleteBudget(
                                    presupuesto.id
                                )

                                presupuestoExpandidoId = null
                                presupuestoEliminar = null

                                mensajeExito =
                                    "Presupuesto eliminado correctamente"

                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFF2E7D32)
                            )
                        ) {
                            Text("Eliminar")
                        }
                    },

                    dismissButton = {

                        TextButton(
                            onClick = {
                                presupuestoEliminar = null
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Gray
                            )
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
            mensajeExito?.let { mensaje ->

                AlertDialog(
                    onDismissRequest = {
                        mensajeExito = null
                    },

                    containerColor = Color(0xFFE8F5E9),
                    titleContentColor = Color(0xFF1B5E20),
                    textContentColor = Color(0xFF2E7D32),

                    title = {
                        Text("Éxito")
                    },

                    text = {
                        Text(mensaje)
                    },

                    confirmButton = {

                        TextButton(
                            onClick = {
                                mensajeExito = null
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFF2E7D32)
                            )
                        ) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EditBudgetDialog(
    presupuesto: Presupuesto,
    onDismiss: () -> Unit,
    onGuardar: (
        String,
        Double
    ) -> Unit
) {

    var categoria by remember {
        mutableStateOf(
            presupuesto.categoriaNombre
        )
    }

    var monto by remember {
        mutableStateOf(
            presupuesto.montoLimite.toString()
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFE8F5E9),
        titleContentColor = Color(0xFF1B5E20),
        textContentColor = Color(0xFF2E7D32),

        title = {
            Text("Editar presupuesto")
        },

        text = {

            Column {

                OutlinedTextField(
                    value = categoria,
                    onValueChange = {
                        categoria = it
                    },
                    label = {
                        Text("Categoría")
                    }
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = monto,
                    onValueChange = {
                        monto = it
                    },
                    label = {
                        Text("Monto límite")
                    }
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    onGuardar(
                        categoria,
                        monto.toDoubleOrNull() ?: 0.0
                    )
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF2E7D32)
                )
            ) {
                Text("Guardar")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun BudgetItem( presupuesto: Presupuesto,
                sumGastos: Double,
                expanded: Boolean,
                onExpand: () -> Unit,
                onDelete: () -> Unit,
                onEdit: () -> Unit) {


    val progress: Float = (sumGastos.toFloat() / presupuesto.montoLimite.toFloat())
        .coerceIn(0f, 1f)



    val offsetX by animateDpAsState(
        targetValue = if (expanded) (-100).dp else 0.dp,
        label = "budget_offset"
    )


    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onEdit
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color(0xFF2196F3)
                )
            }

            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
            }
        }

        ElevatedCard(
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 2.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = offsetX)
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null
                ) {
                    onExpand()
                }
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

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
                                imageVector = getIcon(
                                    presupuesto.categoriaNombre
                                ),
                                contentDescription = null,
                                tint = Color(0xFF22C55E)
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

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

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF22C55E),
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



