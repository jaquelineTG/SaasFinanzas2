package com.example.saasfinanzas.features.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.Dp

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.saasfinanzas.features.goals.Meta
import com.example.saasfinanzas.features.goals.metas
import com.example.saasfinanzas.features.transactions.Transacciones
import com.example.saasfinanzas.features.transactions.transacciones



data class usuario (
    val nombre: String,
    val apellido:String
);

val usuarios=listOf(
    usuario("Jaqueline","Torres"),
    usuario("Evelyn","Gutierrez")
)


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navHostController: NavHostController) {
    val viewModel: HomeViewModel = viewModel()
    val nombre by viewModel.nombre.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }
    var ingresos: Float=0.0f;
    var gastos: Float=0.0f;
    transacciones.forEach {
        if (it.tipo=="ingreso") ingresos+= it.monto else gastos+=it.monto

    }

    var meta=metas.last()
    val ultimos = transacciones.takeLast(3)

    Scaffold(




    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item { Header(nombre) }

            item { BalanceSection(ingresos,gastos) }

            item { IncomeExpense(ingresos,gastos) }

            item { GoalCard(meta) }

            item { RecentTitle() }

            items(ultimos) { movimiento ->
                MovementItem(movimiento)
            }
        }
    }
}

@Composable
fun Header(nombre: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text("Buenos días,", color = Color.Gray)
            Text("Hola, $nombre", fontWeight = FontWeight.Bold)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BalanceSection(ingresos: Float, gastos: Float) {
    // "+4.2% este mes",
    //porcentaje = ((balanceActual - balanceMesAnterior) / balanceMesAnterior) * 100

    val balanceTotal=ingresos-gastos;
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()) {

        Text("Balance Total", color = Color.Gray)
        Text(
            "$${balanceTotal}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(50),
            color = Color(0xFFD8F3DC)
        ) {
            Text(
                "+4.2% este mes",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                color = Color(0xFF2D6A4F)
            )
        }
    }
}

@Composable
fun IncomeExpense(ingresos: Float, gastos: Float) {

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {

        val cardWidth = (maxWidth - 12.dp) / 2

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val total = ingresos + gastos
            val progressIngresos = if (total > 0) ingresos / total else 0f
            val progressGastos = if (total > 0) gastos / total else 0f
            CardInfo("INGRESOS", "$${ingresos}", true, cardWidth,progressIngresos)
            CardInfo("GASTOS", "$${gastos}", false, cardWidth,progressGastos)
        }
    }
}

@Composable
fun CardInfo(
    title: String,
    amount: String,
    isIncome: Boolean,
    width: Dp,
    progreso: Float
) {


    ElevatedCard(
        modifier = Modifier.width(width),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(title, color = Color.Gray)
            Text(amount, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progreso,
                color = if (isIncome) Color.Green else Color.Red,
                trackColor = Color.LightGray
            )
        }
    }
}

@Composable
fun GoalCard(meta: Meta) {
    val progress = (meta.ahorrado / meta.objetivo)
        .coerceIn(0f, 1f)
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4332)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text("Meta de Ahorro", color = Color.White.copy(0.7f))
            Text(
                meta.descripcion,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = progress,
                color = Color.White,
                trackColor = Color.White.copy(0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "$${meta.objetivo} / $${meta.ahorrado} acumulado",
                color = Color.White.copy(0.7f)
            )
        }
    }
}

@Composable
fun RecentTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Movimientos recientes", fontWeight = FontWeight.Bold)

    }
}

@Composable
fun MovementItem(movimiento: Transacciones) {
    ElevatedCard(shape = RoundedCornerShape(20.dp)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(movimiento.descripcion, fontWeight = FontWeight.Bold)
                Text(movimiento.categoriaNombre, color = Color.Gray)
            }

            Text("$${movimiento.monto}", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}

