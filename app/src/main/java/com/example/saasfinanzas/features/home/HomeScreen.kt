package com.example.saasfinanzas.features.home

import RequestNotificationPermission
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.saasfinanzas.data.model.Meta
import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.features.auth.AuthViewModel
import com.example.saasfinanzas.features.goals.GoalViewModel
import com.example.saasfinanzas.features.transactions.TransactionViewModel
import java.util.Calendar
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navHostController: NavHostController) {

    RequestNotificationPermission()

    val viewModel: HomeViewModel = hiltViewModel()
    val viewModelAuth: AuthViewModel = hiltViewModel()
    val viewModelMeta: GoalViewModel = hiltViewModel()
    val viewModelTran: TransactionViewModel = hiltViewModel()

    val currentUser by viewModel.currentUser.collectAsState()
    val transacciones by viewModelTran.movimientos.collectAsState()
    val metas by viewModelMeta.metas.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.userData()
        viewModelTran.cargarMovimientos()
        viewModelMeta.cargarMetas()
        viewModelAuth.checkAndSaveFcmToken()
    }

    // VARIABLES PARA TOTALES HISTÓRICOS
    var ingresos = 0.0f
    var gastos = 0.0f

    // VARIABLES PARA COMPARACIÓN DEL MES
    val calendar = Calendar.getInstance()
    val anioActual = calendar.get(Calendar.YEAR)
    val mesActual = calendar.get(Calendar.MONTH)
    val mesPasado = if (mesActual == 0) 11 else mesActual - 1
    val anioPasado = if (mesActual == 0) anioActual - 1 else anioActual

    var balanceActual = 0.0f
    var balancePasado = 0.0f

    transacciones.forEach { mov ->
        val monto = mov.monto.toFloat()

        // 1. Sumar a totales históricos
        if (mov.tipo == "ingreso") ingresos += monto else gastos += monto

        // 2. Separar por meses para sacar el porcentaje real
        val calMov = Calendar.getInstance().apply { timeInMillis = mov.fecha }
        val mesMov = calMov.get(Calendar.MONTH)
        val anioMov = calMov.get(Calendar.YEAR)

        if (anioMov == anioActual && mesMov == mesActual) {
            if (mov.tipo == "ingreso") balanceActual += monto else balanceActual -= monto
        } else if (anioMov == anioPasado && mesMov == mesPasado) {
            if (mov.tipo == "ingreso") balancePasado += monto else balancePasado -= monto
        }
    }

    // MATEMÁTICA DEL PORCENTAJE
    val diferencia = balanceActual - balancePasado
    val porcentaje = when {
        balancePasado == 0f && balanceActual > 0f -> 100f
        balancePasado == 0f && balanceActual < 0f -> -100f
        balancePasado == 0f -> 0f
        else -> (diferencia / abs(balancePasado)) * 100f
    }

    val meta = metas.lastOrNull()
    val ultimos = transacciones.takeLast(3).reversed() // reversed para que el más nuevo salga primero

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Inicio", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3F4F6),
                    scrolledContainerColor = Color(0xFFF3F4F6)
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = paddingValues.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item { Header(currentUser?.nombre ?: "") }

            item { BalanceSection(ingresos, gastos, porcentaje) }

            item { IncomeExpense(ingresos, gastos) }

            meta?.let {
                item { GoalCard(it) }
            }

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
            Text("Hola, $nombre", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun BalanceSection(ingresos: Float, gastos: Float, porcentaje: Float) {
    val balanceTotal = ingresos - gastos

    // LÓGICA DE COLORES Y TEXTO PARA EL PORCENTAJE
    val porcentajeFormateado = "%.1f".format(abs(porcentaje))
    val textoPorcentaje = when {
        porcentaje > 0 -> "↑ $porcentajeFormateado% este mes"
        porcentaje < 0 -> "↓ $porcentajeFormateado% este mes"
        else -> "= Mismo balance que el mes pasado"
    }

    val colorTextoPorcentaje = if (porcentaje >= 0) Color(0xFF2D6A4F) else Color.Red
    val bgPorcentaje = if (porcentaje >= 0) Color(0xFFD8F3DC) else Color(0xFFFFEBEB)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Balance Total", color = Color.Gray)
        Text(
            "$${balanceTotal}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(50),
            color = bgPorcentaje
        ) {
            Text(
                text = textoPorcentaje,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                color = colorTextoPorcentaje,
                style = MaterialTheme.typography.labelMedium
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
            CardInfo("INGRESOS", "$${ingresos}", true, cardWidth, progressIngresos)
            CardInfo("GASTOS", "$${gastos}", false, cardWidth, progressGastos)
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            Text(amount, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progreso },
                color = if (isIncome) Color(0xFF22C55E) else Color.Red,
                trackColor = Color.LightGray.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun GoalCard(meta: Meta) {
    val progress = (meta.montoAhorrado.toFloat() / meta.montoObjetivo.toFloat())
        .coerceIn(0f, 1f)
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4332)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text("Meta de Ahorro", color = Color.White.copy(0.7f), style = MaterialTheme.typography.labelMedium)
            Text(
                meta.nombre,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                color = Color.White,
                trackColor = Color.White.copy(0.3f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "$${meta.montoAhorrado} / $${meta.montoObjetivo} acumulado",
                color = Color.White.copy(0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun RecentTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Movimientos recientes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun MovementItem(movimiento: Movimiento) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAF2EC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "", tint = Color(0xFF1B3D2F))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(movimiento.descripcion, fontWeight = FontWeight.Bold)
                Text(movimiento.categoriaNombre, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }


            Text(
                text = if (movimiento.tipo == "gasto") "-$${movimiento.monto}" else "+$${movimiento.monto}",
                color = if (movimiento.tipo == "gasto") Color.Red else Color.Blue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}