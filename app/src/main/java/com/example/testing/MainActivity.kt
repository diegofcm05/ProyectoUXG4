package com.example.testing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.background
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import Screens.MainScreen
import Screens.LoginScreen
import Screens.MovieScreen
import Screens.CreateUserScreen

// APP MAIN!!!!
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}


//General Cosmetics!!!

val CoolFont = FontFamily(
    Font(R.font.oswald_regular, weight = FontWeight.Normal)
)

val CustomTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = CoolFont,
        fontSize = 16.sp,
        color = Color.White
    ),
    headlineLarge = TextStyle(
        fontFamily = CoolFont,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        color = Color.Red
    )
)

private val NetflixColorScheme = darkColorScheme(
    primary = Color.Red,
    onPrimary = Color.White,
    background = Color.Black,
    surface = Color.Black,
    onBackground = Color.White,
    error = Color.Red,
    onError = Color.White
)

@Composable
fun NetflixTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NetflixColorScheme,
        typography = CustomTypography,
        content = content
    )
}

// Navigator for Screen Changes!!!!

@Composable
fun MyApp() {
    NetflixTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") { LoginScreen(navController) }
                composable("main") { MainScreen(navController) }
                composable("movie") { MovieScreen(navController) }
                composable("create_user") { CreateUserScreen(navController) }
            }
        }
    }
}

