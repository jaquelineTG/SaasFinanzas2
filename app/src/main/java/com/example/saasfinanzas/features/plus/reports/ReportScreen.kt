package com.example.saasfinanzas.features.plus.reports

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.saasfinanzas.data.model.Categoria
import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.data.model.Presupuesto
import com.example.saasfinanzas.features.budget.BudgetViewModel
import com.example.saasfinanzas.features.transactions.TransactionViewModel
import com.example.saasfinanzas.features.transactions.categoriasFree
import java.util.Calendar
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navHostController: NavHostController) {

    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Mensual") }
    var verTodos by remember { mutableStateOf(false) }

    val viewModel: TransactionViewModel = hiltViewModel()
    val movimientos by viewModel.movimientos.collectAsState()

    val viewModelPresupuesto: BudgetViewModel = hiltViewModel()
    val presupuestosState = viewModelPresupuesto.presupuestos.collectAsState()
    val presupuestos = presupuestosState.value

    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        viewModel.cargarMovimientos()
        viewModelPresupuesto.getBudgets()
    }

    // VARIABLES DE TIEMPO
    val calendar = Calendar.getInstance()
    val anioActual = calendar.get(Calendar.YEAR)
    val mesActual = calendar.get(Calendar.MONTH)
    val semanaActual = calendar.get(Calendar.WEEK_OF_YEAR)

    // 1. FILTRAR GASTOS DEL PERIODO ACTUAL
    val gastos = movimientos.filter { mov ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = mov.fecha
        val anioMov = cal.get(Calendar.YEAR)
        val mesMov = cal.get(Calendar.MONTH)
        val semanaMov = cal.get(Calendar.WEEK_OF_YEAR)

        if (mov.tipo != "gasto") return@filter false

        when (selectedTab) {
            "Semanal" -> anioMov == anioActual && semanaMov == semanaActual
            "Mensual" -> anioMov == anioActual && mesMov == mesActual
            "Anual" -> anioMov == anioActual
            else -> false
        }
    }

    // 2. CÁLCULO DE TOTALES
    val gastosTotal = gastos.sumOf { it.monto }

    // 3. CALCULAR GASTOS DEL PERIODO ANTERIOR (Para la comparación real)
    val gastosPasados = movimientos.filter { mov ->
        if (mov.tipo != "gasto") return@filter false
        val cal = Calendar.getInstance().apply { timeInMillis = mov.fecha }
        val anioMov = cal.get(Calendar.YEAR)
        val mesMov = cal.get(Calendar.MONTH)
        val semanaMov = cal.get(Calendar.WEEK_OF_YEAR)

        when (selectedTab) {
            "Semanal" -> {
                val targetWeek = if (semanaActual == 1) 52 else semanaActual - 1
                val targetYear = if (semanaActual == 1) anioActual - 1 else anioActual
                anioMov == targetYear && semanaMov == targetWeek
            }
            "Mensual" -> {
                val targetMonth = if (mesActual == 0) 11 else mesActual - 1
                val targetYear = if (mesActual == 0) anioActual - 1 else anioActual
                anioMov == targetYear && mesMov == targetMonth
            }
            "Anual" -> anioMov == anioActual - 1
            else -> false
        }
    }.sumOf { it.monto }

    // MATEMÁTICA PARA EL TEXTO DE COMPARACIÓN (Ej: ↓ 12% vs el periodo pasado)
    val diferenciaGastos = gastosTotal - gastosPasados
    val porcentajeDiferencia = if (gastosPasados > 0) (abs(diferenciaGastos) / gastosPasados) * 100 else 0.0
    val porcentajeFormateado = "%.1f".format(porcentajeDiferencia)

    val textoComparacion = when {
        gastosPasados == 0.0 && gastosTotal > 0 -> "↑ 100% vs el periodo pasado"
        gastosPasados == 0.0 -> "Sin datos del periodo anterior"
        diferenciaGastos < 0 -> "↓ $porcentajeFormateado% vs el periodo pasado"
        diferenciaGastos > 0 -> "↑ $porcentajeFormateado% vs el periodo pasado"
        else -> "= Mismo gasto que el periodo pasado"
    }
    val colorComparacion = if (diferenciaGastos <= 0) Color(0xFF1B3D2F) else Color.Red

    // 4. DATOS PARA GRÁFICOS Y ANÁLISIS
    val categorias = categoriasFree
    val gastosAgrupados = categorias.map { categoria ->
        val totalCategoria = gastos.filter { it.categoriaId == categoria.id }.sumOf { it.monto }
        Pair(categoria.nombre, totalCategoria)
    }.filter { it.second > 0.0 }

    val categoriaMayorGasto = gastosAgrupados.maxByOrNull { it.second }

    // 5. DATOS PARA GRÁFICO DE BARRAS (Anual)
    val gastosAnualesPorMes = MutableList(12) { 0.0 }
    if (selectedTab == "Anual") {
        gastos.forEach { mov ->
            val cal = Calendar.getInstance().apply { timeInMillis = mov.fecha }
            gastosAnualesPorMes[cal.get(Calendar.MONTH)] += mov.monto
        }
    }

    val gastosMostrados = if (verTodos) gastos else gastos.take(3)

    // SIMULACIÓN DE ESTADO PREMIUM (Cámbialo a true para probar que todo funciona)
    val isPremium = true

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reportes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "volver")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEAF2EC))
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- TABS DE NAVEGACIÓN ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFDDE6DD))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Semanal", "Mensual", "Anual").forEach { tab ->
                        val selected = tab == selectedTab
                        val isLocked = !isPremium && (tab == "Semanal" || tab == "Anual")

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    when {
                                        selected -> Color.White
                                        isLocked -> Color.LightGray.copy(alpha = 0.5f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable(enabled = true) {
                                    if (isLocked) navHostController.navigate("premium")
                                    else selectedTab = tab
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isLocked) {
                                    Icon(Icons.Default.Lock, contentDescription = "Premium", modifier = Modifier.size(14.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = tab,
                                    color = if (isLocked) Color.Gray else Color.Black,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }

            // --- GRÁFICOS Y TOTAL GASTADO ---
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Lógica del Gráfico Premium vs Free
                        if (isPremium) {
                            if (gastos.isEmpty()) {
                                Text("No hay gastos registrados en este periodo.", color = Color.Gray, modifier = Modifier.padding(32.dp))
                            } else if (selectedTab == "Anual") {
                                PremiumAnnualBarChart(gastosPorMes = gastosAnualesPorMes)
                            } else {
                                PremiumDonutChart(gastosPorCategoria = gastosAgrupados, totalGastos = gastosTotal, modifier = Modifier.fillMaxWidth())
                            }
                        } else {
                            // Gráfico difuminado para usuarios Free
                            Box(contentAlignment = Alignment.Center) {
                                val fakeData = listOf(Pair("A", 40.0), Pair("B", 30.0), Pair("C", 30.0))
                                PremiumDonutChart(
                                    gastosPorCategoria = fakeData,
                                    totalGastos = gastosTotal,
                                    modifier = Modifier.fillMaxWidth().blur(10.dp)
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Lock, contentDescription = "Premium", tint = Color(0xFF1B3D2F), modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Gráficos Premium", fontWeight = FontWeight.Bold, color = Color(0xFF1B3D2F))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // COMPARACIÓN REAL
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (diferenciaGastos <= 0) Color(0xFFD1F2E0) else Color(0xFFFFEBEB)) // Verde si bajó, Rojo si subió
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .then(if (!isPremium) Modifier.blur(6.dp) else Modifier)
                        ) {
                            Text(textoComparacion, color = colorComparacion, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // --- CATEGORÍAS PRINCIPALES ---
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDDEEDD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Categorías principales", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))

                        categorias.forEach { categoria ->
                            var gastosCategoria = 0.0
                            gastos.forEach { mov ->
                                if (mov.categoriaId == categoria.id) {
                                    gastosCategoria += mov.monto
                                }
                            }
                            CategoryItem(categoria, gastosCategoria, presupuestos)
                        }

                        if (!isPremium) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { navHostController.navigate("premium") }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = "Premium", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Crear categorías personalizadas", color = Color.Gray, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // --- BOTÓN EXPORTAR Y HEADER DEL HISTORIAL ---
            item {
                Column {
                    OutlinedButton(
                        onClick = {
                            if(isPremium) {
                                val uri = viewModel.exportarMovimientosACSV(context, gastos)
                                if (uri != null) {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/csv"
                                        putExtra(Intent.EXTRA_SUBJECT, "Reporte de Gastos")
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Exportar reporte a..."))
                                }
                            }
                            else { navHostController.navigate("premium") }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isPremium) Icons.Default.Share else Icons.Default.Lock,
                            contentDescription = "Exportar",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Exportar a Excel (CSV)")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Historial", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            if (verTodos) "Ver menos" else "Ver todo",
                            color = Color(0xFF1DB954),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { verTodos = !verTodos }
                        )
                    }
                }
            }

            // --- LISTA DEL HISTORIAL ---
            items(gastosMostrados) { gasto ->
                HistoryItem(gasto)
            }

            // --- TARJETA PREMIUM (UPSELL PRINCIPAL) ---
            if (!isPremium) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3D2F)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Star, contentDescription = "Premium", tint = Color(0xFFFFD700), modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Toma el control total", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "Desbloquea reportes anuales, exportación a Excel, categorías ilimitadas y análisis profundos de tus hábitos.",
                                color = Color(0xFFEAF2EC), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { navHostController.navigate("premium") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            ) {
                                Text("Ver planes Premium", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // --- MENSAJE DE IA REAL (SOLO PREMIUM) ---
            if (isPremium) {
                item {
                    // LÓGICA DE TEXTOS IA
                    val tituloIA: String
                    val cuerpoIA: String

                    if (gastosTotal == 0.0) {
                        tituloIA = "Sin actividad reciente"
                        cuerpoIA = "Registra tus gastos para generar un análisis inteligente de este periodo."
                    } else if (gastosPasados == 0.0) {
                        tituloIA = "¡Análisis Inicial!"
                        cuerpoIA = "Este periodo tu mayor fuga de dinero fue en ${categoriaMayorGasto?.first ?: "varios"} ($${categoriaMayorGasto?.second?.toInt() ?: 0})."
                    } else if (diferenciaGastos <= 0) {
                        tituloIA = "¡Excelente manejo!"
                        cuerpoIA = "Lograste reducir tus gastos un $porcentajeFormateado% vs el ciclo pasado. Tu gasto principal fue ${categoriaMayorGasto?.first ?: "general"}."
                    } else {
                        tituloIA = "Cuidado con los gastos"
                        cuerpoIA = "Gastaste un $porcentajeFormateado% más que el periodo pasado. Vigila tu presupuesto de ${categoriaMayorGasto?.first ?: "general"} ($${categoriaMayorGasto?.second?.toInt() ?: 0})."
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = if (diferenciaGastos <= 0) Color(0xFF00C853) else Color(0xFFE53935))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("ANÁLISIS INTELIGENTE", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(tituloIA, color = Color.White, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(cuerpoIA, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// COMPONENTES AUXILIARES
// ==========================================

// Función para acortar números grandes (ej. 1400 -> 1.4k)
fun compactFormat(value: Double): String {
    return when {
        value >= 1_000_000 -> "%.1fM".format(value / 1_000_000)
        value >= 1_000 -> "%.1fk".format(value / 1_000)
        else -> value.toInt().toString()
    }
}

@Composable
fun CategoryItem(categoria: Categoria, gastosCategoy: Double, presupuestos: List<Presupuesto>) {
    var presupuesto = 0.0
    presupuestos.forEach { presupuestoCat ->
        if (presupuestoCat.categoriaId == categoria.id) {
            presupuesto = presupuestoCat.montoLimite.toDouble()
        }
    }

    val progress: Float = if (presupuesto > 0) {
        (gastosCategoy.toFloat() / presupuesto.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(categoria.nombre)
            Text("$$gastosCategoy", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = if (progress >= 1f) Color.Red else Color(0xFF1DB954),
            trackColor = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun HistoryItem(gasto: Movimiento) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(gasto.descripcion, modifier = Modifier.weight(1f))
            Text("-$${gasto.monto}", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PremiumDonutChart(gastosPorCategoria: List<Pair<String, Double>>, totalGastos: Double, modifier: Modifier = Modifier) {
    val chartColors = listOf(
        Color(0xFF1DB954), Color(0xFF1B3D2F), Color(0xFFFFD700),
        Color(0xFF4A90E2), Color(0xFFFF6B6B), Color(0xFF9B59B6)
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier.padding(16.dp)) {
        Canvas(modifier = Modifier.size(180.dp)) {
            var startAngle = -90f

            gastosPorCategoria.forEachIndexed { index, pair ->
                val gasto = pair.second
                if (totalGastos > 0) {
                    val sweepAngle = (gasto.toFloat() / totalGastos.toFloat()) * 360f
                    val color = chartColors[index % chartColors.size]

                    drawArc(
                        color = color, startAngle = startAngle, sweepAngle = sweepAngle,
                        useCenter = false, style = Stroke(width = 30.dp.toPx(), cap = StrokeCap.Butt),
                        size = Size(size.width, size.height)
                    )
                    startAngle += sweepAngle
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
            Text("$${compactFormat(totalGastos)}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1B3D2F))
        }
    }
}

@Composable
fun PremiumAnnualBarChart(gastosPorMes: List<Double>) {
    val maxGasto = gastosPorMes.maxOrNull()?.toFloat() ?: 1f
    val meses = listOf("E", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            gastosPorMes.forEachIndexed { index, gasto ->
                val heightPercentage = if (maxGasto > 0) (gasto.toFloat() / maxGasto).coerceIn(0f, 1f) else 0f

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    if (gasto > 0) {
                        // AQUÍ APLICAMOS EL FORMATO (1400 -> 1.4k) Y LE QUITAMOS LAS RESTRICCIONES DE ANCHO
                        Text(
                            text = "$${compactFormat(gasto)}",
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Visible,
                            modifier = Modifier.requiredWidth(30.dp), // Permite que el texto sobresalga un poco de la barra
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(120.dp * heightPercentage)
                            .width(16.dp)
                            .background(
                                color = if (gasto > 0) Color(0xFF1DB954) else Color(0xFFEAF2EC),
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(meses[index], style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
        }
    }
}