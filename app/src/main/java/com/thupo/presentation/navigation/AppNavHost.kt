package com.thupo.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.thupo.presentation.screens.pack_detail.PackDetailScreen
import com.thupo.presentation.screens.home.HomeScreen
import com.thupo.presentation.screens.intro.IntroScreen
import com.thupo.presentation.screens.intro.components.AuroraGlow
import com.thupo.presentation.screens.splash.SplashDestinationTransition
import com.thupo.presentation.screens.splash.SplashUiState
import com.thupo.presentation.screens.splash.SplashViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    splashViewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by splashViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is SplashUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AuroraGlow()
            }
        }
        is SplashUiState.Ready -> {
            SplashDestinationTransition(
                isIntroDestination = state.navigationToIntro,
            ) {
                NavHost(
                    navController = navController,
                    startDestination = if (state.navigationToIntro) Screen.Intro.route else Screen.Home.route
                ) {
                    composable(Screen.Intro.route) {
                        IntroScreen(
                            onNavigateToHome = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Intro.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onNavigateToPackDetail = { packId ->
                                navController.navigate(Screen.PackDetail.createRoute(packId))
                            }
                        )
                    }
                    composable(
                        route = Screen.PackDetail.route,
                        arguments = listOf(
                            navArgument("packId") { type = NavType.LongType }
                        )
                    ) {
                        PackDetailScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
        is SplashUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { splashViewModel.onRetry() }) {
                        Text(text = "Thử lại")
                    }
                }
            }
        }
    }
}