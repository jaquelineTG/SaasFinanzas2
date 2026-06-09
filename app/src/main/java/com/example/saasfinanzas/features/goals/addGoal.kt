package com.example.saasfinanzas.features.goals

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.saasfinanzas.data.model.Meta
import com.example.saasfinanzas.features.components.Alert
import com.example.saasfinanzas.features.components.PrimaryButton
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import java.text.NumberFormat
fun formatMiles(input: String): String {

    val limpio = input.replace(",", "")

    if (limpio.isEmpty()) return ""

    return try {
        NumberFormat.getNumberInstance(Locale.US)
            .format(limpio.toLong())
    } catch (e: Exception) {
        input
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoal(navController: NavController) {

    var nombre by remember { mutableStateOf("") }
    var montoObjetivo by remember { mutableStateOf("") }
    var montoAhorrado by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var showDialogIncompleto by remember { mutableStateOf(false) }
    var showDialogAlert by remember { mutableStateOf(false) }

    val viewModel: GoalViewModel = hiltViewModel()
    val metasState = viewModel.metas.collectAsState()
    val metas = metasState.value

    val calendar = Calendar.getInstance()
    val mesActual = calendar.get(Calendar.MONTH)
    val anioActual = calendar.get(Calendar.YEAR)
    var fechaLimiteTexto by remember { mutableStateOf("") }
    var fechaLimiteMillis by remember { mutableStateOf<Long?>(null) }

    val metasMesActualCant = metas.filter { meta ->
        val cal = Calendar.getInstance().apply { timeInMillis = meta.creadoEn }
        cal.get(Calendar.MONTH) == mesActual && cal.get(Calendar.YEAR) == anioActual
    }.size

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
                    Icon(Icons.Default.ArrowBack, contentDescription = "")
                }
                Text("Agregar Meta", style = MaterialTheme.typography.titleMedium)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        /* 🔹 NOMBRE */
        item {
            CardField(title = "NOMBRE") {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholder = { Text("Ej. Viaje a la playa") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }

        /* 🔹 MONTO OBJETIVO */
        item {
            CardField(title = "MONTO OBJETIVO") {
                OutlinedTextField(
                    value = montoObjetivo,
                    onValueChange = { value ->

                        val numeros = value.replace(",", "")

                        if (numeros.all { it.isDigit() }) {
                            montoObjetivo = formatMiles(numeros)
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
        item { Spacer(modifier = Modifier.height(20.dp)) }

        /* 🔹 MONTO AHORRADO */
        item {
            CardField(title = "MONTO INICIAL") {
                OutlinedTextField(
                    value = montoAhorrado,
                    onValueChange = { value ->

                        val numeros = value.replace(",", "")

                        if (numeros.all { it.isDigit() }) {
                            montoAhorrado = formatMiles(numeros)
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
        item { Spacer(modifier = Modifier.height(20.dp)) }

        /* 🔹 FECHA */
        item {
            CardField(title = "FECHA LÍMITE") {
                FechaLimiteField(
                    fecha = fechaLimiteTexto,
                    onFechaSeleccionada = { texto, millis ->
                        fechaLimiteTexto = texto
                        fechaLimiteMillis = millis
                    }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }

        /* 🔹 IMAGEN */
        item {
            CardField(title = "IMAGEN") {
                ImagePicker { uri -> imageUri = uri }
            }
        }
        item { Spacer(modifier = Modifier.height(30.dp)) }

        /* 🔹 BOTÓN */
        item {
            PrimaryButton("Guardar Meta") {
                if (
                    nombre.isBlank() ||
                    montoObjetivo.isBlank() ||
                    fechaLimiteMillis == null
                ) {
                    showDialogIncompleto = true
                    return@PrimaryButton
                }

                if (metasMesActualCant >= 2) {
                    showDialogAlert = true
                    return@PrimaryButton
                }

                val meta = Meta(
                    id = "",
                    nombre = nombre,
                    montoObjetivo = montoObjetivo
                        .replace(",", "")
                        .toDoubleOrNull() ?: 0.0,

                    montoAhorrado = montoAhorrado
                        .replace(",", "")
                        .toDoubleOrNull() ?: 0.0,
                    fechaLimite = fechaLimiteMillis ?: 0L,
                    imageUrl = "",
                    creadoEn = System.currentTimeMillis()
                )

                // CERRAR PANTALLA SOLO CUANDO FIREBASE TERMINE
                viewModel.addMeta(meta, imageUri) {
                    navController.popBackStack()
                }
            }

            Alert(
                text = "Por favor, llena todos los campos obligatorios.",
                title = "Datos incompletos",
                showDialog = showDialogIncompleto,
                onDismiss = { showDialogIncompleto = false }
            )

            Alert(
                title = "Límite alcanzado 🔒",
                text = "Ya creaste tus 2 metas del mes en el plan gratuito.\n\nDesbloquea metas ilimitadas con Premium 💎",
                showDialog = showDialogAlert,
                onDismiss = {
                    showDialogAlert = false
                    navController.navigate("premium")
                }
            )
        }
        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaLimiteField(
    fecha: String,
    onFechaSeleccionada: (String, Long) -> Unit
) {

    var showPicker by remember {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState()

    OutlinedTextField(
        value = fecha,
        onValueChange = {},
        readOnly = true,
        label = { Text("Fecha límite") },

        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Calendario"
            )
        },

        shape = RoundedCornerShape(12.dp),

        modifier = Modifier
            .fillMaxWidth(),

        singleLine = true
    )

    LaunchedEffect(Unit) {
        // opcional
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .offset(y = (-56).dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = Color.Transparent,
            onClick = {
                showPicker = true
            }
        ) {}
    }

    if (showPicker) {

        DatePickerDialog(
            onDismissRequest = {
                showPicker = false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        datePickerState.selectedDateMillis?.let { millis ->
                            val formato = SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            )

                            onFechaSeleccionada(
                                formato.format(millis),
                                millis
                            )
                        }

                        showPicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showPicker = false
                    }
                ) {
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

//@Composable
//fun ImagePicker() {
//
//    var imageUri by remember { mutableStateOf<Uri?>(null) }
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri ->
//        imageUri = uri
//    }
//
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        Button(
//            colors = ButtonDefaults.buttonColors(
//                containerColor = greenPrimary,
//                contentColor = white
//            ),
//            onClick = { launcher.launch("image/*") },
//
//        ) {
//            Text("Seleccionar Imagen")
//        }
//
//        imageUri?.let {
//            Image(
//                painter = rememberAsyncImagePainter(it),
//                contentDescription = null,
//                modifier = Modifier.size(150.dp)
//            )
//        }
//    }
//}

@Composable
fun ImagePicker(onImageSelected: (Uri?) -> Unit) {

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
        onImageSelected(uri)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Button(
            onClick = {
                launcher.launch("image/*")
            },

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF22C55E),
                contentColor = Color.White
            ),

            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Seleccionar Imagen")
        }

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )
        }
    }
}

@Composable
fun CardField(
    title: String,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                title,
                color = Color.Gray,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            content()
        }
    }
}