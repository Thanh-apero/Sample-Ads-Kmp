package com.apero.kmm

import androidx.compose.material.*
import androidx.compose.runtime.*
import com.apero.kmm.navigation.NavigationManager
import com.apero.kmm.navigation.Screen
import com.apero.kmm.screens.*
import com.apero.sdk.ads.kmp.api.KmpAdmobAdapter

@Composable
fun App() {
    val navigationManager = remember { NavigationManager() }
    var isAdsInitialized by remember { mutableStateOf(false) }
    // Auto-initialize ads when app starts
    LaunchedEffect(Unit) {
        try {
            val result = KmpAdmobAdapter.asyncInitialize()
            result.fold(
                onSuccess = {
                    println("AdMob initialized successfully")
                    isAdsInitialized = true
                },
                onFailure = { exception ->
                    println("AdMob initialization failed: ${exception.message}")
                    isAdsInitialized = false
                }
            )
        } catch (e: Exception) {
            println("AdMob initialization error: ${e.message}")
            isAdsInitialized = false
        }
    }

    MaterialTheme {
        when (navigationManager.currentScreen) {
            Screen.Splash -> {
                SplashScreen(
                    isAdsInitialized = isAdsInitialized,
                    onNavigateToMain = { navigationManager.navigateTo(Screen.Main) }
                )
            }
            Screen.Main -> {
                MainScreen(
                    onNavigateToScreenA = { navigationManager.navigateTo(Screen.ViewPager) }
                )
            }
            Screen.Next -> {
                NextScreen(
                    onNavigateBack = { navigationManager.navigateTo(Screen.Main) }
                )
            }
            
            // ViewPager for A-B-C flow with native ads
            Screen.ViewPager -> {
                ViewPagerScreen(
                    onNavigateBack = { navigationManager.navigateTo(Screen.Main) }
                )
            }
        }
    }
}

// Removed all section composables as they will be moved to their respective screen files