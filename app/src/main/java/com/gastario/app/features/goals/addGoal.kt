package com.gastario.app.features.goals

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
import com.gastario.app.data.model.Meta
import com.gastario.app.features.components.Alert
import com.gastario.app.features.components.PrimaryButton
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// 🔹 Tu verde oscuro oficial (privado para evitar conflictos)
private val greenColor = Color(0xFF2E7D32)

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

    var isSaving by remember { mutableStateOf(false) }
    val metasMesActualCant = metas.filter { meta ->
        val cal = Calendar.getInstance().apply { timeInMillis = meta.creadoEn }
        cal.get(Calendar.MONTH) == mesActual && cal.get(Calendar.YEAR) == anioActual
    }.size

    // 🔹 Envolvemos todo en el MaterialTheme para matar el morado de los inputs
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
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = greenColor,
                            focusedLabelColor = greenColor,
                            cursorColor = greenColor
                        )
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

            /* 🔹 IMAGEN COMPRIMIDA */
            item {
                CardField(title = "IMAGEN") {
                    ImagePicker { uri -> imageUri = uri }
                }
            }
            item { Spacer(modifier = Modifier.height(30.dp)) }

            /* 🔹 BOTÓN */
            /* 🔹 BOTÓN */
            item {
                PrimaryButton(
                    text = "Guardar Meta",
                    isLoading = isSaving
                ) {
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

                    // Prende el círculo de carga
                    isSaving = true

                    val meta = Meta(
                        id = "",
                        nombre = nombre,
                        montoObjetivo = montoObjetivo.replace(",", "").toDoubleOrNull() ?: 0.0,
                        montoAhorrado = montoAhorrado.replace(",", "").toDoubleOrNull() ?: 0.0,
                        fechaLimite = fechaLimiteMillis ?: 0L,
                        imageUrl = "",
                        creadoEn = System.currentTimeMillis()
                    )

                    viewModel.addMeta(meta, imageUri) {
                        isSaving = false
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaLimiteField(
    fecha: String,
    onFechaSeleccionada: (String, Long) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
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
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = greenColor,
            focusedLabelColor = greenColor,
            cursorColor = greenColor,
            focusedTrailingIconColor = greenColor
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .offset(y = (-56).dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
            onClick = { showPicker = true }
        ) {}
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            colors = DatePickerDefaults.colors(containerColor = Color.White), // Fondo del diálogo
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
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = greenColor)
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPicker = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
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

@Composable
fun ImagePicker(onImageSelected: (Uri?) -> Unit) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isCompressing by remember { mutableStateOf(false) }

    // 🔹 Este context es el que usa el Toast para saber dónde mostrarse
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            isCompressing = true
            coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                // Intentamos comprimir
                val compressedUri = compressImage(context, uri)

                withContext(kotlinx.coroutines.Dispatchers.Main) {
                    if (compressedUri != null) {
                        // Éxito: Guardamos la imagen ligera
                        imageUri = compressedUri
                        onImageSelected(compressedUri)
                    } else {

                        android.widget.Toast.makeText(
                            context,
                            "La imagen no es válida o es muy pesada. Por favor, elige otra.",
                            android.widget.Toast.LENGTH_LONG
                        ).show()

                        // Limpiamos la selección para no guardar basura
                        imageUri = null
                        onImageSelected(null)
                    }
                    isCompressing = false
                }
            }
        } else {
            imageUri = null
            onImageSelected(null)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { launcher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32), // greenColor
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            enabled = !isCompressing
        ) {
            if (isCompressing) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Procesando...")
            } else {
                Text("Seleccionar Imagen")
            }
        }

        imageUri?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = coil.compose.rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.LightGray, RoundedCornerShape(12.dp))
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

// =========================================================
// 🔹 FUNCIÓN AUXILIAR PARA COMPRIMIR IMÁGENES
// =========================================================
// =========================================================
// 🔹 FUNCIÓN AUXILIAR PARA COMPRIMIR IMÁGENES (ESTRICTA)
// =========================================================
fun compressImage(context: android.content.Context, uri: android.net.Uri): android.net.Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // 🔹 Si no se pudo leer la imagen (formato raro o corrupta), DEVOLVEMOS NULL
        if (originalBitmap == null) return null

        // 1. Redimensionar (Max 1024x1024)
        val maxWidth = 1024
        val maxHeight = 1024
        var width = originalBitmap.width
        var height = originalBitmap.height

        if (width > maxWidth || height > maxHeight) {
            val ratioBitmap = width.toFloat() / height.toFloat()
            if (ratioBitmap > 1) {
                width = maxWidth
                height = (width / ratioBitmap).toInt()
            } else {
                height = maxHeight
                width = (height * ratioBitmap).toInt()
            }
        }

        val resizedBitmap = android.graphics.Bitmap.createScaledBitmap(originalBitmap, width, height, true)

        // 2. Guardar comprimido (JPEG al 70%)
        val file = java.io.File(context.cacheDir, "meta_comprimida_${System.currentTimeMillis()}.jpg")
        val outputStream = java.io.FileOutputStream(file)
        resizedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream)
        outputStream.flush()
        outputStream.close()

        // Devolvemos el Uri del archivo ligero
        android.net.Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null // 🔹 Si hay cualquier error de memoria o lectura, DEVOLVEMOS NULL para no subir la original
    }
}