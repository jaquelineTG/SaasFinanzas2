package com.example.saasfinanzas.features.transactions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.saasfinanzas.features.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

//data class Transacciones(
//    val categoriaNombre: String,
//    val monto: Float,
//    val descripcion: String,
//    val tipo: String,
//    val fecha: LocalDate
//)

@RequiresApi(Build.VERSION_CODES.O)
//val transacciones = listOf(
//    Transacciones("Compras", 100f, "Almuerzo en restaurante", "gasto", LocalDate.now()),
//    Transacciones("Transporte", 200f, "Taxi", "gasto", LocalDate.now().minusDays(1)),
//    Transacciones("Salario", 5000f, "Pago mensual", "ingreso", LocalDate.parse("2026-04-22"))
//)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(navHostController: NavController) {

    var tipoSeleccionado by remember { mutableStateOf("todas") }
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }
    val viewModel: TransactionViewModel = hiltViewModel()
    val authViewModel: AuthViewModel=hiltViewModel()
    val movimientos by viewModel.movimientos.collectAsState()

    LaunchedEffect(Unit) {
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
                title = { Text("Movimientos") },
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

                transaccionItem(
                    categoriaNombre = transaccion.categoriaNombre,
                    monto = transaccion.monto,
                    descripcion = transaccion.descripcion,
                    tipo=transaccion.tipo
                )
            }
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

@Composable
fun transaccionItem(
    categoriaNombre: String,
    monto: Double,
    descripcion: String,
    tipo:String
) {


    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFFFDFDFD)
        ),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                    tint = Color(0xFF22C55E)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = categoriaNombre,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Text(
                text = "$${"%.2f".format(if (tipo == "gasto") -monto else monto)}",
                style = MaterialTheme.typography.titleMedium,
                color = if (tipo == "gasto") Color.Red else Color(0xFF22C55E)
            )
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

        FiltroChip(
            "Todas",
            selected = tipoSeleccionado == "todas"
        ) { onTipoChange("todas") }

        Spacer(modifier = Modifier.width(8.dp))

        FiltroChip(
            "Ingresos",
            selected = tipoSeleccionado == "ingreso"
        ) { onTipoChange("ingreso") }

        Spacer(modifier = Modifier.width(8.dp))

        FiltroChip(
            "Gastos",
            selected = tipoSeleccionado == "gasto"
        ) { onTipoChange("gasto") }
    }
}

@Composable
fun FiltroChip(
    texto: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val background = if (selected) Color(0xFFD1FAE5) else Color(0xFFF1F1F1)
    val textColor = if (selected) Color(0xFF059669) else Color.Gray

    Box(
        modifier = Modifier
            .background(background, CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {

        Text(
            text = texto,
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )
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
                .background(Color(0xFFF1F1F1), CircleShape)
                .clickable { showPicker = true }
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {

            Text(
                text = "Fecha",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

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
                        }
                    ) {
                        Text("Aceptar")
                    }

                },

                dismissButton = {
                    TextButton(onClick = { showPicker = false }) {
                        Text("Cancelar")
                    }
                }

            ) {

                DatePicker(
                    state = datePickerState
                )
            }
        }
    }
}