package com.gastario.app.features.home

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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gastario.app.data.model.Meta
import com.gastario.app.data.model.Movimiento
import com.gastario.app.features.auth.AuthViewModel
import com.gastario.app.features.goals.GoalViewModel
import com.gastario.app.features.transactions.TransactionViewModel
import java.util.Calendar
import kotlin.math.abs

// 🔹 Tu verde oscuro oficial
private val greenColor = Color(0xFF2E7D32)
private val redColor = Color(0xFFD32F2F)

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

    var gastosMesActual = 0.0f
    var gastosMesPasado = 0.0f

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
            if (mov.tipo == "ingreso") {
                balanceActual += monto
            } else {
                balanceActual -= monto
                gastosMesActual += monto
            }
        } else if (anioMov == anioPasado && mesMov == mesPasado) {
            if (mov.tipo == "ingreso") {
                balancePasado += monto
            } else {
                balancePasado -= monto
                gastosMesPasado += monto
            }
        }
    }

    // MATEMÁTICA DEL PORCENTAJE
    val porcentaje = when {
        gastosMesPasado == 0f && gastosMesActual > 0f -> 100f
        gastosMesPasado == 0f -> 0f
        else -> ((gastosMesActual - gastosMesPasado) / gastosMesPasado) * 100f
    }

    val meta = metas.lastOrNull()
    val ultimos = transacciones
        .filter { mov ->
            val calMov = Calendar.getInstance()
            calMov.timeInMillis = mov.fecha

            calMov.get(Calendar.MONTH) == mesActual &&
                    calMov.get(Calendar.YEAR) == anioActual
        }
        .takeLast(3)
        .reversed()

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = greenColor,
            primaryContainer = greenColor.copy(alpha = 0.1f),
            onPrimaryContainer = greenColor
        )
    ) {
        Scaffold(
            containerColor = Color(0xFFF3F4F6),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Inicio", fontWeight = FontWeight.Bold, color = Color.Black) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFF3F4F6),
                        scrolledContainerColor = Color(0xFFF3F4F6)
                    )
                )
            }
        ) { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                item { Header(currentUser?.nombre ?: "Usuario") }

                item { BalanceSection(ingresos, gastos, porcentaje) }

                item { IncomeExpense(ingresos, gastos) }

                meta?.let {
                    item { GoalCard(it) }
                }

                item { RecentTitle() }

                if (ultimos.isEmpty()) {
                    item {
                        Text(
                            text = "No hay movimientos recientes",
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                } else {
                    items(ultimos) { movimiento ->
                        MovementItem(movimiento)
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
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
            Text("Hola, $nombre 👋", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall, color = Color.Black)
            Text("Este es tu resumen financiero", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun BalanceSection(ingresos: Float, gastos: Float, porcentaje: Float) {
    val balanceTotal = ingresos - gastos

    // LÓGICA DE COLORES Y TEXTO PARA EL PORCENTAJE (Corregida)
    val porcentajeFormateado = "%.1f".format(abs(porcentaje))

    // Si gastaste más, es malo (rojo). Si gastaste menos, es bueno (verde).
    val textoPorcentaje = when {
        porcentaje > 0 -> "↑ Gastaste $porcentajeFormateado% más"
        porcentaje < 0 -> "↓ Gastaste $porcentajeFormateado% menos"
        else -> "= Mismos gastos que el mes pasado"
    }

    val colorTextoPorcentaje = if (porcentaje <= 0) greenColor else redColor
    val bgPorcentaje = if (porcentaje <= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEB)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Balance Total", color = Color.Gray, style = MaterialTheme.typography.titleMedium)
        Text(
            "$${String.format("%.2f", balanceTotal)}",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(50),
            color = bgPorcentaje
        ) {
            Text(
                text = textoPorcentaje,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                color = colorTextoPorcentaje,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun IncomeExpense(ingresos: Float, gastos: Float) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val cardWidth = (maxWidth - 16.dp) / 2
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            val total = ingresos + gastos
            val progressIngresos = if (total > 0) ingresos / total else 0f
            val progressGastos = if (total > 0) gastos / total else 0f

            CardInfo("INGRESOS", "$${String.format("%.2f", ingresos)}", true, cardWidth, progressIngresos)
            CardInfo("GASTOS", "$${String.format("%.2f", gastos)}", false, cardWidth, progressGastos)
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(if (isIncome) Color(0xFFE8F5E9) else Color(0xFFFFEBEB), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isIncome) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (isIncome) greenColor else redColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(amount, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color.Black)

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progreso },
                color = if (isIncome) greenColor else redColor,
                trackColor = Color(0xFFF3F4F6),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
            )
        }
    }
}

@Composable
fun GoalCard(meta: Meta) {
    val progress = if (meta.montoObjetivo > 0) {
        (meta.montoAhorrado.toFloat() / meta.montoObjetivo.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        colors = CardDefaults.cardColors(containerColor = greenColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEvents, contentDescription = "Meta", tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("META DE AHORRO ACTIVA", color = Color.White.copy(0.8f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                meta.nombre,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                color = Color.White,
                trackColor = Color.White.copy(0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "$${meta.montoAhorrado} acumulado",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "de $${meta.montoObjetivo}",
                    color = Color.White.copy(0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
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
        Text("Movimientos recientes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color.Black)
    }
}

@Composable
fun MovementItem(movimiento: Movimiento) {
    val isExpense = movimiento.tipo == "gasto"

    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isExpense) Color(0xFFFFEBEB) else Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isExpense) Icons.Default.ShoppingCart else Icons.Default.AttachMoney,
                    contentDescription = "",
                    tint = if (isExpense) redColor else greenColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(movimiento.descripcion, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(movimiento.categoriaNombre, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }

            Text(
                text = if (isExpense) "-$${movimiento.monto}" else "+$${movimiento.monto}",
                color = if (isExpense) redColor else greenColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}