package com.example.saasfinanzas.features.goals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarms
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.saasfinanzas.R
import java.time.LocalDate

data class Meta(
    val id:Int,
    val descripcion: String,
    val imagen: Int,
    val fechaLimite: LocalDate,
    val objetivo: Float,
    val ahorrado: Float
)


@RequiresApi(Build.VERSION_CODES.O)
val metas = listOf(
    Meta(
        id=1,
        descripcion = "Viaje a la playa",
        imagen = R.drawable.playa,
        fechaLimite = LocalDate.of(2026, 12, 20),
        objetivo = 10000f,
        ahorrado = 2500f
    ),
    Meta(
        id=2,
        descripcion = "Comprar laptop",
        imagen = R.drawable.playa,
        fechaLimite = LocalDate.of(2026, 9, 10),
        objetivo = 15000f,
        ahorrado = 5000f
    ),
    Meta(
        id=3,
        descripcion = "Nuevo celular",
        imagen = R.drawable.playa,
        fechaLimite = LocalDate.of(2026, 8, 1),
        objetivo = 8000f,
        ahorrado = 1200f
    )
)



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(navHostController: NavHostController){
    Scaffold(
        topBar = { CenterAlignedTopAppBar(
            title = { Text("Metas de Ahorro") },
            actions = {
                IconButton(onClick = {
                    navHostController.navigate("añadir_metas")
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "añadir meta")
                }
            })
        }


    ) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){

            items(metas) { meta ->
                ItemGoal(meta,navHostController)
            }


        }
    }
}


@Composable
fun ItemGoal(meta: Meta, navHostController: NavHostController) {

    val progress = (meta.ahorrado / meta.objetivo)
        .coerceIn(0f, 1f)

    val porcentaje = (progress * 100).toInt()

    ElevatedCard(

        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable{
                navHostController.navigate("detail_goal/${meta.id}")

            }
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = meta.imagen),
                    contentDescription = meta.descripcion,
                    Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = meta.descripcion,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = "Objetivo: $${meta.objetivo}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "Ahorrado: $${meta.ahorrado}",
                        fontSize = 14.sp,
                        color = Color(0xFF22C55E)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFD1FAE5),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = meta.fechaLimite.toString(),
                        color = Color(0xFF065F46),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = Color(0xFF22C55E),
                    trackColor = Color(0xFFE5E7EB),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "$porcentaje%",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}