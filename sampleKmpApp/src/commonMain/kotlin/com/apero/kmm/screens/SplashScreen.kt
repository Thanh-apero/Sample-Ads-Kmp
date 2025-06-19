package com.apero.kmm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apero.sdk.ads.kmp.api.composable.*
import kotlinx.coroutines.delay
import androidx.compose.animation.core.*
import com.apero.sdk.ads.kmp.api.callback.InterstitialAdShowCallback
import com.apero.sdk.ads.kmp.api.error.AdError
import com.apero.sdk.ads.kmp.api.integrate.preload.InterstitialAdmobPreloader
import com.apero.ads.sdk.adapter.api.interstitial.InterstitialAdRequest
import com.apero.sdk.ads.kmp.api.adunit.AdUnitId.INTERSTITIAL_DEFAULT
import com.apero.sdk.ads.kmp.api.KmpAdmobAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    isAdsInitialized: Boolean,
    onNavigateToMain: () -> Unit
) {
    val bannerAdState = rememberBannerAdState()
    val scope = rememberCoroutineScope()
    
    // Add a state to control when animation starts
    var startAnimation by remember { mutableStateOf(false) }
    val preloadKey = "inter_splash"

    // Animated progress value from 0 to 1 over 5 seconds
    val animatedProgress by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 5000,
            easing = LinearEasing
        )
    )

    // Progress percentage for display
    val progressPercentage = (animatedProgress * 100).toInt()

    // Load banner ad and preload interstitial when ads are initialized
    LaunchedEffect(isAdsInitialized) {
        if (isAdsInitialized) {
            println("Ads initialized: loading banner ad and preloading interstitial...")
            bannerAdState.loadAd()

            // Preload interstitial ad cho splash - tương tự như trong AdsSample
            try {
                val request = InterstitialAdRequest(INTERSTITIAL_DEFAULT)
                InterstitialAdmobPreloader.preloadAd(preloadKey, request)
                println("Interstitial ad preload started for splash")
            } catch (e: Exception) {
                println("Failed to preload interstitial ad: ${e.message}")
            }
        }
        // Start animation immediately
        startAnimation = true
    }

    // Navigate after 8 seconds and show interstitial
    LaunchedEffect(Unit) {
        delay(5000)
            scope.launch {
                try {
                    val adResult = InterstitialAdmobPreloader.awaitAd(preloadKey)
                    if (adResult != null) {
                        KmpAdmobAdapter.showInterstitialAd(
                            adResult,
                            object : InterstitialAdShowCallback {
                                override fun onAdImpression() {
                                    println("Splash interstitial ad impression")
                                }

                            override fun onAdShowFailed(adError: AdError) {
                                println("Splash interstitial ad show failed: ${adError.message}")
                                onNavigateToMain()
                            }

                            override fun onAdClicked() {
                                println("Splash interstitial ad clicked")
                            }

                            override fun onAdClose() {
                                println("Splash interstitial ad closed")
                                onNavigateToMain()
                            }

                            override fun onNextAction() {
                                println("Splash interstitial ad next action")
                            }
                        })
                    } else {
                        println("No preloaded interstitial ad available")
                        onNavigateToMain()
                    }
                } catch (e: Exception) {
                    println("Error showing preloaded interstitial ad: ${e.message}")
                    onNavigateToMain()
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White.copy(alpha = 0.9f),
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF667eea)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Ads Demo App",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Progress bar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Loading... $progressPercentage%",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Gray.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedProgress)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF667eea),
                                                Color(0xFF764ba2)
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }

        // Banner ad at bottom
        BannerAdLayout(
            adUiState = bannerAdState.uiState,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter)
                .background(Color.LightGray.copy(alpha = 0.1f)),
            adSize = AdSize.BANNER
        )
    }
}