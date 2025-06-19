# Apero Ads SDK - KMP Complete Guide

## 1. Khởi tạo AdMob

```kotlin
val result = KmpAdmobAdapter.asyncInitialize()
```

**Kết quả:**

- `Result<Unit>` - Success hoặc Failure
- Cần await trong coroutine
- Kiểm tra `result.isSuccess` hoặc `result.isFailure`

**Ví dụ:**

```kotlin
scope.launch {
    val result = KmpAdmobAdapter.asyncInitialize()
    if (result.isSuccess) {
        println("AdMob khởi tạo thành công")
    } else {
        println("Lỗi: ${result.exceptionOrNull()?.message}")
    }
}
```

## 2. Banner Ad

### State cần khai báo:

```kotlin
val bannerAdState = rememberBannerAdState()
```

### Load ad:

```kotlin
bannerAdState.loadAd()
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
    
    Column {
        Text("Content của app")
        
        // Banner Ad
        BannerAdLayout(
            adUiState = bannerAdState.uiState,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.LightGray),
            adSize = AdSize.BANNER
        )
    }
}
```

## 3. Native Ad

### State cần khai báo:

```kotlin
val nativeAdState = rememberNativeAdState()
```

### Load ad:

```kotlin
nativeAdState.loadAd()
```

### Hiển thị (Cross-platform):

```kotlin
PlatformAwareNativeAd(
    adUiState = nativeAdState.uiState,
    androidContent = {
        // Layout tùy chỉnh cho Android
        Column {
            Headline(modifier = Modifier.fillMaxWidth())
            MediaView(modifier = Modifier.size(200.dp))
            Body(modifier = Modifier.fillMaxWidth())
            CallToAction(modifier = Modifier.fillMaxWidth())
        }
    },
    iosLayoutStyle = NativeAdLayoutStyle.FULLSCREEN
)
```

### Hiển thị (Android only):

```kotlin
NativeAdLayout(
    adUiState = nativeAdState.uiState,
    modifier = modifier,
    content = {
        Column {
            Headline(modifier = Modifier.fillMaxWidth())
            MediaView(modifier = Modifier.size(200.dp))
            Body(modifier = Modifier.fillMaxWidth())
            CallToAction(modifier = Modifier.fillMaxWidth())
        }
    },
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
    
    LaunchedEffect(Unit) {
        nativeAdState.loadAd()
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 4.dp
    ) {
            PlatformAwareNativeAd(
                adUiState = nativeAdState.uiState,
                androidContent = {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row {
                            MediaView(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Headline(
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Body(
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        CallToAction(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        )
                    }
                },
                iosLayoutStyle = NativeAdLayoutStyle.FULLSCREEN
            )
        }
    }
```

## 4. Interstitial Ad (Fullscreen)

### Load ad:

```kotlin
scope.launch {
    val result = KmpAdmobAdapter.loadInterstitialAd(AdUnitId.INTERSTITIAL_DEFAULT)
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
KmpAdmobAdapter.showInterstitialAd(
    adResult = interstitialAdResult,
    callback = object : InterstitialAdShowCallback {
        override fun onAdImpression() {
            println("Ad hiển thị")
        }
        
        override fun onAdClose() {
            println("Ad đóng")
            interstitialAdResult = null  // Clear sau khi dùng
        }
        
        override fun onAdShowFailed(adError: AdError) {
            println("Show failed: ${adError.message}")
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

### Ví dụ hoàn chỉnh Interstitial Ad:

```kotlin
class InterstitialAdManager {
    private var interstitialAdResult: InterstitialAdResult? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    
    fun loadAd() {
        scope.launch {
            val result = KmpAdmobAdapter.loadInterstitialAd(AdUnitId.INTERSTITIAL_DEFAULT)
            result.fold(
                onSuccess = { adResult ->
                    interstitialAdResult = adResult
                    println("Interstitial ad loaded successfully")
                },
                onFailure = { exception ->
                    println("Failed to load interstitial ad: ${exception.message}")
                }
            )
        }
    }
    
