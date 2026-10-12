package com.thupo.presentation.navigation

sealed class Screen(val route: String) {
    object Splash: Screen("splash")
}