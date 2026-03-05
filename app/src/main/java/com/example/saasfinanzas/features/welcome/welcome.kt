package com.example.saasfinanzas.features.welcome
import androidx.compose.foundation.BorderStroke
import com.example.saasfinanzas.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saasfinanzas.features.components.PrimaryButton
import com.example.saasfinanzas.ui.theme.BluePrimary
import com.example.saasfinanzas.ui.theme.SaasFinanzasTheme
import com.example.saasfinanzas.ui.theme.greenGradient
import com.example.saasfinanzas.ui.theme.greenPrimary
import com.example.saasfinanzas.ui.theme.white


@Composable
fun Welcome(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()

            .background(Brush.verticalGradient(listOf(greenGradient,white), startY = 0f, endY = 400f))
    ) {

        Spacer(modifier = Modifier.weight(0.5f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(R.drawable.welcome),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Visualiza tus Finanzas",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Observa tus ingresos y gastos de forma clara y concisa con nuestros reportes gráficos",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.weight(1f))




        PrimaryButton(
            text = "Comenzar",
            onClick = {
                navController.navigate("login")
            }
        )
    }
}



@Preview(showBackground = true)
@Composable
fun WelcomePreview() {
    SaasFinanzasTheme {
        val navController = androidx.navigation.compose.rememberNavController()
        Welcome(navController = navController)
    }
}