package com.example.saasfinanzas.features.transactions

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saasfinanzas.features.budget.BudgetItem
import com.example.saasfinanzas.features.budget.presupuestos


data class Transacciones(
    val categoriaNombre: String,
    val monto: Float,
    val descripcion:String

)
//traer del usuario logeado
val transacciones = listOf(
    Transacciones("Compras", 100f,"Almuerzo en restaurante" ),
    Transacciones("Transporte", 200f,"Almuerzo en restaurante"),
    Transacciones("Renta", 25.5f,"Almuerzo en restaurante")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(navHostController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Presupuestos") },
                actions = {
                    IconButton(onClick = {navHostController.navigate("añadir_movimiento")}) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        //filtros-fecha-categoria-tipo(gasto,ingreso)

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(transacciones) { transaccion ->
                transaccionItem(
                    categoriaNombre= transaccion.categoriaNombre,
                    monto=transaccion.monto,
                    descripcion=transaccion.descripcion
                )
            }




        }
    }

}

@Composable
fun transaccionItem(
    categoriaNombre: String,
    monto: Float,
    descripcion: String
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFFFDFDFD)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // 🔹 Icono dentro de círculo verde claro
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFFE8F5E9),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 🔹 Texto central
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1C1C1C)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = categoriaNombre,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // 🔹 Monto alineado a la derecha
            Text(
                text = "$${"%.2f".format(monto)}",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF22C55E)
            )
        }
    }
}