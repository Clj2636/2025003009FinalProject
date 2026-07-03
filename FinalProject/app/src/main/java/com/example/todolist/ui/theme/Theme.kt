package com.example.todolist.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.todolist.TodoApplication

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = SurfaceLight,
    surface = SurfaceLight
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = SurfaceDark,
    surface = SurfaceDark
)

@Composable
fun TodoListTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as TodoApplication
    val isDarkMode by app.appContainer.userPreferencesRepository.isDarkMode
        .collectAsState(initial = false)

    val systemDark = isSystemInDarkTheme()
    val darkTheme = isDarkMode || systemDark

    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val dynamicColor = if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
            dynamicColor
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