    fun showAd(onAdClosed: () -> Unit) {
        interstitialAdResult?.let { ad ->
            KmpAdmobAdapter.showInterstitialAd(
                ad,
                object : InterstitialAdShowCallback {
                    override fun onAdImpression() {
                        println("Interstitial ad impression")
                    }
                    
                    override fun onAdClose() {
                        println("Interstitial ad closed")
                        interstitialAdResult = null
                        onAdClosed()
                    }
                    
                    override fun onAdShowFailed(adError: AdError) {
                        println("Interstitial ad show failed: ${adError.message}")
                        interstitialAdResult = null
                        onAdClosed()
                    }
                    
                    override fun onAdClicked() {
                        println("Interstitial ad clicked")
                    }
                    
                    override fun onNextAction() {
                        println("Interstitial ad next action")
                    }
                }
            )
        } ?: run {
            println("No interstitial ad loaded")
            onAdClosed()
        }
    }
    
    fun isAdReady(): Boolean {
        return interstitialAdResult != null
    }
}
```

## 4.1. Interstitial Ad Preload (Nâng cao)

**Preload giúp ads load sẵn trong background, hiển thị nhanh hơn khi cần thiết.**

### Import:

### Preload ad:

```kotlin
val preloadKey = "splash_interstitial" // Key duy nhất cho placement
val request = InterstitialAdRequest(AdUnitId.INTERSTITIAL_DEFAULT)

try {
    InterstitialAdmobPreloader.preloadAd(preloadKey, request)
    println("Bắt đầu preload interstitial ad")
} catch (e: Exception) {
    println("Lỗi preload: ${e.message}")
}
```

### Kiểm tra ad có sẵn:

```kotlin
val isAvailable = InterstitialAdmobPreloader.isAdAvailable(preloadKey)
// Kết quả: Boolean
```

### Lấy ad preload ngay lập tức:

```kotlin
val preloadedAd = InterstitialAdmobPreloader.popBufferedAd(preloadKey)
// Kết quả: InterstitialAdResult? (null nếu chưa load xong)

if (preloadedAd != null) {
    // Show ad ngay
    KmpAdmobAdapter.showInterstitialAd(preloadedAd, callback)
} else {
    // Load ad thường
    val result = KmpAdmobAdapter.loadInterstitialAd(AdUnitId.INTERSTITIAL_DEFAULT)
}
```

### Chờ ad preload hoàn thành:

```kotlin
scope.launch {
    try {
        val adResult = InterstitialAdmobPreloader.awaitAd(
            preloadKey, 
            timeoutMs = 5000L  // Timeout sau 5 giây
        )
        
        if (adResult != null) {
            KmpAdmobAdapter.showInterstitialAd(adResult, callback)
        } else {
            println("Timeout hoặc load failed")
        }
    } catch (e: Exception) {
        println("Lỗi await: ${e.message}")
    }
}
```
### Các phương thức Preloader:

| Phương thức | Mô tả | Kết quả |
|-------------|-------|---------|
| `preloadAd(key, request)` | Bắt đầu preload | Void |
| `isAdAvailable(key)` | Kiểm tra có ad sẵn | Boolean |
| `popBufferedAd(key)` | Lấy ad ngay (xóa khỏi buffer) | InterstitialAdResult? |
| `awaitAd(key, timeout)` | Chờ ad load xong | InterstitialAdResult? |

**Lưu ý:**

- Một preloadKey chỉ chứa được 1 ad
- `popBufferedAd` sẽ xóa ad khỏi buffer sau khi lấy
- `awaitAd` sẽ chờ đến khi load xong hoặc timeout
- Nên preload trước 3-5 giây so với khi cần show

## 5. Rewarded Ad

### Load ad:

```kotlin
scope.launch {
    val result = KmpAdmobAdapter.loadRewardAd(AdUnitId.REWARDED_DEFAULT)
    result.fold(
        onSuccess = { adResult ->
            rewardAdResult = adResult
        },
        onFailure = { exception ->
            println("Load reward ad failed: ${exception.message}")
        }
    )
}
```

### Show ad:

```kotlin
KmpAdmobAdapter.showRewardAd(
    adResult = rewardAdResult,
    callback = object : RewardAdShowCallback {
        override fun onUserEarnedReward(amount: Int, type: String) {
            println("User nhận được: $amount $type")
            // Thưởng cho user tại đây
        }
        
        override fun onAdClose() {
            println("Reward ad đóng")
            rewardAdResult = null
        }
        
        override fun onAdImpression() {
            println("Reward ad hiển thị")
        }
        
        override fun onAdShowFailed(adError: AdError) {
            println("Show reward ad failed: ${adError.message}")
            rewardAdResult = null
        }
        
        override fun onAdClicked() {
            println("Reward ad clicked")
        }
        
        override fun onNextAction() {
            println("Reward ad next action")
        }
    }
)
```

### Ví dụ hoàn chỉnh Rewarded Ad:

```kotlin
class RewardAdManager {
    private var rewardAdResult: RewardAdResult? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    
    fun loadAd() {
        scope.launch {
            val result = KmpAdmobAdapter.loadRewardAd(AdUnitId.REWARDED_DEFAULT)
            result.fold(
                onSuccess = { adResult ->
                    rewardAdResult = adResult
                    println("Reward ad loaded successfully")
                },
                onFailure = { exception ->
                    println("Failed to load reward ad: ${exception.message}")
                }
            )
        }
    }
    
