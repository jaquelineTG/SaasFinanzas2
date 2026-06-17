package com.gastario.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.gastario.app.ui.theme.SaasFinanzasTheme
//navegacion

import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

//

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


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

