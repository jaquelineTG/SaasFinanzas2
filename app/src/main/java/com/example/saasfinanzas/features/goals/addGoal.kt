package com.example.saasfinanzas.features.goals

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.saasfinanzas.data.model.Meta
import com.example.saasfinanzas.data.model.Presupuesto
import com.example.saasfinanzas.features.components.PrimaryButton
import com.example.saasfinanzas.features.transactions.TransactionViewModel
import com.example.saasfinanzas.ui.theme.greenPrimary
import com.example.saasfinanzas.ui.theme.white
import kotlin.String


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoal(navHostController: NavController){

    var nombre by remember { mutableStateOf("") }
    var montoObjetivo by remember { mutableStateOf("") }
    var montoAhorrado by remember { mutableStateOf("") }
    var fechaLimite by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val viewModel: GoalViewModel = hiltViewModel()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Añadir Meta") },
                navigationIcon  = {
                    IconButton(onClick = {
                        navHostController.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "regresar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        )
        {
            item {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre de la meta") },
                    placeholder = { Text("e.j. Viaje a la playa") },
                    shape = RoundedCornerShape(16.dp)
                )
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item {
                OutlinedTextField(
                    value = montoObjetivo,
                    onValueChange = {
                        //expresion regular que so0lo acepta numero y punto decimal
                        if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            montoObjetivo = it
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Monto Objetivo") },
                    placeholder = { Text("$ 0.00") },
                    shape = RoundedCornerShape(16.dp),
                    //Esto le dice al sistema qué teclado mostrar.
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item {
                OutlinedTextField(
                    value = montoAhorrado,
                    onValueChange = {
                        //expresion regular que so0lo acepta numero y punto decimal
                        if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            montoAhorrado = it
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Monto Inicial(Opcional)") },
                    placeholder = { Text("$ 0.00") },
                    shape = RoundedCornerShape(16.dp),
                    //Esto le dice al sistema qué teclado mostrar.
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }

            item {
                FechaLimiteField(fechaLimite, { fechaLimite = it })
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }

            item {
                ImagePicker { uri ->
                    imageUri = uri
                }

            }
            item { Spacer(modifier = Modifier.height(20.dp)) }

            item {
                PrimaryButton("Guardar Meta"){
                    if (nombre.isBlank() || montoObjetivo.isBlank() || montoAhorrado.isBlank() || fechaLimite.toLong() == 0L ) {
                        println("Faltan datos")
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

                    viewModel.addMeta(meta,imageUri)

                    navHostController.popBackStack()
                }

            }


        }

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