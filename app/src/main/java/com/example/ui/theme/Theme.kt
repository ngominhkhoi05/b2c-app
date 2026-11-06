package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),  // Tag/Badge bo 4.dp
    small = RoundedCornerShape(8.dp),       // Button, TextField bo 8.dp
    medium = RoundedCornerShape(12.dp),     // ProductCard bo 12.dp
    large = RoundedCornerShape(16.dp),      // BottomSheet 2 góc trên, Chips bo 16.dp
    extraLarge = RoundedCornerShape(24.dp)
)

private val DarkColorScheme = darkColorScheme(
    primary = CoralPrimary,
    onPrimary = Color.White,
    primaryContainer = CoralPrimaryPressed,
    onPrimaryContainer = Color.White,
    secondary = NavySecondary,
    onSecondary = Color.White,
    tertiary = FreeShippingAccent,
    background = DarkBackgroundApp,
    onBackground = DarkTextPrimary,
    surface = DarkSurfaceCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkBorderDivider,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorderDivider,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CoralPrimary,
    onPrimary = Color.White,
    primaryContainer = CoralPrimaryContainer,
    onPrimaryContainer = CoralPrimaryPressed,
    secondary = NavySecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8EEF5),
    onSecondaryContainer = NavySecondary,
    tertiary = FreeShippingAccent,
    onTertiary = Color.White,
    background = BackgroundApp,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundApp,
    onSurfaceVariant = TextSecondary,
    outline = BorderDivider,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun B2CAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Default false to enforce Concept 1 Coral & Navy palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}

// Alias for template backwards-compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    B2CAppTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
