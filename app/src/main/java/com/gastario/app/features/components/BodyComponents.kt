package com.gastario.app.features.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 🔹 Tu verde oscuro oficial
private val greenColor = Color(0xFF2E7D32)

@Composable
fun PrimaryButton(
    text: String,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = greenColor,
            contentColor = Color.White,
            disabledContainerColor = greenColor.copy(alpha = 0.6f),
            disabledContentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(50.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text)
        } else {
            Text(text)
        }
    }
}

@Composable
fun Alert(
    text: String,
    title: String,
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (!showDialog) return

    AlertDialog(
        onDismissRequest = { onDismiss() },
        shape = RoundedCornerShape(16.dp), // 🔹 Bordes más modernos y redondeados
        containerColor = Color.White, // 🔹 Fondo blanco limpio (mata el fondo morado/rosado)
        titleContentColor = Color.Black, // 🔹 Título en negro
        textContentColor = Color.DarkGray, // 🔹 Texto en gris oscuro
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = text,
                fontSize = 16.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = greenColor // 🔹 El texto del botón ahora es tu verde oficial
                )
            ) {
                Text(
                    text = "Entendido", // (Opcional) Cambié "Confirmar" a "Entendido", suele sentirse más natural
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    )
}