    fun showAd(onRewardEarned: (Int, String) -> Unit, onAdClosed: () -> Unit) {
        rewardAdResult?.let { ad ->
            KmpAdmobAdapter.showRewardAd(
                ad,
                object : RewardAdShowCallback {
                    override fun onUserEarnedReward(amount: Int, type: String) {
                        println("User earned reward: $amount $type")
                        onRewardEarned(amount, type)
                    }
                    
                    override fun onAdClose() {
                        println("Reward ad closed")
                        rewardAdResult = null
                        onAdClosed()
                    }
                    
                    override fun onAdImpression() {
                        println("Reward ad impression")
                    }
                    
                    override fun onAdShowFailed(adError: AdError) {
                        println("Reward ad show failed: ${adError.message}")
                        rewardAdResult = null
                        onAdClosed()
                    }
                    
                    override fun onAdClicked() {
                        println("Reward ad clicked")
                    }
                    
                    override fun onNextAction() {
                        println("Reward ad next action")
                    }
                }
            )
        } ?: run {
            println("No reward ad loaded")
            onAdClosed()
        }
    }
    
    fun isAdReady(): Boolean {
        return rewardAdResult != null
    }
}

// Sử dụng trong UI
@Composable
fun RewardButton() {
    val rewardManager = remember { RewardAdManager() }
    var coins by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        rewardManager.loadAd()
    }
    
    Button(
        onClick = {
            rewardManager.showAd(
                onRewardEarned = { amount, type ->
                    coins += amount
                },
                onAdClosed = {
                    // Load ad mới cho lần sau
                    rewardManager.loadAd()
                }
            )
        },
        enabled = rewardManager.isAdReady()
    ) {
        Text("Watch Ad for Coins")
    }
    
    Text("Coins: $coins")
}
```

## 6. Native Ad Preload

### Import:

### Preload:

```kotlin
NativeAdmobPreloader.preloadAd(
    placementId = "my_placement",
    request = NativeAdRequest(AdUnitId.NATIVE_DEFAULT)
)
```

### Kiểm tra có ad:

```kotlin
val hasAd = NativeAdmobPreloader.isAdAvailable("my_placement")
// Kết quả: Boolean
```

### Lấy ad đã preload:

```kotlin
val preloadedAd = NativeAdmobPreloader.popBufferedAd("my_placement")
// Kết quả: NativeAdResult? (null nếu không có)
```

### Chờ ad preload:

```kotlin
val awaitedAd = NativeAdmobPreloader.awaitAd("my_placement", 5000L)
// Kết quả: NativeAdResult? (timeout sau 5 giây)
```

### Sử dụng preloaded ad:

```kotlin
val preloadedAd = NativeAdmobPreloader.popBufferedAd("my_placement")
if (preloadedAd != null) {
    nativeAdState.setPreloadedAd(preloadedAd)
} else {
    nativeAdState.loadAd()  // Fallback
}
```

### Ví dụ hoàn chỉnh Native Ad Preload:

```kotlin
class NativeAdPreloadManager {
    private val placementKey = "home_native_ad"
    
