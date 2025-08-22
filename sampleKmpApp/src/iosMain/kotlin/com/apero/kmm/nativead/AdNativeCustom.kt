package com.apero.kmm.ads.nativead

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.apero.sdk.ads.kmp.api.composable.CustomNativeAdDelegate
import com.apero.sdk.ads.kmp.api.composable.NativeAdLayoutIOS
import com.apero.sdk.ads.kmp.api.composable.NativeAdUiState
import cocoapods.Google_Mobile_Ads_SDK.GADNativeAdView
import com.apero.sdk.ads.kmp.api.composable.setupFullscreenLayout
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
public actual fun AdNativeCustom(
    adUiState: NativeAdUiState,
    modifier: Modifier
) {
    NativeAdLayoutIOS(
        adUiState,
        modifier
    ) { nativeAd ->
        val nativeAdView = GADNativeAdView()
        nativeAd.delegate = CustomNativeAdDelegate()
        setupFullscreenLayout(nativeAdView, nativeAd)
        nativeAdView.nativeAd = nativeAd
        nativeAdView.apply {
            setNeedsLayout()
            layoutIfNeeded()
        }
    }
}