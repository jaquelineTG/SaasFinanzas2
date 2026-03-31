package com.example.saasfinanzas.data.model

data class Meta(
    val id: String = "",
    val nombre: String = "",
    val montoObjetivo: Double = 0.0,
    val montoAhorrado: Double = 0.0,
    val fechaLimite: Long = 0L,
    val imageUrl: String = "",
    val creadoEn: Long = 0L
)