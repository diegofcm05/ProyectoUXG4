package Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import Screens.HomeScreen
import Screens.SearchScreen
import Screens.FavoriteScreen
import androidx.navigation.NavHostController

@Composable
fun MainScreen(navController: NavHostController) {
    var selectedScreen by remember { mutableStateOf("Home") }

    Scaffold(
        topBar = { TopNavBar(navController) },
        bottomBar = { BottomNavBar(selectedScreen) { selectedScreen = it } }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedScreen) {
                "Search" -> SearchScreen(navController)
                "Home" -> HomeScreen(navController)
                "Favorites" -> FavoriteScreen(navController)
            }
        }
    }
}

@Composable
fun BottomNavBar(
    currentScreen: String,
    onScreenSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = currentScreen == "Search",
            onClick = { onScreenSelected("Search") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentScreen == "Home",
            onClick = { onScreenSelected("Home") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
            label = { Text("Favorites") },
            selected = currentScreen == "Favorites",
            onClick = { onScreenSelected("Favorites") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(navController: NavHostController) {
    TopAppBar(
        title = { Text("Cinemaddicts") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            Button(
                onClick = { navController.navigate("login") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Log Out")
            }
        }
    )
}