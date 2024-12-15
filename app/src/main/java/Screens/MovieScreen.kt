package Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import kotlinx.coroutines.launch
import com.example.testing.MovieApi
import com.example.testing.MovieResult
import com.example.testing.MovieDetails
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberAsyncImagePainter
import com.example.testing.Movie


// This will be alter later, once we connect to the API, this is just a basis!!!!!
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(navController: NavHostController, movieId: Int, username: String) {
    val coroutineScope = rememberCoroutineScope()
    var movieDetails by remember { mutableStateOf<MovieDetails?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch movie details and favorite status
    LaunchedEffect(movieId) {
        coroutineScope.launch {
            try {
                // Fetch movie details
                movieDetails = MovieApi.getMovieDetails(movieId)

                // Check if the movie is in the user's favorites
                val favorites = MovieApi.getFavoriteMovies(username)
                isFavorite = favorites.any { it.id == movieId }
            } catch (e: Exception) {
                errorMessage = "Error fetching movie details: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (movieDetails != null) {
                val details = movieDetails!!

                // Movie Poster
                Image(
                    painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${details.poster_path}"),
                    contentDescription = details.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Movie Title
                Text(
                    text = details.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Like/Unlike Button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                if (isFavorite) {
                                    // Remove from favorites
                                    MovieApi.removeMovieFromUser(username, movieId)
                                } else {
                                    // Add to favorites
                                    MovieApi.addMovieToUser(username, Movie(id = movieId, nombre = details.title))
                                }
                                isFavorite = !isFavorite
                            } catch (e: Exception) {
                                errorMessage = "Error updating favorites: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isFavorite) "Unlike" else "Like")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Movie Overview
                details.overview?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                if (errorMessage != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage ?: "Loading...",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}


