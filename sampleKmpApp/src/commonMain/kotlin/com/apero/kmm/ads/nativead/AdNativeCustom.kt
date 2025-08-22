package com.apero.kmm.ads.nativead

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.apero.sdk.ads.kmp.api.composable.NativeAdUiState

@Composable
public expect fun AdNativeCustom(
    adUiState: NativeAdUiState,
    modifier: Modifier,
)