package Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import Molecules.MovieCarousel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.testing.MovieApi
import kotlinx.coroutines.launch
import com.example.testing.MovieResult


@Composable
fun HomeScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var popularMovies by remember { mutableStateOf(emptyList<Pair<String, String?>>()) }
    var topRatedMovies by remember { mutableStateOf(emptyList<Pair<String, String?>>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val popularResponse = MovieApi.getPopularMovies() as List<MovieResult>
                popularMovies = popularResponse.mapNotNull {
                    it.poster_path?.let { path -> Pair("https://image.tmdb.org/t/p/w500$path", it.title) }
                }

                val topRatedResponse = MovieApi.getTopRatedMovies() as List<MovieResult>
                topRatedMovies = topRatedResponse.mapNotNull {
                    it.poster_path?.let { path -> Pair("https://image.tmdb.org/t/p/w500$path", it.title) }
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load movies: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Welcome to the Home Screen",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            if (popularMovies.isNotEmpty()) {
                MovieCarousel(title = "Popular Movies", movies = popularMovies)
            } else {
                Text("No popular movies available.", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (topRatedMovies.isNotEmpty()) {
                MovieCarousel(title = "Top-Rated Movies", movies = topRatedMovies)
            } else {
                Text("No top-rated movies available.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}