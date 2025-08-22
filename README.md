# Apero Ads SDK - Sample KMP App

## Tổng quan

Sample KMP App là một ứng dụng mẫu sử dụng Apero Ads SDK để demo các loại quảng cáo khác nhau trên cả Android và iOS. Ứng dụng được viết bằng Kotlin Multiplatform (KMP) với Compose Multiplatform.

## Cấu trúc dự án

```
sampleKmpApp/
├── src/
│   ├── commonMain/          # Code chung cho cả Android và iOS
│   │   ├── kotlin/
│   │   │   └── com/apero/kmm/
│   │   │       ├── ads/     # Components quảng cáo cross-platform
│   │   │       ├── screens/ # Các màn hình của ứng dụng
│   │   │       └── navigation/ # Quản lý navigation
│   ├── androidMain/         # Code riêng cho Android
│   └── iosMain/            # Code riêng cho iOS
├── build.gradle.kts
└── README.md
```

## 1. Khởi tạo AdMob

```kotlin
import com.apero.sdk.ads.kmp.api.KmpAdmobAdapter

LaunchedEffect(Unit) {
    try {
        val result = KmpAdmobAdapter.asyncInitialize()
        result.fold(
            onSuccess = {
                println("AdMob initialized successfully")
                // Có thể load ads sau khi khởi tạo
            },
            onFailure = { exception ->
                println("AdMob initialization failed: ${exception.message}")
            }
        )
    } catch (e: Exception) {
        println("AdMob initialization error: ${e.message}")
    }
}
```

**Kết quả:**
- `Result<Unit>` - Success hoặc Failure
- Cần await trong coroutine
- Kiểm tra `result.isSuccess` hoặc `result.isFailure`

## 2. Banner Ads

### State cần khai báo:

```kotlin
import com.apero.sdk.ads.kmp.api.composable.*

val bannerAdState = rememberBannerAdState()
```

### Load ad:

```kotlin
LaunchedEffect(Unit) {
    bannerAdState.loadAd()
}
```

### Hiển thị:

```kotlin
BannerAdLayout(
    adUiState = bannerAdState.uiState,           // State của ad
    modifier = Modifier.fillMaxWidth().height(60.dp),  // Kích thước
    adSize = AdSize.BANNER                       // Loại banner
)
```

**Các AdSize có sẵn:**
- `AdSize.BANNER` - 320x50
- `AdSize.LARGE_BANNER` - 320x100
- `AdSize.MEDIUM_RECTANGLE` - 300x250

**Các trạng thái uiState:**
- `shouldShowAd` - Hiển thị ad
- `shouldShowShimmer` - Hiển thị loading
- `shouldShowError` - Hiển thị lỗi

### Ví dụ hoàn chỉnh Banner Ad:

```kotlin
@Composable
fun BannerExample() {
    val bannerAdState = rememberBannerAdState()
    
    LaunchedEffect(Unit) {
        bannerAdState.loadAd()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Content của app
        Column {
            Text("Content của app")
        }
        
        // Banner Ad ở bottom
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
```

## 3. Native Ads

### State cần khai báo:

```kotlin
import com.apero.sdk.ads.kmp.api.composable.*

val nativeAdState = rememberNativeAdState()
```

### Load ad:

```kotlin
LaunchedEffect(Unit) {
    nativeAdState.loadAd()
}
```

### Hiển thị (Cross-platform):

```kotlin
import com.apero.kmm.ads.CrossPlatformNativeAd

CrossPlatformNativeAd(nativeAdState)
```

### Hiển thị (Custom layout):

