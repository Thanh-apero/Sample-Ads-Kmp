package com.apero.kmm.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apero.sdk.ads.kmp.api.composable.Body
import com.apero.sdk.ads.kmp.api.composable.CallToAction
import com.apero.sdk.ads.kmp.api.composable.Headline
import com.apero.sdk.ads.kmp.api.composable.MediaView
import com.apero.sdk.ads.kmp.api.composable.NativeAdLayoutStyle
import com.apero.sdk.ads.kmp.api.composable.NativeAdState
import com.apero.sdk.ads.kmp.api.composable.PlatformAwareNativeAd

@Composable
fun CrossPlatformNativeAd(nativeAdState: NativeAdState) {
    PlatformAwareNativeAd(
        adUiState = nativeAdState.uiState,
        androidContent = {
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
        iosLayoutStyle = NativeAdLayoutStyle.FULLSCREEN
    )
}