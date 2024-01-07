package com.example.p2us_cristhian_bravo.ui.state

import com.example.p2us_cristhian_bravo.data.modelo.Producto

data class ProductosUIState (
    val mensaje:String = "",
    val productos:List<Producto> = listOf<Producto>()
)