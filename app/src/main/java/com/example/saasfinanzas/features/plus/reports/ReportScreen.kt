package com.example.saasfinanzas.features.plus.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navHostController: NavHostController) {

    var selectedTab by remember { mutableStateOf("Weekly") }

    Scaffold (

        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reportes") },
                navigationIcon = {
                    IconButton(onClick = {navHostController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3F4F6),
                    scrolledContainerColor = Color(0xFFF3F4F6)
                ))
        }
    ){ padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEAF2EC))
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {



            // TABS
            item {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFDDE6DD))
                        .padding(4.dp)
                ) {
                    listOf("Weekly", "Monthly", "Yearly").forEach { tab ->
                        val selected = tab == selectedTab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (selected) Color.White else Color.Transparent)
                                .clickable { selectedTab = tab } //  importante
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(tab)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }

            // TOTAL SPENT
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Total Spent")
                        Text("$3,240.50", style = MaterialTheme.typography.headlineMedium)

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFD1F2E0))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("12% vs last week")
                        }

                        Spacer(modifier = Modifier.height(60.dp))

                        Text(
                            "MON   TUE   WED   THU   FRI   SAT   SUN",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            // TOP CATEGORIES
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDDEEDD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Top Categories")

                        Spacer(modifier = Modifier.height(12.dp))

                        CategoryItem("Food", "$840.00", 0.8f)
                        CategoryItem("Transport", "$320.00", 0.3f)
                        CategoryItem("Entertainment", "$450.00", 0.5f)
                        CategoryItem("Bills", "$1,630.50", 0.9f)
                    }
                }
            }

            // HISTORY HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("History", style = MaterialTheme.typography.titleMedium)
                    Text("View All", color = Color(0xFF1DB954))
                }
            }

            // HISTORY LIST ( aquí puedes usar items si luego viene de API)
            items(
                listOf(
                    "Whole Foods Market" to "-$142.30",
                    "Electric Utility" to "-$89.00"
                )
            ) { (title, amount) ->
                HistoryItem(title, amount)
            }

            // BUDGET CARD
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00C853))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        Text("MONTHLY BUDGET", color = Color.White)

                        Text(
                            "You're doing great this week!",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "You've spent 20% less on Entertainment compared to last month.",
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("Adjust Budgets", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(title: String, amount: String, progress: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title)
            Text(amount)
        }

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun HistoryItem(title: String, amount: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title)
            Text(amount, color = Color.Red)
        }
    }
}

