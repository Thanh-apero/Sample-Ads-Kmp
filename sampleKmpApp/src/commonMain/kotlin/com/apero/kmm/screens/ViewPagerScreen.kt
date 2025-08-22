package com.apero.kmm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apero.kmm.ads.CrossPlatformNativeAd
import com.apero.sdk.ads.kmp.api.composable.rememberNativeAdState
import com.apero.sdk.ads.kmp.api.integrate.preload.NativeAdmobPreloader
import com.apero.ads.sdk.adapter.api.nativead.NativeAdRequest
import com.apero.ads.sdk.adapter.api.nativead.NativeAdResult
import com.apero.sdk.ads.kmp.api.adunit.AdUnitId.NATIVE_DEFAULT
import kotlinx.coroutines.launch

@Composable
fun ViewPagerScreen(
    onNavigateBack: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 6 })
    val scope = rememberCoroutineScope()

    // Preload keys for different native ads
    val preloadKey1 = "native_ad_page_1"
    val preloadKey2 = "native_ad_page_2"

    // State to track preload status
    var preloadStates by remember { mutableStateOf(mapOf<String, Boolean>()) }

    // Function to update preload state
    val updatePreloadState = { key: String, isLoaded: Boolean ->
        preloadStates = preloadStates.toMutableMap().apply {
            put(key, isLoaded)
        }
    }

    // Preload logic based on current page
    LaunchedEffect(pagerState.currentPage) {
        when (pagerState.currentPage) {
            0 -> {
                // On Screen A, preload first native ad
                if (preloadStates[preloadKey1] != true) {
                    scope.launch {
                        try {
                            println("Starting preload for native ad 1 from Screen A")
                            val request = NativeAdRequest(NATIVE_DEFAULT)
                            NativeAdmobPreloader.preloadAd(preloadKey1, request)

                            // Wait for the ad to be actually loaded
                            val adResult = NativeAdmobPreloader.getOrAwaitAd(preloadKey1)
                            if (adResult != null) {
                                println("Native ad 1 preload completed successfully")
                                updatePreloadState(preloadKey1, true)
                            } else {
                                println("Native ad 1 preload failed - no result")
                                updatePreloadState(preloadKey1, false)
                            }
                        } catch (e: Exception) {
                            println("Failed to preload native ad 1: ${e.message}")
                            updatePreloadState(preloadKey1, false)
                        }
                    }
                }
            }

            2 -> {
                // On Screen B, preload second native ad
                if (preloadStates[preloadKey2] != true) {
                    scope.launch {
                        try {
                            println("Starting preload for native ad 2 from Screen B")
                            val request = NativeAdRequest(NATIVE_DEFAULT)
                            NativeAdmobPreloader.preloadAd(preloadKey2, request)

                            // Wait for the ad to be actually loaded
                            val adResult = NativeAdmobPreloader.getOrAwaitAd(preloadKey2)
                            if (adResult != null) {
                                println("Native ad 2 preload completed successfully")
                                updatePreloadState(preloadKey2, true)
                            } else {
                                println("Native ad 2 preload failed - no result")
                                updatePreloadState(preloadKey2, false)
                            }
                        } catch (e: Exception) {
                            println("Failed to preload native ad 2: ${e.message}")
                            updatePreloadState(preloadKey2, false)
                        }
                    }
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("A-B-C Flow") },
                backgroundColor = MaterialTheme.colors.primary,
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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

            // ViewPager chính - allow scrolling always since we preload
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = true
            ) { page ->
                when (page) {
                    0 -> ScreenAPage()
                    1 -> PreloadedNativeAdPage(
                        title = "Native Ad - Between A & B",
                        preloadKey = preloadKey1,
                        isPreloaded = preloadStates[preloadKey1] ?: false
                    )
                    2 -> ScreenBPage()
                    3 -> PreloadedNativeAdPage(
                        title = "Native Ad - Between B & C",
                        preloadKey = preloadKey2,
                        isPreloaded = preloadStates[preloadKey2] ?: false
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
private fun PreloadedNativeAdPage(
    title: String,
    preloadKey: String,
    isPreloaded: Boolean
) {
    val scope = rememberCoroutineScope()
    var adResult by remember { mutableStateOf<NativeAdResult?>(null) }
    var adError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Try to get preloaded ad when page loads
    LaunchedEffect(isPreloaded) {
        if (isPreloaded && adResult == null) {
            println("Attempting to get preloaded ad for key: $preloadKey")
            scope.launch {
                try {
                    val preloadedAd = NativeAdmobPreloader.getOrAwaitAd(preloadKey)
                    if (preloadedAd != null && preloadedAd is NativeAdResult) {
                        println("Successfully retrieved preloaded native ad with key: $preloadKey")
                        adResult = preloadedAd
                        adError = null
                    } else {
                        println("No preloaded native ad available for key: $preloadKey, will load directly")
                        // Fallback: try to load directly
                        isLoading = true
                        val request = NativeAdRequest(NATIVE_DEFAULT)
                        NativeAdmobPreloader.preloadAd(preloadKey, request)
                        val directAd = NativeAdmobPreloader.getOrAwaitAd(preloadKey)
                        if (directAd != null && directAd is NativeAdResult) {
                            adResult = directAd
                            adError = null
                        } else {
                            adError = "Không thể tải quảng cáo"
                        }
                        isLoading = false
                    }
                } catch (e: Exception) {
                    println("Error getting preloaded native ad: ${e.message}")
                    adError = "Lỗi hiển thị quảng cáo: ${e.message}"
                    isLoading = false
                }
            }
        } else if (!isPreloaded && adResult == null) {
            // If not preloaded, try to load directly
            println("Ad not preloaded for key: $preloadKey, loading directly")
            isLoading = true
            scope.launch {
                try {
                    val request = NativeAdRequest(NATIVE_DEFAULT)
                    NativeAdmobPreloader.preloadAd(preloadKey, request)
                    val directAd = NativeAdmobPreloader.getOrAwaitAd(preloadKey)
                    if (directAd != null && directAd is NativeAdResult) {
                        adResult = directAd
                        adError = null
                    } else {
                        adError = "Không thể tải quảng cáo"
                    }
                } catch (e: Exception) {
                    adError = "Lỗi tải quảng cáo: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
    ) {
        // Close icon (top right) - show when ad is loaded or error
        if (adResult != null || adError != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Skip",
                    tint = Color.White
                )
            }
        }

        // Native Ad Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Native Ad Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                when {
                    isLoading -> {
                        // Loading state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Đang tải quảng cáo...",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    adResult != null -> {
                        // Show the actual preloaded native ad
                        val nativeAdState = rememberNativeAdState()

                        // Set the preloaded ad result to the native ad state
                        LaunchedEffect(adResult) {
                            println("Setting preloaded native ad for key: $preloadKey")
                            try {
                                val result = adResult
                                if (result != null) {
                                    nativeAdState.setPreloadedAd(result)
                                    println("Successfully set preloaded native ad to state")
                                }
                            } catch (e: Exception) {
                                println("Error setting preloaded ad: ${e.message}")
                            }
                        }

                        CrossPlatformNativeAd(nativeAdState)
                    }
                    adError != null -> {
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
                                    color = Color.Gray
                                )
                                Text(
                                    text = adError ?: "Lỗi không xác định",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    else -> {
                        // Default state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Đang chuẩn bị quảng cáo...",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = when {
                    adResult != null -> "👉 Vuốt sang phải để tiếp tục"
                    adError != null -> "👉 Vuốt sang phải để bỏ qua"
                    isLoading -> "Đang tải quảng cáo..."
                    else -> "Đang chuẩn bị quảng cáo..."
                },
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
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