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
import androidx.navigation.NavController
import com.example.saasfinanzas.ui.theme.SaasFinanzasTheme

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

import java.util.Calendar



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudget(navHostController: NavController){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(24.dp)

    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically




        ){
            IconButton(onClick = {}) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)


            }

            Text("Agregar Presupuesto" , modifier = Modifier.fillMaxWidth(),  textAlign = TextAlign.Center)

        }
        Spacer(modifier = Modifier.height(20.dp))

        var expanded by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf("") }

        val opciones = listOf("Comida", "Transporte", "Renta")
        ElevatedCard(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text("Categoria")
                IconButton(onClick = {}) {
                    Icon(Icons.Default.AddCircle, contentDescription = null)
                }

            }
            Spacer(modifier = Modifier.height(16.dp))

            // Select
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {

                OutlinedTextField(
                    value = selectedOption,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = {
                        Text("Selecciona una categoría")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)

                    ,

                    shape = RoundedCornerShape(16.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    opciones.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                selectedOption = opcion
                                expanded = false
                            }
                        )
                    }
                }
            }

        }

        Spacer(modifier = Modifier.height(30.dp))
        ElevatedCard(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text("Monto Limite")


            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.padding(horizontal = 15.dp) .background(MaterialTheme.colorScheme.surfaceVariant).fillMaxWidth()){
                Text("$$" , modifier = Modifier.padding(horizontal = 20.dp))
            }



        }

        Spacer(modifier = Modifier.height(30.dp))
        // Estados

        val anios = (2020..2030).map { it.toString() }



        val calendar = Calendar.getInstance()

        val mesActualIndex = calendar.get(Calendar.MONTH)
        val anioActual = calendar.get(Calendar.YEAR)

        val meses = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )

        var mesSeleccionado by remember {
            mutableStateOf(meses[mesActualIndex])
        }

        var anioSeleccionado by remember {
            mutableStateOf(anioActual.toString())
        }

        var expandedMes by remember { mutableStateOf(false) }
        var expandedAnio by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🔹 CARD MES
            ElevatedCard(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "MES",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expandedMes,
                            onDismissRequest = { expandedMes = false }
                        ) {
                            meses.forEach { mes ->
                                DropdownMenuItem(
                                    text = { Text(mes) },
                                    onClick = {
                                        mesSeleccionado = mes
                                        expandedMes = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 🔹 CARD AÑO
            ElevatedCard(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "AÑO",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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
                            shape = RoundedCornerShape(16.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expandedAnio,
                            onDismissRequest = { expandedAnio = false }
                        ) {
                            anios.forEach { anio ->
                                DropdownMenuItem(
                                    text = { Text(anio) },
                                    onClick = {
                                        anioSeleccionado = anio
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
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar Presupuesto")
        }



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