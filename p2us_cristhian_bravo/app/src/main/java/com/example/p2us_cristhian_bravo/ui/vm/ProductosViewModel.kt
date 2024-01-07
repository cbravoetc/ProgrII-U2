package com.example.p2us_cristhian_bravo.ui.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p2us_cristhian_bravo.data.modelo.Producto
import com.example.p2us_cristhian_bravo.ui.state.ProductosUIState
import com.example.p2us_cristhian_bravo.data.ProductosRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.UUID

class ProductosViewModel(
    private val productosRepository: ProductosRepository = ProductosRepository()
) : ViewModel() {

    companion object {
        const val FILE_NAME = "productos.data"
    }

    private var job: Job? = null

    private val _ordenAlfabetico = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(ProductosUIState())

    val uiState:StateFlow<ProductosUIState> = _uiState.asStateFlow()

    private val _moverCompradosAlFinal = MutableStateFlow(false)

    init {
        obtenerProductos()
    }

    fun obtenerProductosGuardadosEnDisco(fileInputStream: FileInputStream) {
        productosRepository.getProductosEnDisco(fileInputStream)
    }

    fun guardarProductosEnDisco(fileOutputStream: FileOutputStream) {
        productosRepository.guardarProductosEnDisco(fileOutputStream)
    }

    private fun obtenerProductos() {
        job?.cancel()
        job = viewModelScope.launch {
            val productosStream = productosRepository.getProductosStream()
            productosStream.collect { productosActualizadas ->
                Log.v("ProductosViewModel", "obtenerProductos() update{}")
                val productosOrdenados = when {
                    _ordenAlfabetico.value -> {
                        productosActualizadas.sortedBy { it.descripcion }
                    }
                    else -> {
                        productosActualizadas
                    }
                }

                _uiState.update { currentState ->
                    currentState.copy(productos = productosOrdenados)
                }
            }
        }
    }

    fun seDebeOrdenarAlfabeticamente(orden: Boolean) {
        _ordenAlfabetico.value = orden
        obtenerProductos()
    }

    fun agregarProducto(producto:String) {
        job = viewModelScope.launch {
            val t = Producto(UUID.randomUUID().toString(), producto)
            productosRepository.insertar(t)
            _uiState.update {
                it.copy(mensaje = "Producto agregado: ${t.descripcion}")
            }
            obtenerProductos()
        }
    }

    fun eliminarProducto(producto:Producto) {
        job = viewModelScope.launch {
            productosRepository.eliminar(producto)
            _uiState.update {
                it.copy(mensaje = "Producto eliminado: ${producto.descripcion}")
            }
            obtenerProductos()
        }
    }

    fun cambiarEstadoProducto(productoId: String, nuevoEstado: Boolean) {
        viewModelScope.launch {
            productosRepository.cambiarEstadoProducto(productoId, nuevoEstado)
            _uiState.update { currentState ->
                val nuevosProductos = currentState.productos.map { producto ->
                    if (producto.id == productoId) producto.copy(comprado = nuevoEstado) else producto
                }
                currentState.copy(productos = nuevosProductos)
            }
        }
    }

    fun setMoverCompradosAlFinal(mover: Boolean) {
        Log.d("ViewModel", "setMoverCompradosAlFinal: $mover")
        _moverCompradosAlFinal.value = mover
        obtenerProductos()
    }

    fun guardar(fileOutputStream: FileOutputStream, productos:List<Producto>) {
        fileOutputStream.use { fos ->
            ObjectOutputStream(fos).use { oos ->
                oos.writeObject(productos)
            }
        }
    }

    fun obtener(fileInputStream: FileInputStream):List<Producto> {
        return try {
            fileInputStream.use { fis ->
                ObjectInputStream(fis).use { ois ->
                    ois.readObject() as? List<Producto> ?: emptyList()
                }
            }
        } catch (fnfex: FileNotFoundException) {
            emptyList<Producto>()
        } catch (ex:Exception) {
            Log.e("ProductoDiskDataSource", "obtener ex:Exception ${ex.toString()}")
            emptyList<Producto>()
        }
    }
}