package Screens

import Atoms.MovieCard
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import Molecules.MovieCarousel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.testing.MovieApi
import kotlinx.coroutines.launch
import com.example.testing.MovieResult
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.testing.MovieDetails


@Composable
fun SearchScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var allMovies by remember { mutableStateOf(emptyList<MovieResult>()) }
    var filteredMovies by remember { mutableStateOf(emptyList<MovieResult>()) }
    var popularMovies by remember { mutableStateOf(emptyList<MovieResult>()) }
    var topRatedMovies by remember { mutableStateOf(emptyList<MovieResult>()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                popularMovies = MovieApi.getPopularMovies()
                topRatedMovies = MovieApi.getTopRatedMovies()
                isLoading = true
                allMovies = popularMovies + topRatedMovies
                filteredMovies = allMovies
            } catch (e: Exception) {
                errorMessage = "Failed to load movies: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(searchQuery) {
        filteredMovies = if (searchQuery.isBlank()) {
            allMovies
        } else {
            allMovies.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Movies") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            if (filteredMovies.isNotEmpty()) {
                LazyColumn {
                    items(filteredMovies) { movie ->
                        MovieCard(movie = movie, navController = navController)
                    }
                }
            } else {
                Text(
                    text = "No movies match your search.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun MovieCard(movie: MovieResult, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("movie/${movie.id}") }
    ) {
        movie.posterPath?.let { path ->
            Image(
                painter = rememberImagePainter("https://image.tmdb.org/t/p/w500$path"),
                contentDescription = movie.title,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 8.dp)
            )
        }

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = movie.overview ?: "No description available.",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