```kotlin
PlatformAwareNativeAd(
    adUiState = nativeAdState.uiState,
    androidContent = {
        // Layout tùy chỉnh cho Android
        Card(
            modifier = Modifier.fillMaxSize(),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.background(Color.Yellow).padding(4.dp)
                    ) {
                        Text(
                            text = "Ad",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                    Headline(
                        modifier = Modifier.padding(start = 8.dp).weight(1f),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                }

                MediaView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp)
                )

                Body(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    style = TextStyle(fontSize = 14.sp),
                    maxLines = 2
                )

                CallToAction(
                    style = TextStyle(
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .background(
                            Color.Red,
                            RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }
        }
    },
    iosLayoutStyle = {
        AdNativeCustom(adUiState = nativeAdState.uiState, modifier = Modifier)
    }
)
```

**Các thành phần Native Ad:**
- `Headline()` - Tiêu đề
- `Body()` - Nội dung
- `MediaView()` - Hình ảnh/video
- `CallToAction()` - Nút hành động

### Ví dụ hoàn chỉnh Native Ad:

```kotlin
@Composable
fun NativeAdExample() {
    val nativeAdState = rememberNativeAdState()
    val loadedOptions = remember { mutableSetOf<Int>() }
    
    LaunchedEffect(Unit) {
        nativeAdState.loadAd()
    }
    
    Column {
        // Radio buttons để demo load ads
        listOf("Option 1", "Option 2", "Option 3").forEachIndexed { index, text ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .selectable(
                        selected = selectedOption == index,
                        onClick = {
                            if (!loadedOptions.contains(index)) {
                                loadedOptions.add(index)
                                nativeAdState.reset()
                                nativeAdState.loadAd()
                            }
                        }
                    )
            ) {
                Text(text)
            }
        }
        
        // Reload button
        Button(
            onClick = {
                nativeAdState.reset()
                nativeAdState.loadAd()
            }
        ) {
            Text("Reload Ads")
        }
        
        // Ad status debug
        Text(
            text = when {
                nativeAdState.uiState.shouldShowShimmer -> "Loading..."
                nativeAdState.uiState.shouldShowError -> "Error: ${nativeAdState.uiState.errorMessage}"
                nativeAdState.uiState.shouldShowAd -> "Loaded Successfully"
                else -> "Empty/Idle"
            }
        )
        
        // Hiển thị native ad
        if (nativeAdState.uiState.shouldShowAd || nativeAdState.uiState.shouldShowShimmer) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.LightGray.copy(alpha = 0.1f))
                    .padding(8.dp)
            ) {
                CrossPlatformNativeAd(nativeAdState)
            }
        }
    }
}
```

## 4. Interstitial Ad (Fullscreen)

### Load ad trực tiếp:

```kotlin
import com.apero.sdk.ads.kmp.api.KmpAdmobAdapter
import com.apero.sdk.ads.kmp.api.adunit.AdUnitId.INTERSTITIAL_DEFAULT

scope.launch {
    val result = KmpAdmobAdapter.loadInterstitialAd(INTERSTITIAL_DEFAULT)
    result.fold(
        onSuccess = { adResult ->
            // Lưu adResult để show sau
            interstitialAdResult = adResult
        },
        onFailure = { exception ->
            println("Load failed: ${exception.message}")
        }
    )
}
```

### Show ad:

```kotlin
import com.apero.ads.sdk.adapter.api.callback.AdFullScreenShowCallback

KmpAdmobAdapter.showInterstitialAd(
    adResult = interstitialAdResult,
    callback = object : AdFullScreenShowCallback() {
        override fun onAdImpression() {
            println("Ad hiển thị")
        }
        
        override fun onAdClosed() {
            println("Ad đóng")
            interstitialAdResult = null  // Clear sau khi dùng
        }
        
        override fun onAdFailedToShow(error: AdError) {
            println("Show failed: ${error.error}")
        }
        
        override fun onAdClicked() {
            println("Ad được click")
        }
        
        override fun onNextAction() {
            // Hành động tiếp theo
        }
    }
)
```

## 4.1. Interstitial Ad Preload (Nâng cao)

**Preload giúp ads load sẵn trong background, hiển thị nhanh hơn khi cần thiết.**

### Import:

