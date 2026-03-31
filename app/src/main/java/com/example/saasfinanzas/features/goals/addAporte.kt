package com.example.saasfinanzas.features.goals

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.saasfinanzas.data.model.Aporte
import com.example.saasfinanzas.data.model.Meta
import com.example.saasfinanzas.features.components.PrimaryButton
import kotlin.text.toLong

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAporte(navHostController: NavHostController, metaId: String?) {
    val viewModel: GoalViewModel= hiltViewModel()
    val viewModelAporte: AporteViewModel= hiltViewModel()
    val metas by viewModel.metas.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarMetas()
    }
    val meta=metas.find { meta -> meta.id==metaId }
    var monto by remember { mutableStateOf("0") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Añadir aporte") },
                navigationIcon = {
                    IconButton(onClick = {navHostController.popBackStack() }) {
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
                    value = monto,
                    onValueChange = {
                        //expresion regular que so0lo acepta numero y punto decimal
                        if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            monto = it
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
                    monto = (monto.toInt() + 50).toString()
                }

                QuickAddButton("+100") {
                    monto = (monto.toInt() + 100).toString()
                }

                QuickAddButton("+200") {
                    monto = (monto.toInt() + 200).toString()
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
                            text = meta?.nombre ?: "",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton("Guardar Aporte"){
                if (monto.isBlank()) {
                    println("Faltan datos")
                    return@PrimaryButton
                }


                val aporte = Aporte(
                    id = "",
                    metaId=metaId.toString(),
                    monto =monto.toDouble() ,
                    fecha = System.currentTimeMillis()

                )

                viewModelAporte.addAporte(aporte)

                navHostController.popBackStack()
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