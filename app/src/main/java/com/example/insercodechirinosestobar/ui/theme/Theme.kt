package com.example.insercodechirinosestobar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores de estado usados en toda la app (iguales en claro y oscuro)
object InsertColors {
    val Teal = Color(0xFF14B8A6)      // acción principal
    val Ambar = Color(0xFFF5B83D)     // pendiente / cotizado
    val Celeste = Color(0xFF38BDF8)   // en ruta / en despacho
    val Verde = Color(0xFF4ADE80)     // entregado / activo
    val Coral = Color(0xFFFB7185)     // cancelado / error
    val Lila = Color(0xFFA78BFA)      // supervisor / acentos
}

// Paleta propia: verde bosque oscuro + teal + ámbar (distinta al azul marino de la versión anterior)
private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFF14B8A6),
    onPrimary = Color(0xFF042F2A),
    primaryContainer = Color(0xFF0F3F39),
    onPrimaryContainer = Color(0xFFCCFBF1),
    secondary = Color(0xFFF5B83D),
    onSecondary = Color(0xFF2B1D00),
    tertiary = Color(0xFFA78BFA),
    error = Color(0xFFFB7185),
    background = Color(0xFF0C1411),
    onBackground = Color(0xFFE8F3EF),
    surface = Color(0xFF121D19),
    onSurface = Color(0xFFE8F3EF),
    surfaceVariant = Color(0xFF1A2924),
    onSurfaceVariant = Color(0xFF93ABA2),
    outline = Color(0xFF2D4039),
    outlineVariant = Color(0xFF22332D)
)

private val EsquemaClaro = lightColorScheme(
    primary = Color(0xFF0F766E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF042F2A),
    secondary = Color(0xFFB45309),
    tertiary = Color(0xFF7C3AED),
    error = Color(0xFFE11D48),
    background = Color(0xFFF2F7F5),
    surface = Color.White,
    surfaceVariant = Color(0xFFE1ECE8),
    onSurfaceVariant = Color(0xFF45605A),
    outline = Color(0xFFB7CBC5),
    outlineVariant = Color(0xFFCFDDD8)
)

@Composable
fun InserCodeTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        content = content
    )
}