    fun preloadAd() {
        val request = NativeAdRequest(AdUnitId.NATIVE_DEFAULT)
        NativeAdmobPreloader.preloadAd(placementKey, request)
        println("Started preloading native ad")
    }
    
    suspend fun getPreloadedAd(): NativeAdResult? {
        // Chờ tối đa 3 giây
        return NativeAdmobPreloader.awaitAd(placementKey, 3000L)
    }
    
    fun getAdImmediately(): NativeAdResult? {
        return NativeAdmobPreloader.popBufferedAd(placementKey)
    }
    
    fun isAdReady(): Boolean {
        return NativeAdmobPreloader.isAdAvailable(placementKey)
    }
}

@Composable
fun PreloadedNativeAdScreen() {
    val nativeAdState = rememberNativeAdState()
    val preloadManager = remember { NativeAdPreloadManager() }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        // Preload ad khi vào màn hình
        preloadManager.preloadAd()
        
        // Chờ ad load xong
        scope.launch {
            val preloadedAd = preloadManager.getPreloadedAd()
            if (preloadedAd != null) {
                nativeAdState.setPreloadedAd(preloadedAd)
            } else {
                // Fallback load ad thường
                nativeAdState.loadAd()
            }
        }
    }
    
    // UI hiển thị native ad
    if (nativeAdState.uiState.shouldShowAd) {
        PlatformAwareNativeAd(
            adUiState = nativeAdState.uiState,
            androidContent = {
                // Layout native ad
            },
            iosLayoutStyle = NativeAdLayoutStyle.FULLSCREEN
        )
    }
}
```

## 7. App Open Ad

### Load ad:

```kotlin
scope.launch {
    val result = KmpAdmobAdapter.loadAppOpenAd(AdUnitId.APP_OPEN_DEFAULT)
    result.fold(
        onSuccess = { adResult ->
            appOpenAdResult = adResult
        },
        onFailure = { exception ->
            println("Load app open ad failed: ${exception.message}")
        }
    )
}
```

### Show ad:

```kotlin
KmpAdmobAdapter.showAppOpenAd(
    adResult = appOpenAdResult,
    callback = object : AppOpenAdShowCallback {
        override fun onAdImpression() {
            println("App open ad impression")
        }
        
        override fun onAdClose() {
            println("App open ad closed")
            appOpenAdResult = null
        }
        
        override fun onAdShowFailed(adError: AdError) {
            println("App open ad show failed: ${adError.message}")
            appOpenAdResult = null
        }
        
        override fun onAdClicked() {
            println("App open ad clicked")
        }
        
        override fun onNextAction() {
            println("App open ad next action")
        }
    }
)
```

### Ví dụ hoàn chỉnh App Open Ad:

```kotlin
class AppOpenAdManager {
    private var appOpenAdResult: AppOpenAdResult? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var isShowingAd = false
    private var loadTime: Long = 0
    
    fun loadAd() {
        // Không load nếu đang show ad
        if (isShowingAd) return
        
        scope.launch {
            val result = KmpAdmobAdapter.loadAppOpenAd(AdUnitId.APP_OPEN_DEFAULT)
            result.fold(
                onSuccess = { adResult ->
                    appOpenAdResult = adResult
                    loadTime = System.currentTimeMillis()
                    println("App open ad loaded successfully")
                },
                onFailure = { exception ->
                    println("Failed to load app open ad: ${exception.message}")
                }
            )
        }
    }
    
