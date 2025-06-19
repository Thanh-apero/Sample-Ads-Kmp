package com.apero.kmm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apero.kmm.ads.CrossPlatformNativeAd
import com.apero.sdk.ads.kmp.api.composable.*
import com.apero.sdk.ads.kmp.api.integrate.preload.NativeAdmobPreloader
import com.apero.ads.sdk.adapter.api.nativead.NativeAdRequest
import com.apero.sdk.ads.kmp.api.adunit.AdUnitId
import kotlinx.coroutines.delay

@Composable
fun ViewPagerScreen(
    onNavigateBack: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 6 })
    var adsLoadedStates by remember { mutableStateOf(mapOf<Int, Boolean>()) }

    // Placement IDs for preloading
    val nativeAdPlacement1 = "native_ad_between_a_b"
    val nativeAdPlacement2 = "native_ad_between_b_c"

    // Function to update ad load state
    val updateAdLoadState = { pageIndex: Int, isLoaded: Boolean ->
        adsLoadedStates = adsLoadedStates.toMutableMap().apply {
            put(pageIndex, isLoaded)
        }
    }

    // Preload native ads based on current page
    LaunchedEffect(pagerState.currentPage) {
        when (pagerState.currentPage) {
            0 -> {
                // On Screen A - preload native ad for page 1
                if (!NativeAdmobPreloader.isPreloading(nativeAdPlacement1) &&
                    !NativeAdmobPreloader.isAdAvailable(nativeAdPlacement1)) {
                    println("Preloading native ad for page 1...")
                    NativeAdmobPreloader.preloadAd(
                        nativeAdPlacement1,
                        NativeAdRequest(AdUnitId.NATIVE_DEFAULT)
                    )
                }
            }
            2 -> {
                // On Screen B - preload native ad for page 3
                if (!NativeAdmobPreloader.isPreloading(nativeAdPlacement2) &&
                    !NativeAdmobPreloader.isAdAvailable(nativeAdPlacement2)) {
                    println("Preloading native ad for page 3...")
                    NativeAdmobPreloader.preloadAd(
                        nativeAdPlacement2,
                        NativeAdRequest(AdUnitId.NATIVE_DEFAULT)
                    )
                }
            }
        }
    }

    // Check if current page can be swiped from
    val canSwipeFromCurrentPage = remember(pagerState.currentPage, adsLoadedStates) {
        when (pagerState.currentPage) {
            1 -> adsLoadedStates[1] == true // Native ad page 1 - có thể swipe khi ad loaded hoặc error
            3 -> adsLoadedStates[3] == true // Native ad page 2 - có thể swipe khi ad loaded hoặc error
            else -> true // Other pages can always be swiped
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Thanh tiến trình (Page Indicator)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(6) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage)
                                    MaterialTheme.colors.primary
                                else
                                    Color.Gray.copy(alpha = 0.3f)
                            )
                    )
                    if (index < 5) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            // ViewPager chính
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = canSwipeFromCurrentPage
            ) { page ->
                when (page) {
                    0 -> ScreenAPage()
                    1 -> NativeAdPage(
                        title = "Native Ad - Between A & B",
                        nextScreenText = "Continue to Screen B",
                        placementId = nativeAdPlacement1,
                        onAdLoadStateChanged = { isLoaded -> updateAdLoadState(1, isLoaded) }
                    )
                    2 -> ScreenBPage()
                    3 -> NativeAdPage(
                        title = "Native Ad - Between B & C",
                        nextScreenText = "Continue to Screen C",
                        placementId = nativeAdPlacement2,
                        onAdLoadStateChanged = { isLoaded -> updateAdLoadState(3, isLoaded) }
                    )
                    4 -> ScreenCPage()
                    5 -> FinishPage(onNavigateBack = onNavigateBack)
                }
            }
        }
    }
}

@Composable
private fun ScreenAPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Content area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🚀",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to Screen A!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "First step in our journey",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "This is the first screen in our A-B-C flow!",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Swipe right to see the native ad before moving to Screen B.",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
        }

        // Swipe hint
        Text(
            text = "👉 Vuốt sang phải để tiếp tục",
            fontSize = 16.sp,
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ScreenBPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Content area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🔥",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Great Progress!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Screen B - Halfway there",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You're doing great!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "This is the second screen in our flow. Another native ad is coming up before the final screen.",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
        }

        // Swipe hint
        Text(
            text = "👉 Vuốt sang phải để tiếp tục",
            fontSize = 16.sp,
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ScreenCPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Content area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🏆",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Almost Done!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Screen C - Final Step",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "You've almost completed the A-B-C flow!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "One more swipe to finish!",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
        }

        // Swipe hint
        Text(
            text = "👉 Vuốt sang phải để hoàn thành",
            fontSize = 16.sp,
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun NativeAdPage(
    title: String,
    nextScreenText: String,
    placementId: String,
    onAdLoadStateChanged: (Boolean) -> Unit
) {
    val nativeAdState = rememberNativeAdState()
    var isPreloadedAdUsed by remember { mutableStateOf(false) }

    // Try to get preloaded ad first, then load normally if not available
    LaunchedEffect(Unit) {
        try {
            // First check if we have a preloaded ad available
            val preloadedAd = NativeAdmobPreloader.popBufferedAd(placementId)
            if (preloadedAd != null) {
                println("Using preloaded native ad for $placementId")
                // Convert the preloaded ad to our state
                nativeAdState.setPreloadedAd(preloadedAd)
                isPreloadedAdUsed = true
            } else {
                // Try to await a preloading ad with timeout
                println("Waiting for preloaded native ad for $placementId...")
                val awaitedAd = NativeAdmobPreloader.awaitAd(placementId, 5000L)
                if (awaitedAd != null) {
                    println("Got awaited preloaded native ad for $placementId")
                    nativeAdState.setPreloadedAd(awaitedAd)
                    isPreloadedAdUsed = true
                } else {
                    // Fallback to normal loading
                    println("No preloaded ad available for $placementId, loading normally...")
                    nativeAdState.loadAd()
                }
            }
        } catch (e: Exception) {
            println("Error getting preloaded ad for $placementId: ${e.message}")
            // Fallback to normal loading
            nativeAdState.loadAd()
        }
    }

    LaunchedEffect(nativeAdState.uiState.shouldShowAd, nativeAdState.uiState.shouldShowError) {
        onAdLoadStateChanged(nativeAdState.uiState.shouldShowAd || nativeAdState.uiState.shouldShowError)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
    ) {
        // Full screen native ad
        when {
            nativeAdState.uiState.shouldShowAd || nativeAdState.uiState.shouldShowShimmer -> {
                CrossPlatformNativeAd(
                    nativeAdState = nativeAdState
                )
            }
            nativeAdState.uiState.shouldShowError -> {
                // Error state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Không thể tải quảng cáo",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = nativeAdState.uiState.errorMessage ?: "Lỗi không xác định",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "👉 Vuốt sang phải để bỏ qua",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun FinishPage(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Content area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🎉",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Congratulations!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You've completed the entire flow!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "You've successfully navigated through:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "✓ Screen A\n✓ Native Ad #1\n✓ Screen B\n✓ Native Ad #2\n✓ Screen C\n✓ Completion!",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
        }

        // Finish button
        Button(
            onClick = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF4CAF50)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hoàn thành & Quay về Main",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
} 