```kotlin
import com.apero.sdk.ads.kmp.api.integrate.preload.InterstitialAdmobPreloader
import com.apero.ads.sdk.adapter.api.interstitial.InterstitialAdRequest
```

### Preload ad:

```kotlin
val preloadKey = "splash_interstitial" // Key duy nhất cho placement
val request = InterstitialAdRequest(INTERSTITIAL_DEFAULT)

try {
    InterstitialAdmobPreloader.preloadAd(preloadKey, request)
    println("Bắt đầu preload interstitial ad")
} catch (e: Exception) {
    println("Lỗi preload: ${e.message}")
}
```

### Lấy ad preload:

```kotlin
val adResult = InterstitialAdmobPreloader.getOrAwaitAd(preloadKey)
if (adResult != null) {
    KmpAdmobAdapter.showInterstitialAd(
        adResult,
        object : AdFullScreenShowCallback() {
            override fun onAdClosed() {
                super.onAdClosed()
                onNavigateToMain() // Hành động sau khi ad đóng
            }
            
            override fun onAdFailedToShow(error: AdError) {
                super.onAdFailedToShow(error)
                onNavigateToMain() // Fallback nếu show failed
            }
        }
    )
} else {
    println("No preloaded interstitial ad available")
    onNavigateToMain() // Fallback
}
```

### Ví dụ hoàn chỉnh Interstitial Ad Preload:

```kotlin
@Composable
fun SplashScreen(onNavigateToMain: () -> Unit) {
    val scope = rememberCoroutineScope()
    val preloadKey = "inter_splash"
    
    // Load banner ad và preload interstitial khi ads được khởi tạo
    LaunchedEffect(isAdsInitialized) {
        if (isAdsInitialized) {
            // Preload interstitial ad cho splash
            try {
                val request = InterstitialAdRequest(INTERSTITIAL_DEFAULT)
                InterstitialAdmobPreloader.preloadAd(preloadKey, request)
                println("Interstitial ad preload started for splash")
            } catch (e: Exception) {
                println("Failed to preload interstitial ad: ${e.message}")
            }
        }
    }
    
    // Navigate sau 5 giây và show interstitial
    LaunchedEffect(Unit) {
        delay(5000)
        scope.launch {
            try {
                val adResult = InterstitialAdmobPreloader.getOrAwaitAd(preloadKey)
                if (adResult != null) {
                    KmpAdmobAdapter.showInterstitialAd(
                        adResult,
                        object : AdFullScreenShowCallback() {
                            override fun onAdClosed() {
                                super.onAdClosed()
                                onNavigateToMain()
                            }
                            
                            override fun onAdFailedToShow(error: AdError) {
                                super.onAdFailedToShow(error)
                                onNavigateToMain()
                            }
                        }
                    )
                } else {
                    onNavigateToMain()
                }
            } catch (e: Exception) {
                onNavigateToMain()
            }
        }
    }
    
    // UI content...
}
```

## 5. Native Ad Preload

### Import:

```kotlin
import com.apero.sdk.ads.kmp.api.integrate.preload.NativeAdmobPreloader
import com.apero.ads.sdk.adapter.api.nativead.NativeAdRequest
import com.apero.sdk.ads.kmp.api.adunit.AdUnitId.NATIVE_DEFAULT
```

### Preload:

```kotlin
val preloadKey = "native_ad_page_1"
val request = NativeAdRequest(NATIVE_DEFAULT)

try {
    NativeAdmobPreloader.preloadAd(preloadKey, request)
    println("Started preloading native ad")
} catch (e: Exception) {
    println("Failed to preload native ad: ${e.message}")
}
```

### Chờ ad preload hoàn thành:

```kotlin
scope.launch {
    try {
        val adResult = NativeAdmobPreloader.getOrAwaitAd(preloadKey)
        if (adResult != null) {
            println("Native ad preload completed successfully")
            updatePreloadState(preloadKey, true)
        } else {
            println("Native ad preload failed - no result")
            updatePreloadState(preloadKey, false)
        }
    } catch (e: Exception) {
        println("Failed to preload native ad: ${e.message}")
        updatePreloadState(preloadKey, false)
    }
}
```

