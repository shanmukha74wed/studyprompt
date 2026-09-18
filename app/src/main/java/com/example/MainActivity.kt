package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StudyPromptViewModel
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.GeneratedPromptSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ImprovePromptScreen
import com.example.ui.screens.MyPromptsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.StudyPromptTheme

class MainActivity : ComponentActivity() {

    private val viewModel: StudyPromptViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themePreference by viewModel.themeMode.collectAsState()

            StudyPromptTheme(themePreference = themePreference) {
                StudyPromptApp(viewModel = viewModel)
            }
        }
    }
}

data class NavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun StudyPromptApp(viewModel: StudyPromptViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showSheet by viewModel.showGeneratedSheet.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Listen for snackbar messages (e.g. "Prompt copied! 🚀")
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val navItems = listOf(
        NavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
        NavItem("my_prompts", "My Prompts", Icons.Filled.Folder, Icons.Outlined.Folder, "nav_my_prompts"),
        NavItem("favorites", "Favorites", Icons.Filled.Star, Icons.Outlined.StarOutline, "nav_favorites"),
        NavItem("improve", "Improve ✨", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_improve"),
        NavItem("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars,
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                navItems.forEach { item ->
                    val isSelected = currentTab == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(item.route) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryIndigo,
                            selectedTextColor = PrimaryIndigo,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tab_transition") { tab ->
                when (tab) {
                    "home" -> HomeScreen(viewModel = viewModel)
                    "my_prompts" -> MyPromptsScreen(viewModel = viewModel)
                    "favorites" -> FavoritesScreen(viewModel = viewModel)
                    "improve" -> ImprovePromptScreen(viewModel = viewModel)
                    "settings" -> SettingsScreen(viewModel = viewModel)
                    else -> HomeScreen(viewModel = viewModel)
                }
            }

            // Bottom sheet displaying the generated prompt
            if (showSheet) {
                GeneratedPromptSheet(
                    viewModel = viewModel,
                    onDismiss = { viewModel.dismissGeneratedSheet() }
                )
            }
        }
    }
}
