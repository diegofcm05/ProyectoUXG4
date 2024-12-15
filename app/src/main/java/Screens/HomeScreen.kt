package Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role.Companion.Image
import coil.compose.rememberImagePainter
import com.example.testing.MovieApi
import kotlinx.coroutines.launch
import com.example.testing.TmdbResponse
import com.example.testing.MovieResult


@Composable
fun HomeScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var popularMovies by remember { mutableStateOf(emptyList<MovieResult>()) }
    var topRatedMovies by remember { mutableStateOf(emptyList<MovieResult>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                popularMovies = MovieApi.getPopularMovies()
                topRatedMovies = MovieApi.getTopRatedMovies()
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
                MovieCarousel(title = "Popular Movies", movies = popularMovies, navController = navController)
            } else {
                Text("No popular movies available.", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (topRatedMovies.isNotEmpty()) {
                MovieCarousel(title = "Top-Rated Movies", movies = topRatedMovies, navController = navController)
            } else {
                Text("No top-rated movies available.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun MovieCarousel(title: String, movies: List<MovieResult>, navController: NavHostController) {
    Column {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow {
            items(movies) { movie ->
                MovieItem(movie, navController)
            }
        }
    }
}

@Composable
fun MovieItem(movie: MovieResult, navController: NavHostController) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(140.dp)
            .clickable { navController.navigate("movie/${movie.id}") } // Navigate to MovieScreen
    ) {
        movie.posterPath?.let { path ->
            Image(
                painter = rememberImagePainter("https://image.tmdb.org/t/p/w500$path"),
                contentDescription = movie.title,
                modifier = Modifier.height(200.dp)
            )
        }
        Text(text = movie.title, style = MaterialTheme.typography.bodyLarge)
    }
}