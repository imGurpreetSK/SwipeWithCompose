package com.gurpreetsk.jobmatchingpos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gurpreetsk.jobmatchingpos.internal.ActionButtons
import com.gurpreetsk.jobmatchingpos.internal.CardsStack
import com.gurpreetsk.jobmatchingpos.internal.FrontCard
import com.gurpreetsk.jobmatchingpos.ui.theme.JobMatchingPocTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

const val LOCK_THRESHOLD = 98f

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

        var cardState by remember(frontCard) { mutableStateOf(CardState.default()) }
        LaunchedEffect(key1 = frontCard) {
            cardState.alpha.animateTo(1f)
        }

        val coroutineScope = rememberCoroutineScope()
        FrontCard(
            cardState,
            frontCard!!,
            { id, direction ->
                cardState = cardState.copy(progress = CardState.Progress(100f, direction))
                coroutineScope.launch {
                    delay(if (direction == CardState.Direction.LEFT) 1000 else 3000)
                    onAction(id)
                }
            }
        ) { progress, direction ->
            cardState = cardState.copy(
                progress = cardState.progress.copy(
                    value = progress.absoluteValue.coerceIn(0f, 100f),
                    direction = direction
                )
            )

            if (!cardState.progress.isLocked) {
                coroutineScope.launch {
                    val rotation = progress / 6.6f // Magic number for degrees of rotation for card: 0 - ~15.

                    @Suppress("NAME_SHADOWING") // Explicit name shadowing.
                    val direction = direction?.multiplier?.toFloat() ?: 0f
                    cardState.rotationZ.animateTo(rotation * direction)
                }
            }
        }

        ActionButtons(cardState, frontCard) { id, direction ->
            cardState = cardState.copy(progress = CardState.Progress(100f, direction))
            delay(3000)
            onAction(id)
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
