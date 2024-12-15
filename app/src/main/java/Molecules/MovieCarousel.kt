package Molecules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import Atoms.MovieCard
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@Composable
fun MovieCarousel(title: String, movies: List<Pair<String, String?>>) {
    println("Rendering MovieCarousel: $title with ${movies.size} movies")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Start,
            color = Color.White
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movies.size) { index ->
                val movie = movies[index]
                println("Displaying Movie: ${movie.second}") // Log movie title
                MovieCard(imageUrl = movie.first, title = movie.second)
            }
        }
    }
}
