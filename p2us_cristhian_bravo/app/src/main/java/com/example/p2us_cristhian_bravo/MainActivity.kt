package com.example.p2us_cristhian_bravo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.p2us_cristhian_bravo.data.modelo.Producto
import com.example.p2us_cristhian_bravo.ui.vm.ProductosViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val productosVm: ProductosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("MainActivity::onCreate", "Recuperando productos en disco")
        try {
            productosVm.obtenerProductosGuardadosEnDisco(openFileInput(ProductosViewModel.FILE_NAME))
        } catch (e:Exception) {
            Log.e("MainActivity::onCreate", "Archivo con productos no existe!!")
        }

        setContent {
            AppProductos()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.v("MainActivity::onPause", "Guardando a disco")
        productosVm.guardarProductosEnDisco(openFileOutput(ProductosViewModel.FILE_NAME, MODE_PRIVATE))
    }

    override fun onStop() {
        super.onStop()
        Log.v("MainActivity::onStop", "Guardando a disco")

    }
}

@Composable
fun AppProductos(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomePageUI(
                onButtonSettingsClicked = {
                    navController.navigate("settings")
                }
            )
        }
        composable("settings") {
            SettingsPageUI(
                onBackButtonClicked = {
                    navController.popBackStack()
                },
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppProductosTopBar(
    title:String = "",
    showSettingsButton:Boolean = true,
    onButtonSettingsClicked:() -> Unit = {},
    showBackButton:Boolean = false,
    onBackButtonClicked:() -> Unit = {}
) {
    val contexto = LocalContext.current
    val textoDescripConf = contexto.getString(R.string.top_settings_configuracion)
    val textoatras = contexto.getString(R.string.txt_atras)


    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if(showBackButton) {
                IconButton(onClick = {
                    onBackButtonClicked()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = textoatras
                    )
                }
            }
        },
        actions = {
            if( showSettingsButton ) {
                IconButton(onClick = {
                    onButtonSettingsClicked()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = textoDescripConf
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Preview(showSystemUi = true, locale = "es")
@Composable
fun SettingsPageUI(
    productosVm: ProductosViewModel = viewModel(),
    onBackButtonClicked:() -> Unit = {},
) {

    val contexto = LocalContext.current
    var seDebeOrdenarAlfabeticamente by rememberSaveable {
        mutableStateOf(false)
    }
    var moverProductosCompradosAlFinal by rememberSaveable {
        mutableStateOf(false)
    }

    val title:String = stringResource(id = R.string.top_settings_configuracion)
    Scaffold(
        topBar = {
            AppProductosTopBar(
                title = title,
                showSettingsButton = false,
                showBackButton = true,
                onBackButtonClicked = onBackButtonClicked
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = 24.dp,
                    vertical = it.calculateTopPadding()
                )
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = contexto.getString(R.string.stw_setting_ordena_alfa),
                    modifier = Modifier.weight(1f))
                Switch(
                    checked = seDebeOrdenarAlfabeticamente,
                    onCheckedChange = {
                        seDebeOrdenarAlfabeticamente = it
                        productosVm.seDebeOrdenarAlfabeticamente(it)

                    })
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = contexto.getString(R.string.stw_settings_por_comprar),
                    modifier = Modifier.weight(1f))
                Switch(
                    checked = moverProductosCompradosAlFinal,
                    onCheckedChange = {
                        moverProductosCompradosAlFinal = it
                        productosVm.setMoverCompradosAlFinal(it)
                    })
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, locale = "es")
@Composable
fun HomePageUI(
    productosVm: ProductosViewModel = viewModel(),
    onButtonSettingsClicked:() -> Unit = {}
) {
    val contexto = LocalContext.current
    val textoCarro = contexto.getString(R.string.carro)
    val textotitulo = contexto.getString(R.string.top_home_title)
    val uiState by productosVm.uiState.collectAsStateWithLifecycle()
    var mostrarMensaje by rememberSaveable {
        mutableStateOf(false)
    }
    var primeraEjecucion by rememberSaveable {
        mutableStateOf(true)
    }

    LaunchedEffect(uiState.mensaje) {
        if(!primeraEjecucion) {
            mostrarMensaje = true
            delay(1_000)
            mostrarMensaje = false
        }
        primeraEjecucion = false
    }

    Scaffold(
        topBar = {
            AppProductosTopBar(
                title = textotitulo,
                onButtonSettingsClicked = onButtonSettingsClicked
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedVisibility(
                visible = mostrarMensaje,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = uiState.mensaje,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(10.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.carro),
                    contentDescription = textoCarro,
                    modifier = Modifier
                        .size(92.dp)
                        .align(Alignment.Center)
                )
            }


            ProductoFormUI {
                productosVm.agregarProducto(it)
            }
            Spacer(modifier = Modifier.height(8.dp))

            ProductoListaUI(
                productos = uiState.productos,
                productosVm = productosVm,
                onCheckedChange = { updatedProducto ->
                    productosVm.cambiarEstadoProducto(updatedProducto.id, updatedProducto.comprado)
                },
                onDelete = {
                    productosVm.eliminarProducto(it)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormUI(
    onClickAgregarProducto: (producto: String) -> Unit
) {
    val contexto = LocalContext.current
    val textTaskPlaceholder = contexto.getString(R.string.producto_form_ejemplo)
    val textoagrega = contexto.getString(R.string.producto_form_agregar)

    var descripcionProducto by rememberSaveable { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Color(0xFF6200EE), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp)
        ) {
            TextField(
                value = descripcionProducto,
                onValueChange = { descripcionProducto = it },
                placeholder = { Text(textTaskPlaceholder) },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFF6200EE)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        IconButton(
            onClick = {
                if (descripcionProducto.isNotBlank()) {
                    onClickAgregarProducto(descripcionProducto.trim())
                    descripcionProducto = ""
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = textoagrega,
                modifier = Modifier.size(30.dp),
                tint = Color(0xFF6200EE) // Color morado para el ícono
            )
        }
    }
}



@Composable
fun ProductoListaUI(
    productos:List<Producto>,
    productosVm: ProductosViewModel,
    onCheckedChange: (Producto) -> Unit = { updatedProducto ->
        productosVm.cambiarEstadoProducto(updatedProducto.id, updatedProducto.comprado)
    },
    onDelete: (Producto) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(productos) { producto ->
            ProductoListaItemUI(
                producto = producto,
                onCheckedChange = { updatedProducto ->
                    onCheckedChange(updatedProducto)
                },
                onDelete = onDelete
            )
        }
    }
}

@Composable
fun ProductoListaItemUI(
    producto: Producto,
    onCheckedChange: (Producto) -> Unit,
    onDelete: (Producto) -> Unit
) {
    val contexto = LocalContext.current
    val textoEliminarProducto = contexto.getString(R.string.producto_form_eliminar)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = producto.descripcion,
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(2.0f)
                    .padding(10.dp, 8.dp),
                textDecoration = if (producto.comprado) TextDecoration.LineThrough else TextDecoration.None

            )
            Checkbox(
                checked = producto.comprado,
                onCheckedChange = { isChecked ->
                    onCheckedChange(producto.copy(comprado = isChecked))
                }
            )
            IconButton(onClick = {
                Log.v("ProductoListaItemUI::IconButton", "onClick DELETE")
                onDelete(producto)
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = textoEliminarProducto,
                    tint = Color.Black,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
        HorizontalDivider()
    }
}