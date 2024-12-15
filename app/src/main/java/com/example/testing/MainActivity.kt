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
import Screens.UserRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import io.ktor.client.plugins.logging.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}

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

data class MovieResult(
    val id: Int,
    val title: String,
    val overview: String?,
    val poster_path: String?,
    val release_date: String?,
    val vote_average: Double?,
    val vote_count: Int?,
    val popularity: Double?
)

data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String?,
    val poster_path: String?,
    val release_date: String?,
    val vote_average: Double?,
    val runtime: Int?,
    val genres: List<String>?
)

@Composable
fun MyApp() {
    NetflixTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            val navController = rememberNavController()
            var currentUsername by remember { mutableStateOf("") }

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LoginScreen(navController) { username ->
                        currentUsername = username
                        navController.navigate("main")
                    }
                }
                composable("main") { MainScreen(navController, username = currentUsername) }
                composable(
                    route = "movie/{movieId}",
                    arguments = listOf(navArgument("movieId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
                    MovieScreen(navController, movieId, username = currentUsername)
                }
                composable("create_user") { CreateUserScreen(navController) }
            }
        }
    }
}

@Serializable
data class Movie(val id: Int, val nombre: String)

@Serializable
data class UserRequest(val username: String, val password: String)


object MovieApi {
    private const val BASE_URL = "https://a202-190-92-11-185.ngrok-free.app"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        install(io.ktor.client.plugins.logging.Logging) {
            level = io.ktor.client.plugins.logging.LogLevel.ALL
        }
    }

    suspend fun registerUser(request: UserRequest): Boolean {
        try {
            val response = client.post("$BASE_URL/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            println("Register Response: $response")
            return response.body()
        } catch (e: Exception) {
            println("Register Error: ${e.message}")
            throw e
        }
    }

    suspend fun loginUser(request: UserRequest): Boolean {
        return try {
            println("Sending Login Request: $request")
            val response = client.post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            println("Response Status: ${response.status}")
            val responseBody = response.body<String>()
            println("Response Body: $responseBody")


            responseBody.contains("true")
        } catch (e: Exception) {
            println("Error during login: ${e.message}")
            false
        }
    }

    suspend fun getUser(username: String): Map<String, Any> {
        return client.get("$BASE_URL/user/$username") {
            contentType(io.ktor.http.ContentType.Application.Json)
        }.body()
    }

    suspend fun deleteUser(username: String): Boolean {
        return client.delete("$BASE_URL/user/$username") {
            contentType(io.ktor.http.ContentType.Application.Json)
        }.body()
    }

    suspend fun addMovieToUser(username: String, movie: Movie): Boolean {
        return client.post("$BASE_URL/user/$username/movie") {
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody(movie)
        }.body()
    }

    suspend fun removeMovieFromUser(username: String, movieId: Int): Boolean {
        return client.delete("$BASE_URL/user/$username/movie/$movieId") {
            contentType(io.ktor.http.ContentType.Application.Json)
        }.body()
    }

    suspend fun getFavoriteMovies(username: String): List<Movie> {
        return client.get("$BASE_URL/user/$username/favorites") {
            contentType(io.ktor.http.ContentType.Application.Json)
        }.body()
    }

    suspend fun getPopularMovies(): List<Movie> {
        return client.get("$BASE_URL/movies/popular") {
            contentType(io.ktor.http.ContentType.Application.Json)
        }.body()
    }

    suspend fun getTopRatedMovies(): List<Movie> {
        return client.get("$BASE_URL/movies/top_rated") {
            contentType(io.ktor.http.ContentType.Application.Json)
        }.body()
    }

    suspend fun getUpcomingMovies(): List<Movie> {
        return client.get("$BASE_URL/movies/upcoming") {
            contentType(io.ktor.http.ContentType.Application.Json)
        }.body()
    }

    suspend fun getNowPlayingMovies(): List<Movie> {
        return client.get("$BASE_URL/movies/now_playing") {
            contentType(io.ktor.http.ContentType.Application.Json)
        }.body()
    }

    suspend fun getAnimeMovies(): List<Movie> {
        return client.get("$BASE_URL/movies/anime") {
            contentType(io.ktor.http.ContentType.Application.Json)
        }.body()
    }

    suspend fun getAnimeTvShows(): List<Movie> {
        return client.get("$BASE_URL/tv/anime") {
            contentType(io.ktor.http.ContentType.Application.Json)
        }.body()
    }

    suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return client.get("$BASE_URL/movies/$movieId") {
            contentType(io.ktor.http.ContentType.Application.Json)
        }.body()
    }
}