### Ví dụ hoàn chỉnh Native Ad Preload:

```kotlin
@Composable
fun ViewPagerScreen(onNavigateBack: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 6 })
    val scope = rememberCoroutineScope()
    
    // Preload keys cho các native ads khác nhau
    val preloadKey1 = "native_ad_page_1"
    val preloadKey2 = "native_ad_page_2"
    
    // State để track preload status
    var preloadStates by remember { mutableStateOf(mapOf<String, Boolean>()) }
    
    // Function để update preload state
    val updatePreloadState = { key: String, isLoaded: Boolean ->
        preloadStates = preloadStates.toMutableMap().apply {
            put(key, isLoaded)
        }
    }
    
    // Preload logic dựa trên current page
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
    
    // UI content...
}
```

## 6. Ad Unit IDs có sẵn

```kotlin
import com.apero.sdk.ads.kmp.api.adunit.AdUnitId

// Banner Ads
AdUnitId.BANNER_DEFAULT

// Interstitial Ads  
AdUnitId.INTERSTITIAL_DEFAULT

// Native Ads
AdUnitId.NATIVE_DEFAULT
```

## 7. Error Handling

SDK cung cấp error handling tự động thông qua uiState:

```kotlin
// Kiểm tra trạng thái ads
when {
    adState.uiState.shouldShowShimmer -> {
        // Đang loading
        Text("Loading...")
    }
    adState.uiState.shouldShowError -> {
        // Có lỗi, hiển thị message
        Text("Error: ${adState.uiState.errorMessage}")
    }
    adState.uiState.shouldShowAd -> {
        // Hiển thị ad
        AdComponent(adState)
    }
    else -> {
        // Empty/Idle state
        Text("No ad available")
    }
}
```

## 8. Platform-specific Implementation

### Android
- Sử dụng Compose UI
- Tích hợp với AdMob SDK
- Hỗ trợ Material Design

### iOS
- Sử dụng SwiftUI thông qua KMP
- Tích hợp với Google Mobile Ads SDK
- Hỗ trợ iOS native components thông qua `AdNativeCustom`

## 9. Build và Run

### Yêu cầu
- Kotlin 1.8+
- Android Studio Hedgehog | 2023.1.1+
- Xcode 15.0+
- iOS 13.0+
- Android API 21+

### Build Android
```bash
./gradlew :sampleKmpApp:assembleDebug
```

### Build iOS
```bash
./gradlew :sampleKmpApp:iosSimulatorArm64Framework
```

## 10. Dependencies

```kotlin
dependencies {
    implementation(project(":common-ads-sdk-admob"))
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.ui)
}
```

## 11. Best Practices

1. **Khởi tạo SDK sớm**: Khởi tạo trong SplashScreen hoặc MainActivity
2. **Preload ads**: Sử dụng preloading để giảm thời gian chờ
3. **Error handling**: Luôn xử lý lỗi và fallback gracefully
4. **Memory management**: Reset ad states khi không cần thiết
5. **User experience**: Không hiển thị quá nhiều ads cùng lúc
6. **State management**: Sử dụng `reset()` trước khi load ads mới

## 12. Troubleshooting

### Ads không load
- Kiểm tra internet connection
- Verify Ad Unit IDs
- Check AdMob account status
- Review console logs

### Build errors
- Clean project và rebuild
- Sync Gradle files
- Check Kotlin version compatibility

## 13. Support

- **Documentation**: [Apero Ads SDK Docs](https://docs.apero.com)
- **Issues**: [GitHub Issues](https://github.com/apero/ads-sdk/issues)
- **Email**: support@apero.com

## License

Copyright © 2024 Apero. All rights reserved.