    fun showAdIfAvailable(onAdClosed: () -> Unit) {
        // Kiểm tra ad có sẵn và không quá cũ (4 giờ)
        if (!isAdAvailable()) {
            onAdClosed()
            return
        }
        
        isShowingAd = true
        
        appOpenAdResult?.let { ad ->
            KmpAdmobAdapter.showAppOpenAd(
                ad,
                object : AppOpenAdShowCallback {
                    override fun onAdImpression() {
                        println("App open ad impression")
                    }
                    
                    override fun onAdClose() {
                        println("App open ad closed")
                        appOpenAdResult = null
                        isShowingAd = false
                        onAdClosed()
                        
                        // Preload ad mới
                        loadAd()
                    }
                    
                    override fun onAdShowFailed(adError: AdError) {
                        println("App open ad show failed: ${adError.message}")
                        appOpenAdResult = null
                        isShowingAd = false
                        onAdClosed()
                    }
                    
                    override fun onAdClicked() {
                        println("App open ad clicked")
                    }
                    
                    override fun onNextAction() {
                        println("App open ad next action")
                    }
                }
            )
        } ?: onAdClosed()
    }
    
    private fun isAdAvailable(): Boolean {
        return appOpenAdResult != null && 
               !isShowingAd && 
               (System.currentTimeMillis() - loadTime) < (4 * 60 * 60 * 1000) // 4 giờ
    }
}

// Sử dụng trong Application
class MyApplication {
    private val appOpenAdManager = AppOpenAdManager()
    
    fun onCreate() {
        // Load app open ad khi app khởi động
        appOpenAdManager.loadAd()
    }
    
    fun onAppForegrounded() {
        // Show app open ad khi app được mở lại
        appOpenAdManager.showAdIfAvailable {
            println("App open ad finished or not available")
        }
    }
}
```

## 8. Ad Unit IDs có sẵn

```kotlin
AdUnitId.BANNER_DEFAULT
AdUnitId.NATIVE_DEFAULT  
AdUnitId.INTERSTITIAL_DEFAULT
AdUnitId.REWARDED_DEFAULT
AdUnitId.REWARDED_INTERSTITIAL_DEFAULT
AdUnitId.APP_OPEN_DEFAULT
```

## 9. Ví dụ tổng hợp - App hoàn chỉnh

```kotlin
class AdsManager {
    // Interstitial
    private var interstitialAd: InterstitialAdResult? = null
    
    // Rewarded
    private var rewardAd: RewardAdResult? = null
    
    // App Open
    private var appOpenAd: AppOpenAdResult? = null
    
    private val scope = CoroutineScope(Dispatchers.Main)
    
    suspend fun initialize(): Boolean {
        val result = KmpAdmobAdapter.asyncInitialize()
        if (result.isSuccess) {
            loadAllAds()
            return true
        }
        return false
    }
    
    private fun loadAllAds() {
        loadInterstitial()
        loadReward()
        loadAppOpen()
    }
    
    private fun loadInterstitial() {
        scope.launch {
            val result = KmpAdmobAdapter.loadInterstitialAd(AdUnitId.INTERSTITIAL_DEFAULT)
            result.fold(
                onSuccess = { interstitialAd = it },
                onFailure = { println("Load interstitial failed: ${it.message}") }
            )
        }
    }
    
    private fun loadReward() {
        scope.launch {
            val result = KmpAdmobAdapter.loadRewardAd(AdUnitId.REWARDED_DEFAULT)
            result.fold(
                onSuccess = { rewardAd = it },
                onFailure = { println("Load reward failed: ${it.message}") }
            )
        }
    }
    
    private fun loadAppOpen() {
        scope.launch {
            val result = KmpAdmobAdapter.loadAppOpenAd(AdUnitId.APP_OPEN_DEFAULT)
            result.fold(
                onSuccess = { appOpenAd = it },
                onFailure = { println("Load app open failed: ${it.message}") }
            )
        }
    }
    
