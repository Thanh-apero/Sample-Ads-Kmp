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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apero.kmm.ads.CrossPlatformNativeAd
import com.apero.sdk.ads.kmp.api.composable.*

@Composable
fun MainScreen(
    onNavigateToScreenA: () -> Unit
) {
    var selectedOption by remember { mutableStateOf(-1) } // Start with no selection
    val nativeAdState = rememberNativeAdState()
    val loadedOptions = remember { mutableSetOf<Int>() }

    val radioOptions = listOf("Option 1", "Option 2", "Option 3")

    LaunchedEffect(Unit) {
        nativeAdState.loadAd()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Main Screen") },
                backgroundColor = MaterialTheme.colors.primary,
                actions = {
                    IconButton(
                        onClick = onNavigateToScreenA
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done - Start ViewPager Flow",
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
            // Main content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select an option:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Radio buttons
                radioOptions.forEachIndexed { index, text ->
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
                        elevation = if (selectedOption == index) 8.dp else 2.dp,
                        backgroundColor = if (selectedOption == index)
                            MaterialTheme.colors.primary.copy(alpha = 0.1f)
                        else
                            Color.White
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOption == index,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colors.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = text,
                                fontSize = 16.sp,
                                fontWeight = if (selectedOption == index)
                                    FontWeight.Bold
                                else
                                    FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button để reload ads
                Button(
                    onClick = {
                        nativeAdState.reset()
                        nativeAdState.loadAd()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reload Ads")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Hiển thị trạng thái ads để debug
                Text(
                    text = buildString {
                        append("Ads Status: ")
                        when {
                            nativeAdState.uiState.shouldShowShimmer -> append("Loading...")
                            nativeAdState.uiState.shouldShowError -> append("Error: ${nativeAdState.uiState.errorMessage}")
                            nativeAdState.uiState.shouldShowAd -> append("Loaded Successfully")
                            else -> append("Empty/Idle")
                        }
                    },
                    fontSize = 12.sp,
                    color = when {
                        nativeAdState.uiState.shouldShowShimmer -> Color.Blue
                        nativeAdState.uiState.shouldShowError -> Color.Red
                        nativeAdState.uiState.shouldShowAd -> Color.Green
                        else -> Color.Gray
                    },
                    modifier = Modifier.padding(8.dp)
                )
            }

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
}
