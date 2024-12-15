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
import androidx.compose.foundation.lazy.LazyColumn
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



@Composable
fun SearchScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<MovieResult>()) }

    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    val results = MovieApi.getPopularMovies() as List<MovieResult> // Replace with actual search API
                    searchResults = results.filter {
                        it.title.contains(query, ignoreCase = true)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            searchResults = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BasicTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(searchResults.size) { index ->
                val movie = searchResults[index]
                MovieCard(
                    imageUrl = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                    title = movie.title
                )

                Spacer(modifier = Modifier.height(8.dp))

                Divider(color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}