    fun showInterstitial(onClosed: () -> Unit) {
        interstitialAd?.let { ad ->
            KmpAdmobAdapter.showInterstitialAd(ad, object : InterstitialAdShowCallback {
                override fun onAdClose() {
                    interstitialAd = null
                    onClosed()
                    loadInterstitial() // Preload cho lần sau
                }
                override fun onAdShowFailed(adError: AdError) {
                    interstitialAd = null
                    onClosed()
                }
                override fun onAdImpression() {}
                override fun onAdClicked() {}
                override fun onNextAction() {}
            })
        } ?: onClosed()
    }
    
    fun showReward(onReward: (Int, String) -> Unit, onClosed: () -> Unit) {
        rewardAd?.let { ad ->
            KmpAdmobAdapter.showRewardAd(ad, object : RewardAdShowCallback {
                override fun onUserEarnedReward(amount: Int, type: String) {
                    onReward(amount, type)
                }
                override fun onAdClose() {
                    rewardAd = null
                    onClosed()
                    loadReward() // Preload cho lần sau
                }
                override fun onAdShowFailed(adError: AdError) {
                    rewardAd = null
                    onClosed()
                }
                override fun onAdImpression() {}
                override fun onAdClicked() {}
                override fun onNextAction() {}
            })
        } ?: onClosed()
    }
    
    fun showAppOpen(onClosed: () -> Unit) {
        appOpenAd?.let { ad ->
            KmpAdmobAdapter.showAppOpenAd(ad, object : AppOpenAdShowCallback {
                override fun onAdClose() {
                    appOpenAd = null
                    onClosed()
                    loadAppOpen() // Preload cho lần sau
                }
                override fun onAdShowFailed(adError: AdError) {
                    appOpenAd = null
                    onClosed()
                }
                override fun onAdImpression() {}
                override fun onAdClicked() {}
                override fun onNextAction() {}
            })
        } ?: onClosed()
    }
    
    fun isInterstitialReady() = interstitialAd != null
    fun isRewardReady() = rewardAd != null
    fun isAppOpenReady() = appOpenAd != null
}

@Composable
fun MainScreen() {
    val adsManager = remember { AdsManager() }
    val bannerState = rememberBannerAdState()
    val nativeState = rememberNativeAdState()
    var coins by remember { mutableStateOf(100) }
    
    LaunchedEffect(Unit) {
        adsManager.initialize()
        bannerState.loadAd()
        nativeState.loadAd()
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Coins: $coins", fontSize = 24.sp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Reward Button
        Button(
            onClick = {
                adsManager.showReward(
                    onReward = { amount, type ->
                        coins += amount
                    },
                    onClosed = {}
                )
            },
            enabled = adsManager.isRewardReady()
        ) {
            Text("Watch Ad for Coins")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Interstitial Button
        Button(
            onClick = {
                adsManager.showInterstitial {
                    println("Interstitial closed")
                }
            },
            enabled = adsManager.isInterstitialReady()
        ) {
            Text("Show Interstitial")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Native Ad
        if (nativeState.uiState.shouldShowAd) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = 4.dp
            ) {
                PlatformAwareNativeAd(
                    adUiState = nativeState.uiState,
                    androidContent = {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Headline(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(8.dp))
                            MediaView(modifier = Modifier.size(200.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Body(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(8.dp))
                            CallToAction(modifier = Modifier.fillMaxWidth())
                        }
                    },
                    iosLayoutStyle = NativeAdLayoutStyle.FULLSCREEN
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Banner Ad
        BannerAdLayout(
            adUiState = bannerState.uiState,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            adSize = AdSize.BANNER
        )
    }
}
```

## Lưu ý quan trọng:
- **Kiểm tra `isInitialized`** trước khi load
- **Banner/Native** dùng Compose State
- **Interstitial/Reward/AppOpen** dùng callback pattern
- **Preload** giúp ads load nhanh hơn, nên dùng cho các màn hình quan trọng
- **Luôn có fallback** khi ads load thất bại
- **Preload lại ads** sau khi show để sẵn sàng cho lần sau