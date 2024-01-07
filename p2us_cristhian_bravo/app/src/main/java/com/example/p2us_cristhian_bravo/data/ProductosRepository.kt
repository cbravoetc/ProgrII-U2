package com.example.p2us_cristhian_bravo.data

import com.example.p2us_cristhian_bravo.data.modelo.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.FileInputStream
import java.io.FileOutputStream

class ProductosRepository(
    private val productoDataSource: ProductoMemoryDataSource = ProductoMemoryDataSource(),
    private val productoDiskDataSource: ProductoDiskDataSource = ProductoDiskDataSource()

) {

    private val _productosStream = MutableStateFlow(listOf<Producto>())

    fun getProductosEnDisco(fileInputStream: FileInputStream) {
        val tareas = productoDiskDataSource.obtener(fileInputStream)
        insertar(*tareas.toTypedArray())
    }

    fun guardarProductosEnDisco(fileOutputStream: FileOutputStream) {
        productoDiskDataSource.guardar(fileOutputStream, productoDataSource.obtenerTodas())
    }


    fun getProductosStream():StateFlow<List<Producto>> {
        _productosStream.update {
            ArrayList(productoDataSource.obtenerTodas())
        }
        return _productosStream.asStateFlow()
    }

    fun insertar(vararg productos: Producto) {
        productoDataSource.insertar(*productos) // spread operator (*)
        getProductosStream()
    }

    fun eliminar(producto: Producto) {
        productoDataSource.eliminar(producto)
        getProductosStream()
    }

    fun cambiarEstadoProducto(productoId: String, comprado: Boolean) {
        productoDataSource.cambiarEstadoProducto(productoId, comprado)
        _productosStream.update { productos ->
            productos.map { if (it.id == productoId) it.copy(comprado = comprado) else it }
        }
    }

}