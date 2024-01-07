package com.example.p2us_cristhian_bravo.data.modelo


import java.io.Serializable

data class Producto(
    val id:String,
    val descripcion:String,
    var comprado: Boolean = false
) : Serializable