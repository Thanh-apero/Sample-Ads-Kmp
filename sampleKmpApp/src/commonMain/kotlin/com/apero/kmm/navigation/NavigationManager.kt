package com.apero.kmm.navigation

import androidx.compose.runtime.*

enum class Screen {
    Splash,
    Main,
    Next,
    ViewPager // ViewPager screen for A-B-C flow
}

class NavigationManager {
    private var _currentScreen by mutableStateOf(Screen.Splash)
    val currentScreen: Screen get() = _currentScreen

    fun navigateTo(screen: Screen) {
        _currentScreen = screen
    }
}