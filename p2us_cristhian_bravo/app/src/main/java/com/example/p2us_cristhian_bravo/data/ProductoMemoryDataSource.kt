package com.example.p2us_cristhian_bravo.data

import android.util.Log
import com.example.p2us_cristhian_bravo.data.modelo.Producto
import java.util.UUID

class ProductoMemoryDataSource {
    private val _productos = mutableListOf<Producto>()

    init {
        _productos.addAll(productosDePrueba())
    }

    fun obtenerTodas():List<Producto> {
        return _productos
    }

    fun insertar(vararg productos: Producto) {
        _productos.addAll( productos.asList() )
    }

    fun eliminar(producto: Producto) {
        _productos.remove(producto)
        Log.v("DataSource", _productos.toString())
    }

    fun cambiarEstadoProducto(productoId: String, comprado: Boolean) {
        val index = _productos.indexOfFirst { it.id == productoId }
        if (index != -1) {
            val producto = _productos[index].copy(comprado = comprado)
            _productos[index] = producto
        }
    }

    private fun productosDePrueba():List<Producto> = listOf(
        Producto(UUID.randomUUID().toString(), "Arroz", false)
        , Producto(UUID.randomUUID().toString(), "Salsa de Tomates", true)
        , Producto(UUID.randomUUID().toString(), "Sal", false)
        , Producto(UUID.randomUUID().toString(), "Queso", false)
    )
}