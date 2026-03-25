package com.example.saasfinanzas.data.model

data class Presupuesto(
    val id: String = "",
    val categoriaId: String = "",
    val categoriaNombre: String = "",
    val montoLimite: Double = 0.0,
    val mes: Int = 0,
    val anio: Int = 0,
    val creadoEn: Long = 0L
)