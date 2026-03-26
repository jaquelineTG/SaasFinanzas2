package com.example.saasfinanzas.data.model

data class Categoria(
    val id: String = "",
    val nombre: String = "",
    val icono: String = "",// guardas el nombre del icono
    val tipo: String = "" // ingreso o gasto
)