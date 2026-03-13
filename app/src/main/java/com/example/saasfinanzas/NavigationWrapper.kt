package com.example.saasfinanzas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.saasfinanzas.features.auth.LoginScreen
import com.example.saasfinanzas.features.auth.RegisterScreen
import com.example.saasfinanzas.features.budget.AddBudget
import com.example.saasfinanzas.features.budget.BudgetScreen
import com.example.saasfinanzas.features.components.BottomNavigationBar
import com.example.saasfinanzas.features.goals.AddGoal
import com.example.saasfinanzas.features.goals.DetailGoal
import com.example.saasfinanzas.features.goals.GoalScreen
import com.example.saasfinanzas.features.home.Home
import com.example.saasfinanzas.features.plus.PlusScreen
import com.example.saasfinanzas.features.transactions.AddTransaccionScreen
import com.example.saasfinanzas.features.transactions.TransactionsScreen
import com.example.saasfinanzas.features.welcome.Welcome

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationWrapper(navHostController: NavHostController) {

    Scaffold(
        bottomBar = {

                BottomNavigationBar(navHostController)

        }
    ) { innerPadding ->

        NavHost(
            navController = navHostController,
            startDestination = "presupuestos",
            modifier = Modifier.padding(innerPadding)
        )
        {


            composable("welcome") {
                Welcome(navHostController)
            }

            composable("login") {
                LoginScreen(navHostController)
            }
            composable("register") {
                RegisterScreen(navHostController)
            }
            composable("home") {
                Home(navHostController)
            }

            composable("movimientos") {
                TransactionsScreen(navHostController)
            }

            composable("metas") {
                GoalScreen(navHostController)
            }
            composable("añadir_metas") {
                AddGoal(navHostController)
            }
            composable(
                route = "detail_goal/{metaId}"
            ) { backStackEntry ->

                val metaId = backStackEntry.arguments?.getString("metaId")

                DetailGoal(navHostController,metaId)
            }

            composable("presupuestos") {
                BudgetScreen(navHostController)
            }
            composable("añadir_presupuestos") {
                AddBudget(navHostController)
            }

//            composable("añadir_categoria") {
//                AñadirCategoria(navHostController)
//            }

            composable("añadir_movimiento") {
                AddTransaccionScreen(navHostController)
            }

            composable("mas") {
                PlusScreen(navHostController)
            }
        }

    }
    }