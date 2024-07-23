package com.gurpreetsk.jobmatchingpos.internal

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.gurpreetsk.jobmatchingpos.ActionButtonState
import com.gurpreetsk.jobmatchingpos.Card
import com.gurpreetsk.jobmatchingpos.CardState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val FINAL_ROTATION_DEGREE = 15f

@Composable
fun BoxScope.ActionButtons(
    cardState: CardState,
    onAction: (id: String) -> Unit,
    frontCard: Card
) {
    Row(modifier = Modifier.Companion.align(Alignment.BottomCenter)) {
        val coroutineScope = rememberCoroutineScope()

        val density = LocalDensity.current
        // All values in px.
        val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
        val topMargin = with(density) { 144.dp.toPx() }
        val selfWidth = with(density) { 32.dp.toPx() }
        val selfHeight = with(density) { 32.dp.toPx() }
        val spacerWidth = with(density) { 8.dp.toPx() }

        val negativeButtonState by remember(frontCard) { mutableStateOf(ActionButtonState.default()) }
        val positiveButtonState by remember(frontCard) { mutableStateOf(ActionButtonState.default()) }
        val positiveButtonScaling = remember(frontCard) { Animatable(1f) }

        LaunchedEffect(key1 = cardState.progress.value) {
            val progress = cardState.progress.value / 100

            when (cardState.progress.direction) {
                CardState.Direction.LEFT -> {
                    coroutineScope.launch {
                        negativeButtonState.offset.first.animateTo((selfWidth + spacerWidth) * progress)
                    }
                    coroutineScope.launch {
                        negativeButtonState.offset.second.animateTo(-(screenHeight - selfHeight - topMargin) * progress)
                    }
                    coroutineScope.launch {
                        positiveButtonState.offset.first.animateTo((selfWidth + spacerWidth) * progress)
                    }
                    coroutineScope.launch {
                        positiveButtonState.offset.second.animateTo(-(screenHeight - selfHeight - topMargin) * progress)
                    }
                    coroutineScope.launch {
                        positiveButtonState.alpha.animateTo(1 - progress)
                    }
                }

                CardState.Direction.RIGHT -> {
                    coroutineScope.launch {
                        positiveButtonScaling.animateTo(1f + (0.3f) * progress)
                        // Pulsate
                        try {
                            positiveButtonScaling.animateTo(
                                1.0f,
                                infiniteRepeatable(
                                    animation = tween(500),
                                    repeatMode = RepeatMode.Reverse,
                                    initialStartOffset = StartOffset(300)
                                )
                            )
                        } catch (e: Exception) {
                            Log.e(null, e.message, e)
                        }
                    }
                    coroutineScope.launch {
                        positiveButtonState.offset.first.animateTo(selfWidth * 2 * progress)
                    }
                    coroutineScope.launch {
                        positiveButtonState.offset.second.animateTo(-(screenHeight - selfHeight - topMargin) * progress)
                    }
                    coroutineScope.launch {
                        negativeButtonState.alpha.animateTo(1 - progress)
                    }
                }

                else -> {
                    if (cardState.progress.isLocked) return@LaunchedEffect

                    coroutineScope.launch {
                        negativeButtonState.offset.first.animateTo(0f)
                    }
                    coroutineScope.launch {
                        negativeButtonState.offset.second.animateTo(0f)
                    }
                    coroutineScope.launch {
                        positiveButtonState.offset.first.animateTo(0f)
                    }
                    coroutineScope.launch {
                        positiveButtonState.offset.second.animateTo(0f)
                    }
                    coroutineScope.launch {
                        positiveButtonState.alpha.animateTo(1f)
                    }
                    coroutineScope.launch {
                        negativeButtonState.alpha.animateTo(1f)
                    }
                    coroutineScope.launch {
                        positiveButtonScaling.animateTo(1f)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .size(64.dp)
                .offset {
                    IntOffset(
                        negativeButtonState.offset.first.value.toInt(),
                        negativeButtonState.offset.second.value.toInt()
                    )
                }
                .graphicsLayer { this.alpha = negativeButtonState.alpha.value }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.8f))
                    .clickable {
                        coroutineScope.launch {
                            cardState.rotationZ.animateTo(FINAL_ROTATION_DEGREE.unaryMinus())
                        }
                        coroutineScope.launch {
                            negativeButtonState.offset.first.animateTo(selfWidth + spacerWidth)
                        }
                        coroutineScope.launch {
                            negativeButtonState.offset.second.animateTo(-(screenHeight - selfHeight - topMargin))
                        }
                        coroutineScope.launch {
                            positiveButtonState.offset.first.animateTo(selfWidth + spacerWidth)
                        }
                        coroutineScope.launch {
                            positiveButtonState.offset.second.animateTo(-(screenHeight - selfHeight - topMargin))
                        }
                        coroutineScope.launch {
                            positiveButtonState.alpha.animateTo(0f)
                        }

                        // Action
                        coroutineScope.launch {
                            delay(2000)
                            onAction(frontCard.id)
                            cardState.alpha.animateTo(0f)
                        }

                        // Cleanup
                        coroutineScope.launch {
                            delay(2000)
                            cardState.rotationZ.animateTo(0f)
                        }
                        coroutineScope.launch {
                            delay(2000)
                            negativeButtonState.offset.first.animateTo(0f)
                        }
                        coroutineScope.launch {
                            delay(2000)
                            negativeButtonState.offset.second.animateTo(0f)
                        }
                        coroutineScope.launch {
                            delay(2000)
                            positiveButtonState.offset.first.animateTo(0f)
                        }
                        coroutineScope.launch {
                            delay(2000)
                            positiveButtonState.offset.second.animateTo(0f)
                        }
                        coroutineScope.launch {
                            delay(2000)
                            positiveButtonState.alpha.animateTo(1f)
                        }
                    }
            ) {
                Text(text = "-")
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .offset {
                    IntOffset(
                        positiveButtonState.offset.first.value.toInt(),
                        positiveButtonState.offset.second.value.toInt()
                    )
                }
                .graphicsLayer {
                    this.scaleX = positiveButtonScaling.value
                    this.scaleY = positiveButtonScaling.value
                    this.alpha = positiveButtonState.alpha.value
                }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.8f))
                    .clickable {
                        coroutineScope.launch {
                            cardState.rotationZ.animateTo(FINAL_ROTATION_DEGREE)
                        }
                        coroutineScope.launch {
                            positiveButtonScaling.animateTo(1.3f)
                        }
                        coroutineScope.launch {
                            positiveButtonState.offset.first.animateTo(selfWidth * 2)
                        }
                        coroutineScope.launch {
                            positiveButtonState.offset.second.animateTo(-(screenHeight - selfHeight - topMargin))
                        }
                        coroutineScope.launch {
                            negativeButtonState.alpha.animateTo(0f)
                        }
                        coroutineScope.launch {
                            // Pulsate
                            try {
                                positiveButtonScaling.animateTo(
                                    1.4f,
                                    infiniteRepeatable(
                                        animation = tween(500),
                                        repeatMode = RepeatMode.Reverse,
                                        initialStartOffset = StartOffset(300)
                                    )
                                )
                            } catch (e: Exception) {
                                Log.e(null, e.message, e)
                            }
                        }

                        // Action
                        coroutineScope.launch {
                            delay(5000)
                            onAction(frontCard.id)
                            cardState.alpha.animateTo(0f)
                        }

                        // Cleanup
                        coroutineScope.launch {
                            delay(5000)
                            cardState.rotationZ.animateTo(0f)
                        }
                        coroutineScope.launch {
                            delay(5000)
                            positiveButtonScaling.animateTo(1f)
                        }
                        coroutineScope.launch {
                            delay(5000)
                            positiveButtonState.offset.first.animateTo(0f)
                        }
                        coroutineScope.launch {
                            delay(5000)
                            positiveButtonState.offset.second.animateTo(0f)
                        }
                        coroutineScope.launch {
                            delay(5000)
                            negativeButtonState.alpha.animateTo(1f)
                        }
                    }
            ) {
                Text(text = "+")
            }
        }
    }
}
