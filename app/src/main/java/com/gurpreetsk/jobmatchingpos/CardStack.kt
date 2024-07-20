package com.gurpreetsk.jobmatchingpos

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.gurpreetsk.jobmatchingpos.ui.theme.JobMatchingPocTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val FINAL_ROTATION_DEGREE = 15f

data class Card(val id: String)

data class CardState(
    val rotationZ: Animatable<Float, AnimationVector1D>,
    val alpha: Animatable<Float, AnimationVector1D>
)

data class ActionButtonState(
    val offset: Pair<Animatable<Float, AnimationVector1D>, Animatable<Float, AnimationVector1D>>, // (x,y) offsets.
    val alpha: Animatable<Float, AnimationVector1D>
)

@Composable
fun CardStack(
    cards: List<Card>,
    onAction: (id: String) -> Unit,
    onRestock: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val frontCard = cards.firstOrNull()
        val stack = cards.drop(1)
        LaunchedEffect(key1 = stack.isEmpty()) {
            if (stack.isEmpty()) {
                onRestock()
            }
        }

        CardsStack(stack)

        val cardState = remember { getCardState() }
        LaunchedEffect(key1 = frontCard) {
            cardState.alpha.animateTo(1f)
        }

        FrontCard(cardState.rotationZ, cardState.alpha, frontCard!!)

        ActionButtons(cardState.rotationZ, onAction, frontCard, cardState.alpha)
    }
}

private fun getCardState(): CardState {
    val rotationZ = Animatable(0f)
    val cardAlpha = Animatable(1f)
    return CardState(rotationZ, cardAlpha)
}

@Composable
private fun BoxScope.ActionButtons(
    rotationZ: Animatable<Float, AnimationVector1D>,
    onAction: (id: String) -> Unit,
    frontCard: Card,
    cardAlpha: Animatable<Float, AnimationVector1D>
) {
    Row(modifier = Modifier.Companion.align(Alignment.BottomCenter)) {
        val coroutineScope = rememberCoroutineScope()

        val negativeButtonState = remember { getActionButtonState() }
        val positiveButtonState = remember { getActionButtonState() }

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
            val density = LocalDensity.current
            // All values in px.
            val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
            val topMargin = with(density) { 144.dp.toPx() }
            val selfWidth = with(density) { 32.dp.toPx() }
            val selfHeight = with(density) { 32.dp.toPx() }
            val spacerWidth = with(density) { 8.dp.toPx() }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.8f))
                    .clickable {
                        coroutineScope.launch {
                            rotationZ.animateTo(FINAL_ROTATION_DEGREE.unaryMinus())
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
                            cardAlpha.animateTo(0f)
                        }

                        // Cleanup
                        coroutineScope.launch {
                            delay(2000)
                            rotationZ.animateTo(0f)
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

        val positiveButtonScaling = remember { Animatable(1f) }
        Box(
            modifier = Modifier
                .size(64.dp)
                .offset {
                    IntOffset(
                        positiveButtonState.offset.first.value.toInt(), positiveButtonState.offset.second.value
                            .toInt
                                ()
                    )
                }
                .graphicsLayer {
                    this.scaleX = positiveButtonScaling.value
                    this.scaleY = positiveButtonScaling.value
                    this.alpha = positiveButtonState.alpha.value
                }
        ) {
            val density = LocalDensity.current
            // All values in px.
            val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
            val topMargin = with(density) { 144.dp.toPx() }
            val selfWidth = with(density) { 32.dp.toPx() }
            val selfHeight = with(density) { 32.dp.toPx() }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.8f))
                    .clickable {
                        coroutineScope.launch {
                            rotationZ.animateTo(FINAL_ROTATION_DEGREE)
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
                            cardAlpha.animateTo(0f)
                        }

                        // Cleanup
                        coroutineScope.launch {
                            delay(5000)
                            rotationZ.snapTo(0f)
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

private fun getActionButtonState(): ActionButtonState {
    val negativeButtonOffsetX = Animatable(0f)
    val negativeButtonOffsetY = Animatable(0f)
    val negativeButtonAlpha = Animatable(1f)

    return ActionButtonState(negativeButtonOffsetX to negativeButtonOffsetY, negativeButtonAlpha)
}

@Composable
private fun BoxScope.FrontCard(
    rotationZ: Animatable<Float, AnimationVector1D>,
    cardAlpha: Animatable<Float, AnimationVector1D>,
    frontCard: Card
) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .animateContentSize()
            .padding(top = 16.dp)
            .graphicsLayer {
                this.rotationZ = rotationZ.value
                this.alpha = cardAlpha.value
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .matchParentSize()
                .background(Color.Blue)
                .align(Alignment.Center)
        ) {
            Text(text = frontCard.id)
        }
    }
}

@Composable
private fun BoxScope.CardsStack(stack: List<Card>) {
    stack.reversed().forEachIndexed { _, card ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.Companion
                .matchParentSize()
                .padding(horizontal = 24.dp)
                .background(Color.Magenta)
                .align(Alignment.Center)
        ) {
            Text(text = card.id)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CardStackPreview() {
    JobMatchingPocTheme {
        val cards = listOf(Card("1"), Card("2"), Card("3"), Card("4"), Card("5"))
        CardStack(
            cards = cards,
            onAction = {},
            onRestock = {},
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}
