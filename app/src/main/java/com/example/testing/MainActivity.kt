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
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

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

@Serializable
data class TmdbResponse(
    val results: List<MovieResult>
)

@Serializable
data class MovieResult(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("release_date") val releaseDate: String?,
    @SerialName("vote_average") val voteAverage: Double?,
    @SerialName("vote_count") val voteCount: Int?,
    val popularity: Double?
)

@Serializable
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("release_date") val releaseDate: String?,
    @SerialName("vote_average") val voteAverage: Double?,
    @SerialName("vote_count") val voteCount: Int?,
    val popularity: Double?,
    val runtime: Int?,
    val genres: List<Genre>?,
    val homepage: String?,
    val tagline: String?
)

@Serializable
data class Genre(
    val id: Int,
    val name: String
)

@Serializable
data class VideoResponse(
    val results: List<VideoResult>
)

@Serializable
data class VideoResult(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String
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
    private const val BASE_URL = "http://10.0.2.2:8080"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                }
            )
        }
        install(Logging) {
            level = LogLevel.ALL
        }
    }

    suspend fun registerUser(request: UserRequest): Boolean {
        return try {
            val response: HttpResponse = client.post("$BASE_URL/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val responseText = response.bodyAsText()
            println("Register Response: $responseText")

            val json = Json.decodeFromString<Map<String, Boolean>>(responseText)
            val success = json["success"] ?: false

            println("Register Success: $success")
            success
        } catch (e: Exception) {
            println("Register Error: ${e.message}")
            false
        }
    }

    @Serializable
    data class LoginResponse(
        val success: Boolean,
        val message: String
    )

    suspend fun loginUser(request: UserRequest): Boolean {
        return try {
            println("Sending Login Request: $request")

            val response: HttpResponse = client.post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val responseText = response.bodyAsText()
            println("Response Body: $responseText")

            val loginResponse = Json.decodeFromString<LoginResponse>(responseText)
            println("Login Success: ${loginResponse.success}, Message: ${loginResponse.message}")

            if (!loginResponse.success) {
                println("Login Failed: ${loginResponse.message}")
            }

            loginResponse.success
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

    suspend fun getFavoriteMovies(username: String): List<MovieResult> {
        return try {
            val response = client.get("$BASE_URL/user/$username/favorites") {
                contentType(ContentType.Application.Json)
            }
            response.body()
        } catch (e: Exception) {
            println("Error fetching favorites: ${e.message}")
            emptyList()
        }
    }

    suspend fun addFavoriteMovie(username: String, movie: MovieResult): Boolean {
        return try {
            val response = client.post("$BASE_URL/user/$username/movie") {
                contentType(ContentType.Application.Json)
                setBody(movie)
            }
            response.body<Map<String, Boolean>>()["success"] ?: false
        } catch (e: Exception) {
            println("Error adding favorite: ${e.message}")
            false
        }
    }

    suspend fun removeFavoriteMovie(username: String, movieId: Int): Boolean {
        return try {
            val response = client.delete("$BASE_URL/user/$username/movie/$movieId") {
                contentType(ContentType.Application.Json)
            }
            response.body<Map<String, Boolean>>()["success"] ?: false
        } catch (e: Exception) {
            println("Error removing favorite: ${e.message}")
            false
        }
    }

    suspend fun getPopularMovies(): List<MovieResult> {
        return try {
            println("Fetching Popular Movies...")
            val movies: List<MovieResult> = client.get("$BASE_URL/movies/popular") {
                contentType(ContentType.Application.Json)
            }.body()
            println("Popular Movies Received: $movies")
            movies
        } catch (e: Exception) {
            println("Error fetching popular movies: ${e.message}")
            emptyList()
        }
    }

    suspend fun getTopRatedMovies(): List<MovieResult> {
        return try {
            println("Fetching Top Rated Movies...")
            val movies: List<MovieResult> = client.get("$BASE_URL/movies/top_rated") {
                contentType(ContentType.Application.Json)
            }.body()
            println("Top Rated Movies Received: $movies")
            movies
        } catch (e: Exception) {
            println("Error fetching top rated movies: ${e.message}")
            emptyList()
        }
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

    suspend fun getMovieDetailsWithTrailers(movieId: Int): Pair<MovieDetails, List<VideoResult>> {
        val responseText = client.get("$BASE_URL/movies/$movieId").bodyAsText()
        println("Raw Response: $responseText")

        return try {
            val jsonObject = Json.parseToJsonElement(responseText).jsonObject
            val movieDetails = Json.decodeFromJsonElement<MovieDetails>(jsonObject["details"]!!)
            val trailers = Json.decodeFromJsonElement<List<VideoResult>>(jsonObject["trailers"]!!)
            Pair(movieDetails, trailers)
        } catch (e: Exception) {
            println("Deserialization Error: ${e.message}")
            throw e
        }
    }
}

