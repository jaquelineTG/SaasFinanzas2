package com.gastario.app.data.model

data class Usuario(
    val nombre: String = "",
    val correo: String = "",
    val fechaRegistro: Long? = null,
    // configuraciones de alertas
    val transactionAlerts: Boolean = false,
    val budgetAlerts: Boolean = false,
    val fcmToken: String = ""
)