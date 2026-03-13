package com.example.saasfinanzas.features.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAporte() {

    var aporte by remember { mutableStateOf("0") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Añadir aporte") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MONTO A APORTAR",
                color = Color.Gray,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = aporte,
                    onValueChange = {
                        //expresion regular que so0lo acepta numero y punto decimal
                        if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            aporte = it
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Monto a Aportar") },
                    placeholder = { Text("$ 0.00") },
                    shape = RoundedCornerShape(16.dp),
                    //Esto le dice al sistema qué teclado mostrar.
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                QuickAddButton("+50") {
                    aporte = (aporte.toInt() + 50).toString()
                }

                QuickAddButton("+100") {
                    aporte = (aporte.toInt() + 100).toString()
                }

                QuickAddButton("+200") {
                    aporte = (aporte.toInt() + 200).toString()
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF2F4F7)
                )
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFFD1FAE5),
                                RoundedCornerShape(10.dp)
                            )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {

                        Text(
                            text = "Este aporte se sumará a tu meta:",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
//poner el decripcion real
                        Text(
                            text = "Viaje a la Playa",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF16C46C)
                )
            ) {
                Text(
                    text = "Confirmar aporte",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuickAddButton(text: String, onClick: () -> Unit) {

    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold
        )
    }
}