package Screens

import Molecules.MovieCarousel
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import Molecules.MovieCarousel
import com.example.testing.MovieApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import Molecules.MovieCarousel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import com.example.testing.MovieResult
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController


@Composable
fun FavoriteScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var favoriteMovies by remember { mutableStateOf(emptyList<MovieResult>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                favoriteMovies = MovieApi.getFavoriteMovies("yourUsernameHere") // Replace with dynamic username
            } catch (e: Exception) {
                errorMessage = "Failed to load favorite movies: ${e.message}"
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
            text = "Your Favorite Movies",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            if (favoriteMovies.isNotEmpty()) {
                MovieCarousel(
                    title = "Favorite Movies",
                    movies = favoriteMovies,
                    navController = navController
                )
            } else {
                Text("No favorite movies available.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

