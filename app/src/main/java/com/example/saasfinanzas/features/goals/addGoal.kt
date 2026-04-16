package com.example.saasfinanzas.features.goals

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.saasfinanzas.data.model.Meta
import com.example.saasfinanzas.data.model.Presupuesto
import com.example.saasfinanzas.features.components.Alert
import com.example.saasfinanzas.features.components.PrimaryButton
import com.example.saasfinanzas.features.transactions.TransactionViewModel
import com.example.saasfinanzas.ui.theme.greenPrimary
import com.example.saasfinanzas.ui.theme.white
import kotlin.String


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoal(navController: NavController) {

    var nombre by remember { mutableStateOf("") }
    var montoObjetivo by remember { mutableStateOf("") }
    var montoAhorrado by remember { mutableStateOf("") }
    var fechaLimite by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialogAlert by remember { mutableStateOf(false) }
    val viewModel: GoalViewModel = hiltViewModel()
    val metasState=viewModel.metas.collectAsState()
    val metas=metasState.value



    val calendar = java.util.Calendar.getInstance()
    val mesActual = calendar.get(java.util.Calendar.MONTH)
    val anioActual = calendar.get(java.util.Calendar.YEAR)

    val metasMesActual=metas.filter { meta ->
        calendar.timeInMillis = meta.creadoEn

        val mesMeta = calendar.get(java.util.Calendar.MONTH)
        val anioMeta = calendar.get(java.util.Calendar.YEAR)

        mesMeta==mesActual && anioMeta==anioActual



    }

    val metasMesActualCant:Int=metasMesActual.size





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

                Text(
                    "Agregar Meta",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        /* 🔹 NOMBRE */
        item {
            CardField(
                title = "NOMBRE"
            ) {
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
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            montoObjetivo = it
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
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            montoAhorrado = it
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
                FechaLimiteField(fechaLimite) {
                    fechaLimite = it
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        /* 🔹 IMAGEN */
        item {
            CardField(title = "IMAGEN") {
                ImagePicker { uri ->
                    imageUri = uri
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }

        /* 🔹 BOTÓN */
        item {
            PrimaryButton("Guardar Meta") {

                if (
                    nombre.isBlank() ||
                    montoObjetivo.isBlank() ||
                    fechaLimite.toLongOrNull() == null
                ) {
                    println("Faltan datos")
                    return@PrimaryButton
                }

                if(metasMesActualCant+1>2){
                    showDialogAlert=true

                    return@PrimaryButton

                }




                val meta = Meta(
                    id = "",
                    nombre = nombre,
                    montoObjetivo = montoObjetivo.toDoubleOrNull() ?: 0.0,
                    montoAhorrado = montoAhorrado.toDoubleOrNull() ?: 0.0,
                    fechaLimite = fechaLimite.toLongOrNull() ?: 0L,
                    imageUrl = "",
                    creadoEn = System.currentTimeMillis()
                )

                viewModel.addMeta(meta, imageUri)

                navController.popBackStack()
            }
            Alert(
                title = "Límite alcanzado 🔒",
                text = "Ya usaste tus 2 Metas del mes.\nDesbloquea Metas ilimitados con Premium 💎",
                showDialog = showDialogAlert,
                onDismiss = { showDialogAlert = false; navController.navigate("premium") }
            )
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
fun FechaLimiteField(
    fecha: String,
    onFechaChange: (String) -> Unit
) {

    OutlinedTextField(
        value = fecha,
        onValueChange = onFechaChange,
        label = { Text("Fecha límite") },
        placeholder = { Text("mm/dd/yyyy") },

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

        Button(onClick = { launcher.launch("image/*") }) {
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