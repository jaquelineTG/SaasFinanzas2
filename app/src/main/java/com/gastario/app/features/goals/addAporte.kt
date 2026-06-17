package com.gastario.app.features.goals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gastario.app.data.model.Aporte
import com.gastario.app.features.components.PrimaryButton
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

// 🔹 Tu verde oscuro oficial
private val greenColor = Color(0xFF2E7D32)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAporte(navController: NavHostController, metaId: String?) {

    val viewModel: GoalViewModel = hiltViewModel()
    val viewModelAporte: AporteViewModel = hiltViewModel()
    val metas by viewModel.metas.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarMetas()
    }

    val meta = metas.find { it.id == metaId }

    var monto by remember { mutableStateOf("") }
    var isSaving by rememberSaveable { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = greenColor,
            primaryContainer = greenColor.copy(alpha = 0.1f),
            onPrimaryContainer = greenColor
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F4F6))
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item { Spacer(modifier = Modifier.height(20.dp)) }

            /* 🔹 HEADER */
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "")
                    }

                    Text(
                        "Agregar Aporte",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            /* 🔹 MONTO */
            item {
                CardField("MONTO A APORTAR") {

                    OutlinedTextField(
                        value = monto,
                        onValueChange = {
                            if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                                monto = it
                            }
                        },
                        leadingIcon = { Text("$") },
                        placeholder = { Text("0.00") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = greenColor,
                            focusedLabelColor = greenColor,
                            cursorColor = greenColor,
                            focusedLeadingIconColor = greenColor
                        )
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            /* 🔹 BOTONES RÁPIDOS UNIFICADOS */
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // 🔹 Reducimos a 8.dp para que quepan bien los 4
                    modifier = Modifier.fillMaxWidth()
                ) {

                    QuickAddButton("+50", Modifier.weight(1f)) {
                        monto = ((monto.toDoubleOrNull() ?: 0.0) + 50).toString()
                    }

                    QuickAddButton("+100", Modifier.weight(1f)) {
                        monto = ((monto.toDoubleOrNull() ?: 0.0) + 100).toString()
                    }

                    QuickAddButton("+200", Modifier.weight(1f)) {
                        monto = ((monto.toDoubleOrNull() ?: 0.0) + 200).toString()
                    }

                    QuickAddButton("+500", Modifier.weight(1f)) {
                        monto = ((monto.toDoubleOrNull() ?: 0.0) + 500).toString()
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            /* 🔹 INFO META */
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        val imageUrl = meta?.imageUrl
                        if (!imageUrl.isNullOrEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = "Foto de la meta",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        Color(0xFFD1FAE5),
                                        RoundedCornerShape(10.dp)
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Este aporte se sumará a:",
                                color = Color.Gray
                            )
                            Text(
                                text = meta?.nombre ?: "",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }

            /* 🔹 BOTÓN */
            item {
                PrimaryButton("Guardar Aporte", isSaving) {

                    if (monto.isBlank()) {
                        println("Faltan datos")
                        return@PrimaryButton
                    }
                    isSaving = true

                    val aporte = Aporte(
                        id = "",
                        metaId = metaId.toString(),
                        monto = monto.toDoubleOrNull() ?: 0.0,
                        fecha = System.currentTimeMillis()
                    )

                    viewModelAporte.addAporte(aporte)

                    navController.popBackStack()
                }

            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

// 🔹 NUEVO DISEÑO RESPONSIVO DEL BOTÓN RÁPIDO
@Composable
fun QuickAddButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp), // 👈 Menos padding para que quepa el texto
        border = BorderStroke(1.dp, greenColor),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = greenColor
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}