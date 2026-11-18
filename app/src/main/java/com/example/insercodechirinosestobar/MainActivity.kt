package com.example.insercodechirinosestobar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.insercodechirinosestobar.navigation.AppRoutes
import com.example.insercodechirinosestobar.navigation.NavigationEvent
import com.example.insercodechirinosestobar.navigation.NavigationViewModel
import com.example.insercodechirinosestobar.navigation.rutaInicio
import com.example.insercodechirinosestobar.navigation.rutasPestana
import com.example.insercodechirinosestobar.ui.components.EncabezadoApp
import com.example.insercodechirinosestobar.ui.components.MenuInferior
import com.example.insercodechirinosestobar.ui.screens.CatalogoScreen
import com.example.insercodechirinosestobar.ui.screens.ClientesScreen
import com.example.insercodechirinosestobar.ui.screens.DashboardScreen
import com.example.insercodechirinosestobar.ui.screens.LoginScreen
import com.example.insercodechirinosestobar.ui.screens.PedidosScreen
import com.example.insercodechirinosestobar.ui.screens.PerfilScreen
import com.example.insercodechirinosestobar.ui.screens.PostScreen
import com.example.insercodechirinosestobar.ui.screens.RegistroScreen
import com.example.insercodechirinosestobar.ui.screens.RutaScreen
import com.example.insercodechirinosestobar.ui.screens.UsuariosScreen
import com.example.insercodechirinosestobar.ui.theme.InserCodeTheme
import com.example.insercodechirinosestobar.viewmodel.AjustesViewModel
import com.example.insercodechirinosestobar.viewmodel.LoginViewModel
import com.example.insercodechirinosestobar.viewmodel.OperacionViewModel
import com.example.insercodechirinosestobar.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val ajustesViewModel: AjustesViewModel = viewModel()
            val ajustes by ajustesViewModel.ajustes.collectAsState()

            InserCodeTheme(darkTheme = ajustes.modoOscuro) {
                val navController = rememberNavController()
                val navigationViewModel: NavigationViewModel = viewModel()
                // Mismo LoginViewModel para Login, Registro y Perfil (guarda el rol de la sesión)
                val loginViewModel: LoginViewModel = viewModel()
                // Productos, clientes, pedidos, entregas y usuarios (Room), compartidos entre pantallas
                val operacionViewModel: OperacionViewModel = viewModel()
                val datos by operacionViewModel.datos.collectAsState()
                val sesion by loginViewModel.uiState.collectAsState()
                val rol = sesion.rol
                val rutaActual = navController.currentBackStackEntryAsState().value?.destination?.route

                // Ejecuta los eventos de navegación
                LaunchedEffect(Unit) {
                    navigationViewModel.navigationEvents.collect { evento ->
                        when (evento) {
                            is NavigationEvent.NavigateTo -> navController.navigate(evento.route) {
                                launchSingleTop = true
                                evento.popUpTo?.let { popUpTo(it) { inclusive = evento.inclusive } }
                            }
                            NavigationEvent.NavigateBack -> navController.popBackStack()
                        }
                    }
                }

                // Las pestañas limpian la pila hasta la pantalla de inicio del rol
                fun irA(ruta: String) {
                    if (ruta in rutasPestana && rol != null) {
                        navigationViewModel.navigateTo(ruta, popUpTo = rutaInicio(rol), inclusive = false)
                    } else {
                        navigationViewModel.navigateTo(ruta)
                    }
                }

                // Entra a la pantalla de inicio del rol y quita Login/Registro de la pila
                fun entrar(nuevoRol: com.example.insercodechirinosestobar.model.Rol) {
                    navigationViewModel.navigateTo(rutaInicio(nuevoRol), popUpTo = AppRoutes.Login.route, inclusive = true)
                }

                // Cerrar sesión: limpia la sesión y la pila completa, y vuelve al Login
                fun salir() {
                    loginViewModel.cerrarSesion()
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                val mostrarMenu = rol != null && rutaActual in rutasPestana

                Scaffold(
                    // Cada pantalla maneja sus márgenes del sistema; este Scaffold solo aporta barras
                    contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
                    topBar = { if (mostrarMenu && rol != null) EncabezadoApp(rol = rol, onSalir = { salir() }) },
                    bottomBar = {
                        if (mostrarMenu && rol != null) {
                            MenuInferior(rol = rol, rutaActual = rutaActual, onSeleccionar = { ruta -> irA(ruta) })
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppRoutes.Login.route,
                        modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)
                    ) {
                        composable(AppRoutes.Login.route) {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginExitoso = { r -> entrar(r) },
                                onCrearCuenta = { navigationViewModel.navigateTo(AppRoutes.Registro.route) }
                            )
                        }
                        composable(AppRoutes.Registro.route) {
                            RegistroScreen(
                                viewModel = loginViewModel,
                                onRegistroExitoso = { r -> entrar(r) },
                                onVolverLogin = { navigationViewModel.navigateBack() }
                            )
                        }

                        // ----- Pestañas (cada rol ve solo las de su menú) -----
                        composable(AppRoutes.Dashboard.route) {
                            val r = rol ?: return@composable
                            DashboardScreen(rol = r, email = sesion.email, datos = datos)
                        }
                        composable(AppRoutes.Pedidos.route) {
                            val r = rol ?: return@composable
                            PedidosScreen(
                                rol = r,
                                email = sesion.email,
                                datos = datos,
                                onCambiarEstado = { pedido, estado -> operacionViewModel.cambiarEstadoPedido(pedido, estado) }
                            )
                        }
                        composable(AppRoutes.Catalogo.route) {
                            CatalogoScreen(
                                email = sesion.email,
                                datos = datos,
                                onCotizar = { cliente, producto, cantidad ->
                                    operacionViewModel.crearPedido(cliente, producto, cantidad, sesion.email)
                                    irA(AppRoutes.Pedidos.route)
                                }
                            )
                        }
                        composable(AppRoutes.Clientes.route) {
                            ClientesScreen(datos = datos)
                        }
                        composable(AppRoutes.Ruta.route) {
                            val r = rol ?: return@composable
                            RutaScreen(
                                rol = r,
                                email = sesion.email,
                                datos = datos,
                                onActualizar = { entrega, estado ->
                                    operacionViewModel.actualizarEntrega(entrega, estado, sesion.email)
                                }
                            )
                        }
                        composable(AppRoutes.Usuarios.route) {
                            UsuariosScreen(
                                emailActual = sesion.email,
                                datos = datos,
                                onCambiarRol = { u, nuevo -> operacionViewModel.cambiarRol(u, nuevo) },
                                onAlternarActivo = { u -> operacionViewModel.alternarActivo(u) },
                                onCrear = { email, nombre, nuevo -> operacionViewModel.crearUsuarioPrueba(email, nombre, nuevo) }
                            )
                        }
                        composable(AppRoutes.Perfil.route) {
                            val r = rol ?: return@composable
                            PerfilScreen(
                                nombre = sesion.nombre,
                                email = sesion.email,
                                rol = r,
                                notificaciones = ajustes.notificacionesPush,
                                modoOscuro = ajustes.modoOscuro,
                                onNotificaciones = ajustesViewModel::cambiarPush,
                                onModoOscuro = ajustesViewModel::cambiarModoOscuro,
                                onVerPosts = { navigationViewModel.navigateTo(AppRoutes.Posts.route) },
                                onSalir = { salir() }
                            )
                        }

                        composable(AppRoutes.Posts.route) {
                            // ViewModel propio de la pantalla (se descarta al volver)
                            val postViewModel: PostViewModel = viewModel()
                            PostScreen(
                                viewModel = postViewModel,
                                onVolver = { navigationViewModel.navigateBack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
