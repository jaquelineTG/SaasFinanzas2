package com.example.saasfinanzas.features.budget


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults


import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.saasfinanzas.data.model.Categoria
import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.data.model.Presupuesto
import com.example.saasfinanzas.features.components.Alert
import com.example.saasfinanzas.features.components.PrimaryButton
import com.example.saasfinanzas.features.transactions.TransactionViewModel
import com.example.saasfinanzas.ui.theme.SaasFinanzasTheme

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

import java.util.Calendar



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudget(navController: NavController) {


    val categoriasFree = listOf(
        Categoria("1", "Comida"),
        Categoria("2", "Transporte"),
        Categoria("3", "Salud"),
        Categoria("4", "Entretenimiento")
    )
    val viewModel: BudgetViewModel = hiltViewModel()

    var expanded by remember { mutableStateOf(false) }

    var categoriaId by remember { mutableStateOf("") }
    var categoriaNombre by remember { mutableStateOf("") }


    var montoLimite by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()

    val meses = listOf(
        "Enero","Febrero","Marzo","Abril","Mayo","Junio",
        "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
    )

    val anios = (2020..2030).map { it.toString() }

    var mesSeleccionado by remember { mutableStateOf(meses[calendar.get(Calendar.MONTH)]) }
    var anioSeleccionado by remember { mutableStateOf(calendar.get(Calendar.YEAR).toString()) }
    val mesNumero = meses.indexOf(mesSeleccionado) + 1
    val anioNumero = anioSeleccionado.toInt()
    var expandedMes by remember { mutableStateOf(false) }
    var expandedAnio by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .padding(24.dp)
    ) {

        /* HEADER */

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
                "Agregar Presupuesto",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        /* CATEGORIA */

        ElevatedCard(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(Modifier.padding(16.dp)) {

                Text(
                    "CATEGORÍA",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {

                    OutlinedTextField(
                        value = categoriaNombre,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Selecciona una categoría") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {

                        categoriasFree.forEach {
                            DropdownMenuItem(
                                text = { Text(it.nombre) },
                                onClick = {
                                    categoriaId = it.id
                                    categoriaNombre = it.nombre
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        /* MONTO LIMITE */

        ElevatedCard(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(Modifier.padding(16.dp)) {

                Text(
                    "MONTO LÍMITE",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = montoLimite,
                    onValueChange = { montoLimite = it },
                    leadingIcon = { Text("$") },
                    placeholder = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        /* MES Y AÑO */

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.weight(1f)
            ) {

                Column(Modifier.padding(16.dp)) {

                    Text("MES", color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedMes,
                        onExpandedChange = { expandedMes = !expandedMes }
                    ) {

                        OutlinedTextField(
                            value = mesSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expandedMes)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expandedMes,
                            onDismissRequest = { expandedMes = false }
                        ) {
                            meses.forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        mesSeleccionado = it
                                        expandedMes = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.weight(1f)
            ) {

                Column(Modifier.padding(16.dp)) {

                    Text("AÑO", color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedAnio,
                        onExpandedChange = { expandedAnio = !expandedAnio }
                    ) {

                        OutlinedTextField(
                            value = anioSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expandedAnio)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expandedAnio,
                            onDismissRequest = { expandedAnio = false }
                        ) {
                            anios.forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        anioSeleccionado = it
                                        expandedAnio = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        /* BOTON */

        PrimaryButton("Añadir Presupuesto") {

            val montoDouble = montoLimite.toDoubleOrNull()

            // Validaciones
            if (
                categoriaId.isBlank() ||
                categoriaNombre.isBlank() ||
                montoDouble == null || montoDouble <= 0.0 ||
                mesNumero <= 0 ||
                anioNumero <= 0
            ) {
                println("Faltan datos o son inválidos")

                return@PrimaryButton
            }

            val presupuesto = Presupuesto(
                id = "",
                categoriaId = categoriaId,
                categoriaNombre = categoriaNombre,
                montoLimite = montoDouble,
                mes = mesNumero,
                anio = anioNumero,
                creadoEn = System.currentTimeMillis()
            )

            viewModel.addBudget(presupuesto)

            navController.popBackStack()
        }

        Alert(
            text = "Agrega todos los datos del formulario",
            title = "Datos incompletos",
            showDialog = showDialog,
            onDismiss = { showDialog = false }
        )
    }
}







//@Preview(showBackground = true)
//@Composable
//fun RegisterScreenPreview() {
//    SaasFinanzasTheme {
//
//            AddBudget()
//
//    }
//}