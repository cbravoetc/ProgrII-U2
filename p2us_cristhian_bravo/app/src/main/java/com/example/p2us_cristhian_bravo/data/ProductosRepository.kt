package com.example.p2us_cristhian_bravo.data

import com.example.p2us_cristhian_bravo.data.modelo.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.FileInputStream
import java.io.FileOutputStream

class ProductosRepository(
    private val productoMemoryDataSource: ProductoMemoryDataSource = ProductoMemoryDataSource(),
    private val productoDiskDataSource: ProductoDiskDataSource = ProductoDiskDataSource()

) {

    private val _productosStream = MutableStateFlow(listOf<Producto>())

    fun getProductosEnDisco(fileInputStream: FileInputStream) {
        val productos = productoDiskDataSource.obtener(fileInputStream)
        insertar(* productos.toTypedArray())
    }

    fun guardarProductosEnDisco(fileOutputStream: FileOutputStream) {
        productoDiskDataSource.guardar(fileOutputStream, productoMemoryDataSource.obtenerTodas())
    }


    fun getProductosStream():StateFlow<List<Producto>> {
        _productosStream.update {
            ArrayList(productoMemoryDataSource.obtenerTodas())
        }
        return _productosStream.asStateFlow()
    }

    fun insertar(vararg productos: Producto) {
        productoMemoryDataSource.insertar(*productos) // spread operator (*)
        getProductosStream()
    }

    fun eliminar(producto: Producto) {
        productoMemoryDataSource.eliminar(producto)
        getProductosStream()
    }

    fun cambiarEstadoProducto(productoId: String, comprado: Boolean) {
        productoMemoryDataSource.cambiarEstadoProducto(productoId, comprado)
        _productosStream.update { productos ->
            productos.map { if (it.id == productoId) it.copy(comprado = comprado) else it }
        }
    }

}