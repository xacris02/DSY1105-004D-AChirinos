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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insercodechirinosestobar.model.LoginErrores
import com.example.insercodechirinosestobar.model.LoginUiState
import com.example.insercodechirinosestobar.model.Rol
import com.example.insercodechirinosestobar.ui.components.BotonGoogle
import com.example.insercodechirinosestobar.ui.components.Etiqueta
import com.example.insercodechirinosestobar.ui.theme.InsertColors
import com.example.insercodechirinosestobar.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginExitoso: (Rol) -> Unit,
    onCrearCuenta: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()
    val errores by viewModel.errores.collectAsState()
    val context = LocalContext.current

    // Si hay rol, el acceso fue exitoso (correo o Google)
    LaunchedEffect(estado.rol) {
        estado.rol?.let { onLoginExitoso(it) }
    }

    LoginContenido(
        estado = estado,
        errores = errores,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRecordarmeChange = viewModel::onRecordarmeChange,
        onOlvidaste = viewModel::restablecerPassword,
        onIngresar = viewModel::iniciarSesion,
        onGoogle = { viewModel.iniciarConGoogle(context) },
        onCrearCuenta = onCrearCuenta
    )
}

@Composable
fun LoginContenido(
    estado: LoginUiState,
    errores: LoginErrores,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRecordarmeChange: (Boolean) -> Unit,
    onOlvidaste: () -> Unit,
    onIngresar: () -> Unit,
    onGoogle: () -> Unit,
    onCrearCuenta: () -> Unit
) {
    var verPassword by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Etiqueta("Acceso Seguro", InsertColors.Verde)
            Spacer(Modifier.height(6.dp))
            Text("Bienvenido de vuelta", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
            Text("Ingresa a tu turno de ventas y distribución", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))

            CampoLogin(
                titulo = "CORREO ELECTRÓNICO",
                valor = estado.email,
                onCambio = onEmailChange,
                icono = { Icon(Icons.Filled.Email, contentDescription = null) },
                error = errores.email,
                tipo = KeyboardType.Email,
                habilitado = !estado.isLoading
            )
            CampoLogin(
                titulo = "CONTRASEÑA",
                valor = estado.password,
                onCambio = onPasswordChange,
                icono = { Icon(Icons.Filled.Lock, contentDescription = null) },
                error = errores.password,
                tipo = KeyboardType.Password,
                habilitado = !estado.isLoading,
                transformacion = if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailing = {
                    IconButton(onClick = { verPassword = !verPassword }) {
                        Icon(
                            if (verPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (verPassword) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            )

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = estado.recordarme, onCheckedChange = onRecordarmeChange)
                Text("Recordarme", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                TextButton(onClick = onOlvidaste, enabled = !estado.isLoading) { Text("¿Olvidaste tu contraseña?") }
            }

            estado.mensajeError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            estado.mensajeInfo?.let {
                Text(it, color = InsertColors.Verde, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }

            Button(
                onClick = onIngresar,
                enabled = estado.isLoginEnabled,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                if (estado.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Ingresar al sistema", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.size(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
            Text(
                "Tus permisos se cargarán automáticamente según tu perfil",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("  O CONTINUAR CON  ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            // Solo Google (sin Apple)
            BotonGoogle(texto = "Google", cargando = estado.isLoading, onClick = onGoogle)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("¿No tienes cuenta?", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = onCrearCuenta) { Text("Crear cuenta", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun CampoLogin(
    titulo: String,
    valor: String,
    onCambio: (String) -> Unit,
    icono: @Composable () -> Unit,
    error: String?,
    tipo: KeyboardType,
    habilitado: Boolean,
    transformacion: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(titulo, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            leadingIcon = icono,
            trailingIcon = trailing,
            isError = error != null,
            supportingText = { error?.let { Text(it) } },
            singleLine = true,
            enabled = habilitado,
            visualTransformation = transformacion,
            keyboardOptions = KeyboardOptions(keyboardType = tipo),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
