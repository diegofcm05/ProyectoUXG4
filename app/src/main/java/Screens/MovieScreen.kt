package Screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.testing.MovieApi
import com.example.testing.MovieResult
import com.example.testing.MovieDetails
import com.example.testing.VideoResult
import com.example.testing.VideoResponse
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberAsyncImagePainter
import com.example.testing.Movie


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(navController: NavHostController, movieId: Int, username: String) {
    val coroutineScope = rememberCoroutineScope()
    var movieResult by remember { mutableStateOf<MovieResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(movieId) {
        coroutineScope.launch {
            try {
                println("Fetching Movie Details...")
                val movies = MovieApi.getPopularMovies() + MovieApi.getTopRatedMovies()
                movieResult = movies.firstOrNull { it.id == movieId }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (movieResult != null) {
                val movie = movieResult!!

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    movie.posterPath?.let { path ->
                        Image(
                            painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500$path"),
                            contentDescription = movie.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    movie.releaseDate?.let {
                        Text(
                            text = "Release Date: $it",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        movie.voteAverage?.let {
                            Text(
                                text = "Rating: ${"%.1f".format(it)}/10",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        movie.voteCount?.let {
                            Text(
                                text = "Votes: $it",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    if (movieResult != null) {
                                        val movie = movieResult!!
                                        val success = MovieApi.addFavoriteMovie(username, movie)
                                        if (success) {
                                            println("Movie added to favorites successfully!")
                                        } else {
                                            println("Failed to add movie to favorites.")
                                        }
                                    }
                                } catch (e: Exception) {
                                    println("Error adding movie to favorites: ${e.message}")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add to Favorites")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    movie.overview?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

