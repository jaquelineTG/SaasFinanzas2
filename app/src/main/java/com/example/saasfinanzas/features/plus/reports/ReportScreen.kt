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
import com.example.saasfinanzas.features.categorys.CategoryViewModel
import com.example.saasfinanzas.features.transactions.TransactionViewModel
import com.example.saasfinanzas.features.transactions.categoriasFree
import java.util.Calendar
import kotlin.math.abs

// 🔹 Tu verde oscuro oficial
private val greenColor = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navHostController: NavHostController) {

    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Mensual") }
    var verTodos by remember { mutableStateOf(false) }

    // 🌟 CAMBIA ESTO A 'false' PARA VER EL NUEVO DISEÑO DE UPSELL PARA USUARIOS GRATUITOS
    val isPremium = false

    val viewModel: TransactionViewModel = hiltViewModel()
    val movimientos by viewModel.movimientos.collectAsState()

    val viewModelPresupuesto: BudgetViewModel = hiltViewModel()
    val presupuestosState = viewModelPresupuesto.presupuestos.collectAsState()
    val presupuestos = presupuestosState.value

    val viewModelCat: CategoryViewModel = hiltViewModel()
    val categoriasdb by viewModelCat.categorias.collectAsState()
    val categorias = categoriasFree + categoriasdb

    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        viewModel.cargarMovimientos()
        viewModelPresupuesto.getBudgets()
        viewModelCat.getCategory()
    }

    // VARIABLES DE TIEMPO
    val calendar = Calendar.getInstance()
    val anioActual = calendar.get(Calendar.YEAR)
    val mesActual = calendar.get(Calendar.MONTH)
    val semanaActual = calendar.get(Calendar.WEEK_OF_YEAR)

    // 1. FILTRAR TODOS LOS MOVIMIENTOS DEL PERIODO (Ingresos y Gastos)
    val movimientosPeriodo = movimientos.filter { mov ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = mov.fecha
        val anioMov = cal.get(Calendar.YEAR)
        val mesMov = cal.get(Calendar.MONTH)
        val semanaMov = cal.get(Calendar.WEEK_OF_YEAR)

        when (selectedTab) {
            "Semanal" -> anioMov == anioActual && semanaMov == semanaActual
            "Mensual" -> anioMov == anioActual && mesMov == mesActual
            "Anual" -> anioMov == anioActual
            else -> false
        }
    }

    val gastos = movimientosPeriodo.filter { it.tipo == "gasto" }
    val gastosTotal = gastos.sumOf { it.monto }

    // 3. CALCULAR GASTOS DEL PERIODO ANTERIOR
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

    // MATEMÁTICA PARA EL TEXTO DE COMPARACIÓN
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
    val colorComparacion = if (diferenciaGastos <= 0) greenColor else Color.Red

    // 4. DATOS PARA GRÁFICOS Y ANÁLISIS
    val gastosAgrupados = gastos.groupBy { it.categoriaNombre }
        .map { Pair(it.key, it.value.sumOf { m -> m.monto }) }
        .filter { it.second > 0.0 }
        .sortedByDescending { it.second }

    val categoriaMayorGasto = gastosAgrupados.maxByOrNull { it.second }

    // 5. DATOS PARA GRÁFICO DE BARRAS (Anual)
    val gastosAnualesPorMes = MutableList(12) { 0.0 }
    if (selectedTab == "Anual") {
        gastos.forEach { mov ->
            val cal = Calendar.getInstance().apply { timeInMillis = mov.fecha }
            gastosAnualesPorMes[cal.get(Calendar.MONTH)] += mov.monto
        }
    }

    // 6. CATEGORÍAS PRINCIPALES
    val categoriasUso = categorias.mapNotNull { cat ->
        val movs = movimientosPeriodo.filter { it.categoriaId == cat.id || it.categoriaNombre == cat.nombre }
        if (movs.isEmpty()) null
        else {
            val totalGastos = movs.filter { it.tipo == "gasto" }.sumOf { it.monto }
            val totalIngresos = movs.filter { it.tipo == "ingreso" }.sumOf { it.monto }
            val presupuesto = presupuestos.find { it.categoriaId == cat.id }?.montoLimite?.toDouble() ?: 0.0

            CategoryUsageData(
                nombre = cat.nombre,
                gastos = totalGastos,
                ingresos = totalIngresos,
                presupuesto = presupuesto
            )
        }
    }.sortedByDescending { it.gastos + it.ingresos }.take(5)

    val gastosMostrados = if (verTodos) gastos else gastos.take(3)

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
                    title = { Text("Reportes", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navHostController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "volver")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFF3F4F6)
                    )
                )
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // --- TABS DE NAVEGACIÓN ---
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFE5E7EB))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Semanal", "Mensual", "Anual").forEach { tab ->
                            val selected = tab == selectedTab
                            val isLocked = !isPremium && (tab == "Semanal" || tab == "Anual")

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        when {
                                            selected -> Color.White
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        if (isLocked) navHostController.navigate("premium")
                                        else selectedTab = tab
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isLocked) {
                                        Icon(Icons.Default.Lock, contentDescription = "Premium", modifier = Modifier.size(14.dp), tint = greenColor)
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = tab,
                                        color = if (selected) greenColor else Color.Gray,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // --- GRÁFICOS Y TOTAL GASTADO ---
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            if (isPremium) {
                                if (gastos.isEmpty()) {
                                    Text("No hay gastos registrados en este periodo.", color = Color.Gray, modifier = Modifier.padding(32.dp))
                                } else if (selectedTab == "Anual") {
                                    PremiumAnnualBarChart(gastosPorMes = gastosAnualesPorMes)
                                } else {
                                    PremiumDonutChart(gastosPorCategoria = gastosAgrupados, totalGastos = gastosTotal, modifier = Modifier.fillMaxWidth())
                                }
                            } else {
                                // 🌟 MEJORA VISUAL: El usuario ve el gráfico real traslúcido, lo que incita más a comprar
                                Box(contentAlignment = Alignment.Center) {
                                    val dummyData = listOf(Pair("Comida", 1500.0), Pair("Renta", 3000.0), Pair("Otros", 800.0))
                                    PremiumDonutChart(
                                        gastosPorCategoria = dummyData,
                                        totalGastos = 5300.0,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .blur(8.dp) // Desenfoque sutil
                                    )
                                    // Tarjeta de bloqueo flotante e interactiva
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color.White.copy(alpha = 0.85f))
                                            .clickable { navHostController.navigate("premium") }
                                            .padding(16.dp)
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = "Premium", tint = greenColor, modifier = Modifier.size(28.dp))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Ver distribución de gastos", fontWeight = FontWeight.Bold, color = greenColor, fontSize = 14.sp)
                                        Text("Disponible en Premium 💎", color = Color.Gray, fontSize = 11.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // COMPARACIÓN REAL
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(if (diferenciaGastos <= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEB))
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .then(if (!isPremium) Modifier.blur(5.dp) else Modifier)
                            ) {
                                Text(textoComparacion, color = colorComparacion, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                // --- CATEGORÍAS PRINCIPALES (TOP USO) ---
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Categorías más usadas", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(16.dp))

                            if (categoriasUso.isEmpty()) {
                                Text("Sin movimientos en este periodo.", color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            } else {
                                categoriasUso.forEach { catData ->
                                    CategoryUsageItemUI(catData)
                                }
                            }

                            if (!isPremium) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { navHostController.navigate("premium") }
                                        .background(Color(0xFFF3F4F6))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = "Premium", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Crear categorías personalizadas", color = Color.Gray, fontWeight = FontWeight.Medium, fontSize = 13.sp)
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = greenColor),
                            border = androidx.compose.foundation.BorderStroke(1.dp, greenColor)
                        ) {
                            Icon(
                                imageVector = if (isPremium) Icons.Default.Share else Icons.Default.Lock,
                                contentDescription = "Exportar",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exportar a Excel (CSV)", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Historial de Gastos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                            Text(
                                if (verTodos) "Ver menos" else "Ver todo",
                                color = greenColor,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { verTodos = !verTodos }
                            )
                        }
                    }
                }

                // --- LISTA DEL HISTORIAL ---
                items(gastosMostrados) { gasto ->
                    HistoryItem(gasto)
                }

                // --- MENSAJE DE IA REAL O UPSELL DE IA ---
                item {
                    if (isPremium) {
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
                            cuerpoIA = "Lograste reducir tus gastos un $porcentajeFormateado% vs el ciclo pasado. Tu principal gasto fue en ${categoriaMayorGasto?.first ?: "general"}."
                        } else {
                            tituloIA = "Cuidado con los gastos"
                            cuerpoIA = "Gastaste un $porcentajeFormateado% más que el periodo pasado. Vigila tu presupuesto de ${categoriaMayorGasto?.first ?: "general"} ($${categoriaMayorGasto?.second?.toInt() ?: 0})."
                        }

                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = if (diferenciaGastos <= 0) greenColor else Color(0xFFD32F2F))
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "IA", tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ANÁLISIS INTELIGENTE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(tituloIA, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(cuerpoIA, color = Color.White.copy(alpha = 0.9f), lineHeight = 20.sp)
                            }
                        }
                    } else {
                        // 🌟 MEJORA PSICOLÓGICA: El usuario free ve la caja de IA bloqueada.
                        // Despierta curiosidad y necesidad ("¿Qué dirá la IA sobre mi dinero?")
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = greenColor.copy(alpha = 0.08f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, greenColor.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navHostController.navigate("premium") }
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "IA", tint = greenColor, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ANÁLISIS FINANCIERO CON IA", color = greenColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("La IA está analizando tus datos...", color = Color.Black, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Desbloquea Premium para recibir consejos automáticos sobre cómo optimizar tus ahorros, alertas de fugas de dinero y sugerencias personalizadas.",
                                    color = Color.DarkGray, fontSize = 13.sp, lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text("Obtener Consejos con IA ✨", color = greenColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                // --- TARJETA PREMIUM (UPSELL PRINCIPAL ABAJO) ---
                if (!isPremium) {
                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = greenColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Star, contentDescription = "Premium", tint = Color(0xFFFFD700), modifier = Modifier.size(48.dp))
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
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Text("Ver planes Premium", color = greenColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        }
    }
}

