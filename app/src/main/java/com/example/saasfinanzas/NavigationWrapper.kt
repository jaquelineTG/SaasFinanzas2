package com.example.saasfinanzas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.saasfinanzas.features.auth.LoginScreen
import com.example.saasfinanzas.features.auth.RegisterScreen
import com.example.saasfinanzas.features.budget.AddBudget
import com.example.saasfinanzas.features.budget.BudgetScreen
import com.example.saasfinanzas.features.categorys.AddCategory
import com.example.saasfinanzas.features.components.BottomNavigationBar
import com.example.saasfinanzas.features.goals.AddAporte
import com.example.saasfinanzas.features.goals.AddGoal
import com.example.saasfinanzas.features.goals.DetailGoal
import com.example.saasfinanzas.features.goals.GoalScreen
import com.example.saasfinanzas.features.home.Home
import com.example.saasfinanzas.features.plus.PlusScreen
import com.example.saasfinanzas.features.plus.configuration.ChangePasswordScreen
import com.example.saasfinanzas.features.plus.configuration.ConfigurationScreen
import com.example.saasfinanzas.features.plus.premium.PremiumScreen
import com.example.saasfinanzas.features.plus.reports.ReportScreen
import com.example.saasfinanzas.features.transactions.AddTransaccionScreen
import com.example.saasfinanzas.features.transactions.TransactionsScreen
import com.example.saasfinanzas.features.welcome.Welcome
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationWrapper(navHostController: NavHostController) {

    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Calculamos el destino inicial una sola vez de forma síncrona.
    val startDest = remember {
        if (FirebaseAuth.getInstance().currentUser != null) "home" else "welcome"
    }

    val screensWithBottomNav = listOf(
        "home",
        "movimientos",
        "metas",
        "presupuestos",
        "mas"
    )

    Scaffold(
        bottomBar = {
            // Si la ruta está en la lista permitida, mostramos la barra inferior.
            if (currentRoute in screensWithBottomNav) {
                BottomNavigationBar(navHostController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navHostController,
            startDestination = startDest, // Inicia donde debe iniciar
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("welcome") { Welcome(navHostController) }
            composable("login") { LoginScreen(navHostController) }
            composable("register") { RegisterScreen(navHostController) }
            composable("home") { Home(navHostController) }
            composable("movimientos") { TransactionsScreen(navHostController) }
            composable("metas") { GoalScreen(navHostController) }
            composable("añadir_metas") { AddGoal(navHostController) }

            composable(route = "detail_goal/{metaId}/{porcentaje}/{progress}") { backStackEntry ->
                val metaId = backStackEntry.arguments?.getString("metaId")
                val porcentaje = backStackEntry.arguments?.getString("porcentaje")
                val progress = backStackEntry.arguments?.getString("progress")
                DetailGoal(navHostController, metaId, porcentaje, progress)
            }

            composable("añadir_aporte/{metaId}") { backStackEntry ->
                val metaId = backStackEntry.arguments?.getString("metaId")
                AddAporte(navHostController, metaId)
            }

            composable("presupuestos") { BudgetScreen(navHostController) }
            composable("añadir_presupuestos") { AddBudget(navHostController) }
            composable("añadir_movimiento") { AddTransaccionScreen(navHostController) }
            composable("mas") { PlusScreen(navHostController) }
            composable("configuracion") { ConfigurationScreen(navHostController) }
            composable("cambiarContraseña") { ChangePasswordScreen(navHostController) }
            composable("reportes") { ReportScreen(navHostController) }
            composable("premium") { PremiumScreen(navHostController) }
            composable("categorias") { AddCategory(navHostController) }
        }
    }
}