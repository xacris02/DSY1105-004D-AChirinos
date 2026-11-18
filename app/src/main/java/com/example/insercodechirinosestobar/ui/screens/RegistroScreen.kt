package com.example.insercodechirinosestobar.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insercodechirinosestobar.model.RegistroErrores
import com.example.insercodechirinosestobar.model.RegistroUiState
import com.example.insercodechirinosestobar.model.Rol
import com.example.insercodechirinosestobar.ui.components.BotonGoogle
import com.example.insercodechirinosestobar.ui.components.Etiqueta
import com.example.insercodechirinosestobar.ui.theme.InsertColors
import com.example.insercodechirinosestobar.viewmodel.LoginViewModel

@Composable
fun RegistroScreen(
    viewModel: LoginViewModel,
    onRegistroExitoso: (Rol) -> Unit,
    onVolverLogin: () -> Unit
) {
    val estado by viewModel.registro.collectAsState()
    val errores by viewModel.erroresRegistro.collectAsState()
    val sesion by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Si hay rol, el registro fue exitoso
    LaunchedEffect(sesion.rol) {
        sesion.rol?.let { onRegistroExitoso(it) }
    }

    RegistroContenido(
        estado = estado,
        errores = errores,
        onNombreChange = viewModel::onNombreRegistroChange,
        onPasswordChange = viewModel::onPasswordRegistroChange,
        onConfirmarChange = viewModel::onConfirmarPasswordChange,
        onRegistrar = viewModel::registrar,
        onRegistrarConGoogle = { viewModel.registrarConGoogle(context) },
        onVolverLogin = onVolverLogin
    )
}

@Composable
fun RegistroContenido(
    estado: RegistroUiState,
    errores: RegistroErrores,
    onNombreChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmarChange: (String) -> Unit,
    onRegistrar: () -> Unit,
    onRegistrarConGoogle: () -> Unit,
    onVolverLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Etiqueta("Nueva cuenta", InsertColors.Teal)
            Spacer(Modifier.height(6.dp))
            Text("Crear cuenta", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
            Text(
                "Las cuentas nuevas parten como Vendedor; el Administrador puede cambiar tu rol.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = estado.nombre,
                onValueChange = onNombreChange,
                label = { Text("Nombre") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                isError = errores.nombre != null,
                supportingText = { errores.nombre?.let { Text(it) } },
                singleLine = true,
                enabled = !estado.isLoading,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estado.password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                isError = errores.password != null,
                supportingText = { errores.password?.let { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !estado.isLoading,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estado.confirmarPassword,
                onValueChange = onConfirmarChange,
                label = { Text("Confirmar contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                isError = errores.confirmarPassword != null,
                supportingText = { errores.confirmarPassword?.let { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !estado.isLoading,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            estado.mensajeError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }

            Button(
                onClick = onRegistrar,
                enabled = !estado.isLoading,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                if (estado.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Registrarse", fontWeight = FontWeight.Bold)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("  O CONTINUAR CON  ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            BotonGoogle(texto = "Registrarse con Google", cargando = estado.isLoading, onClick = onRegistrarConGoogle)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("¿Ya tienes cuenta?", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = onVolverLogin) { Text("Iniciar sesión", fontWeight = FontWeight.Bold) }
            }
        }
    }
}
