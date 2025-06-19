package com.apero.kmm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apero.kmm.ads.CrossPlatformNativeAd
import com.apero.sdk.ads.kmp.api.composable.*

@Composable
fun MainScreen(
    onNavigateToScreenA: () -> Unit
) {
    var selectedOption by remember { mutableStateOf(1) } // Start with Vietnamese selected (index 1)
    val nativeAdState = rememberNativeAdState()
    val loadedOptions = remember { mutableSetOf<Int>() }

    // Language options with flag emojis and names
    val languageOptions = listOf(
        "🇬🇧" to "English",
        "🇻🇳" to "Vietnamese",
        "🇮🇳" to "हिंदी",
        "🇧🇷" to "Português (Brasil)",
        "🇪🇸" to "Español",
        "🇮🇩" to "Indonesian"
    )

    LaunchedEffect(Unit) {
        nativeAdState.loadAd()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Main content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Chọn ngôn ngữ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    IconButton(
                        onClick = onNavigateToScreenA,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Check"
                        )
                    }
                }

                // Language options
                languageOptions.forEachIndexed { index, (flag, language) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .selectable(
                                selected = selectedOption == index,
                                onClick = {
                                    val previousSelection = selectedOption
                                    selectedOption = index

                                    // Load native ad only if this option hasn't been loaded before
                                    // OR if switching from a different option
                                    if (!loadedOptions.contains(index) || previousSelection != index) {
                                        if (!loadedOptions.contains(index)) {
                                            loadedOptions.add(index)
                                        }
                                        // Reset ads state trước khi load ads mới
                                        nativeAdState.reset()
                                        nativeAdState.loadAd()
                                    }
                                }
                            ),
                        elevation = 2.dp,
                        backgroundColor = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Flag emoji
                            Text(
                                text = flag,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )

                            // Language name
                            Text(
                                text = language,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )

                            // Selection indicator
                            if (selectedOption == index) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color(0xFF2196F3), // Blue color
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(
                                            Color.Gray.copy(alpha = 0.3f),
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Ad section
            if (nativeAdState.uiState.shouldShowAd || nativeAdState.uiState.shouldShowShimmer) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    CrossPlatformNativeAd(nativeAdState)
                }
            }
        }
    }
}
