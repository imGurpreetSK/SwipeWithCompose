package com.gurpreetsk.jobmatchingpos

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
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

        val translationX = remember { Animatable(0f) }
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
                    this.translationX = translationX.value
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

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White.copy(alpha = 0.8f))
                    .clickable {
                        coroutineScope.launch {
                            rotationZ.animateTo(FINAL_ROTATION_DEGREE.unaryMinus())
                        }
                        coroutineScope.launch {
                            delay(100)
                            translationX.animateTo(FINAL_TRANSLATION_VALUE.unaryMinus())
                        }
                        coroutineScope.launch {
                            delay(200)
                            alpha.snapTo(0f)
                            translationX.snapTo(0f)
                            rotationZ.snapTo(0f)
                        }

                        coroutineScope.launch {
                            delay(500)
                            onAction(frontCard!!.id)
                        }
                    }
            ) {
                Text(text = "-")
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White.copy(alpha = 0.8f))
                    .clickable {
                        coroutineScope.launch {
                            rotationZ.animateTo(FINAL_ROTATION_DEGREE)
                        }
                        coroutineScope.launch {
                            delay(100)
                            translationX.animateTo(FINAL_TRANSLATION_VALUE)
                        }

                        coroutineScope.launch {
                            delay(500)
                            onAction(frontCard!!.id)
                            alpha.snapTo(0f)
                            translationX.snapTo(0f)
                            rotationZ.snapTo(0f)
                        }
                    }
            ) {
                Text(text = "+")
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
