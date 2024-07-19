package com.gurpreetsk.jobmatchingpos

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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

private const val FINAL_TRANSLATION_VALUE = 10000f
private const val FINAL_ROTATION_DEGREE = 15f

data class Card(val id: String) {

    val color: Color = getRandomColor()

    private fun getRandomColor(): Color {
        val colors = listOf(Color.Yellow, Color.Green, Color.Blue, Color.Red, Color.Magenta)
        return colors.random()
    }
}

@Composable
fun CardStack(
    cards: List<Card>,
    onAction: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val frontCard = cards.firstOrNull()
        val stack = cards.drop(1)

        val rotationZ = remember { Animatable(0f) }
        val alpha = remember { Animatable(1f) }

        stack.reversed().forEachIndexed { _, card ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 24.dp)
                    .background(Color.Magenta)
                    .align(Alignment.Center)
            ) {
                Text(text = card.id)
            }
        }

        LaunchedEffect(key1 = frontCard) {
            alpha.animateTo(1f)
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .animateContentSize()
                .padding(top = 16.dp)
                .graphicsLayer {
                    this.rotationZ = rotationZ.value
                    this.alpha = alpha.value
                }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Blue)
                    .align(Alignment.Center)
            ) {
                Text(text = frontCard!!.id)
            }
        }

        Row(modifier = Modifier.align(Alignment.BottomCenter)) {
            val coroutineScope = rememberCoroutineScope()

            val negativeButtonOffsetX = remember { Animatable(0f) }
            val negativeButtonOffsetY = remember { Animatable(0f) }
            val positiveButtonOffsetX = remember { Animatable(0f) }
            val positiveButtonOffsetY = remember { Animatable(0f) }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .offset {
                        IntOffset(negativeButtonOffsetX.value.toInt(), negativeButtonOffsetY.value.toInt())
                    }
            ) {
                val density = LocalDensity.current
                // All values in px.
                val screenWidth = with (density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
                val screenHeight = with (density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
                val topMargin = with (density) { 144.dp.toPx() }
                val selfWidth = with (density) { 32.dp.toPx() }
                val selfHeight = with (density) { 32.dp.toPx() }
                val spacerWidth = with (density) { 8.dp.toPx() }

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
                                negativeButtonOffsetX.animateTo(selfWidth + spacerWidth)
                            }
                            coroutineScope.launch {
                                negativeButtonOffsetY.animateTo(-(screenHeight - selfHeight - topMargin))
                            }

                            // Action
                            coroutineScope.launch {
                                delay(2000)
                                onAction(frontCard!!.id)
                                alpha.animateTo(0f)
                            }

                            // Cleanup
                            coroutineScope.launch {
                                delay(2000)
                                rotationZ.animateTo(0f)
                            }
                            coroutineScope.launch {
                                delay(2000)
                                negativeButtonOffsetX.animateTo(0f)
                                positiveButtonOffsetX.animateTo(0f)
                            }
                            coroutineScope.launch {
                                delay(2000)
                                negativeButtonOffsetY.animateTo(0f)
                                positiveButtonOffsetY.animateTo(0f)
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
                        IntOffset(positiveButtonOffsetX.value.toInt(), positiveButtonOffsetY.value.toInt())
                    }
                    .graphicsLayer {
                        this.scaleX = positiveButtonScaling.value
                        this.scaleY = positiveButtonScaling.value
                    }
            ) {
                val density = LocalDensity.current
                // All values in px.
                val screenWidth = with (density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
                val screenHeight = with (density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
                val topMargin = with (density) { 144.dp.toPx() }
                val selfWidth = with (density) { 32.dp.toPx() }
                val selfHeight = with (density) { 32.dp.toPx() }

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
                                positiveButtonOffsetX.animateTo(selfWidth * 2)
                            }
                            coroutineScope.launch {
                                positiveButtonOffsetY.animateTo(-(screenHeight - selfHeight - topMargin))
                            }

                            // Action
                            coroutineScope.launch {
                                delay(2000)
                                onAction(frontCard!!.id)
                                alpha.animateTo(0f)
                            }

                            // Cleanup
                            coroutineScope.launch {
                                delay(2000)
                                rotationZ.snapTo(0f)
                            }
                            coroutineScope.launch {
                                delay(2000)
                                positiveButtonScaling.animateTo(1f)
                            }
                            coroutineScope.launch {
                                delay(2000)
                                positiveButtonOffsetX.animateTo(0f)
                                negativeButtonOffsetX.animateTo(0f)
                            }
                            coroutineScope.launch {
                                delay(2000)
                                positiveButtonOffsetY.animateTo(0f)
                                negativeButtonOffsetY.animateTo(0f)
                            }
                        }
                ) {
                    Text(text = "+")
                }
            }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}
