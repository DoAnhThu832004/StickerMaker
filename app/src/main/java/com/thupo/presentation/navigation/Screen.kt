package com.thupo.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Intro : Screen("intro")
    object Home : Screen("home")
    object PackDetail : Screen("pack_detail/{packId}") {
        fun createRoute(packId: Long) = "pack_detail/$packId"
    }
}