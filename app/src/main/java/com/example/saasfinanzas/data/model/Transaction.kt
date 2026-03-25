package com.example.saasfinanzas.data.model

data class Movimiento(
    val id: String = "",
    val categoriaId: String = "",
    val categoriaNombre: String = "",
    val tipo: String = "",
    val monto: Double = 0.0,
    val descripcion: String = "",
    val fecha: Long = 0L
)