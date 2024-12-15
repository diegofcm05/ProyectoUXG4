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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController

@Composable
fun MainScreen(navController: NavHostController, username: String) {
    var selectedScreen by rememberSaveable { mutableStateOf("Home") }

    Scaffold(
        topBar = { TopNavBar(navController, selectedScreen) },
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
                "Favorites" -> FavoriteScreen(navController, username)
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
            icon = { Icon(Icons.Default.Search, contentDescription = "Navigate to Search Screen") },
            label = { Text("Search") },
            selected = currentScreen == "Search",
            onClick = { onScreenSelected("Search") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Navigate to Home Screen") },
            label = { Text("Home") },
            selected = currentScreen == "Home",
            onClick = { onScreenSelected("Home") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Navigate to Favorites Screen") },
            label = { Text("Favorites") },
            selected = currentScreen == "Favorites",
            onClick = { onScreenSelected("Favorites") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(navController: NavHostController, currentScreen: String) {
    fun getScreenTitle(screen: String): String = when (screen) {
        "Search" -> "Search Movies"
        "Home" -> "Home"
        "Favorites" -> "Your Favorites"
        else -> "Cinemaddicts"
    }

    TopAppBar(
        title = { Text(getScreenTitle(currentScreen)) },
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