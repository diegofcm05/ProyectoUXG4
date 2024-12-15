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

@Composable
fun FavoriteScreen(navController: NavHostController, username: String) {
    val coroutineScope = rememberCoroutineScope()
    var favoriteMovies by remember { mutableStateOf(emptyList<Pair<String, String?>>()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val favoritesResponse = MovieApi.getFavoriteMovies(username)
                favoriteMovies = favoritesResponse.map { movie ->
                    Pair("https://image.tmdb.org/t/p/w500${movie.id}.jpg", movie.nombre)
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
            text = "Favorites",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favoriteMovies.isNotEmpty()) {
            MovieCarousel(title = "Your Favorite Movies", movies = favoriteMovies)
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorite movies found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