// ==========================================
// COMPONENTES AUXILIARES
// ==========================================

data class CategoryUsageData(
    val nombre: String,
    val gastos: Double,
    val ingresos: Double,
    val presupuesto: Double
)

@Composable
fun CategoryUsageItemUI(data: CategoryUsageData) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(data.nombre, fontWeight = FontWeight.SemiBold, color = Color.DarkGray, fontSize = 15.sp)

            Column(horizontalAlignment = Alignment.End) {
                if (data.ingresos > 0) {
                    Text("+ $${data.ingresos}", color = greenColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                if (data.gastos > 0) {
                    Text("- $${data.gastos}", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        if (data.presupuesto > 0 && data.gastos > 0) {
            val progress = (data.gastos.toFloat() / data.presupuesto.toFloat()).coerceIn(0f, 1f)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = if (progress >= 1f) Color.Red else greenColor,
                trackColor = Color(0xFFF3F4F6)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Presupuesto: $${data.presupuesto}",
                color = Color.Gray,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

fun compactFormat(value: Double): String {
    return when {
        value >= 1_000_000 -> "%.1fM".format(value / 1_000_000)
        value >= 1_000 -> "%.1fk".format(value / 1_000)
        else -> value.toInt().toString()
    }
}

@Composable
fun HistoryItem(gasto: Movimiento) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(gasto.descripcion, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
            Text("-$${gasto.monto}", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PremiumDonutChart(gastosPorCategoria: List<Pair<String, Double>>, totalGastos: Double, modifier: Modifier = Modifier) {
    val chartColors = listOf(
        greenColor, Color(0xFF81C784), Color(0xFFFFD54F),
        Color(0xFF64B5F6), Color(0xFFE57373), Color(0xFFBA68C8)
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
            Text("Gastos", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
            Text("$${compactFormat(totalGastos)}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = greenColor)
        }
    }
}

@Composable
fun PremiumAnnualBarChart(gastosPorMes: List<Double>) {
    val maxGasto = gastosPorMes.maxOrNull()?.toFloat() ?: 1f
    val meses = listOf("E", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
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
                        Text(
                            text = "$${compactFormat(gasto)}",
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Visible,
                            modifier = Modifier.requiredWidth(30.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(120.dp * heightPercentage)
                            .width(16.dp)
                            .background(
                                color = if (gasto > 0) greenColor else Color(0xFFE8F5E9),
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