package com.example.saasfinanzas.features.goals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.time.LocalDate


// tabla aportes
//- id_aporte
//- id_usuario
//- id_meta
//- monto
//- fecha_aporte

data class Aporte(
    val monto: Float,
    val fecha_aporte: LocalDate
)

@RequiresApi(Build.VERSION_CODES.O)
val aportes=listOf(
   Aporte(
       monto=352f,
       fecha_aporte = LocalDate.of(2026, 3, 2),
   ),
    Aporte(
        monto=500f,
        fecha_aporte = LocalDate.of(2026, 4, 20),
    )


)
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailGoal(navHostController: NavHostController, metaId: Int?) {
    val meta = metas.find { it.id==metaId }
    val progreso = 0.75f
    val porcentaje = (progreso * 100).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Meta") },
                navigationIcon = {
                    IconButton(onClick = {navHostController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "volver")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item { Spacer(modifier = Modifier.height(20.dp)) }

            item {

                Text(
                    text = " "+ meta?.descripcion,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Meta: "+meta?.objetivo,
                    color = Color.Gray
                )
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }

            item {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(220.dp)
                ) {

                    CircularProgressIndicator(
                        progress = progreso,
                        strokeWidth = 20.dp,
                        color = Color(0xFF17D45C),
                        trackColor = Color(0xFFD1FAE5),
                        modifier = Modifier.fillMaxSize()
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "$porcentaje%",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
// la suma de los aportes van aqui
                        Text(
                            text = "$750",
                            color = Color.Gray
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }

            item {

                Text(
                    text = "Historial de Aportes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(aportes) { aporte ->
                AporteItem(aporte)
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }

            item {

                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF17D45C)
                    )
                ) {

                    Text(
                        text = "Agregar aporte",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

@Composable
fun AporteItem(
    aporte: Aporte
) {

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF4F6F8)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Aporte del ",
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = aporte.fecha_aporte.toString(),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Text(
                text = aporte.monto.toString(),
                color = Color(0xFF17D45C),
                fontWeight = FontWeight.Bold
            )
        }
    }
}


