package com.example.saasfinanzas

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.navigation.NavHostController
import com.example.saasfinanzas.ui.theme.SaasFinanzasTheme
//navegacion

import androidx.navigation.compose.rememberNavController
//
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SaasFinanzasTheme {


                val navController = rememberNavController()

                NavigationWrapper(navController)

            }
        }
    }
}

