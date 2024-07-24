package com.gurpreetsk.jobmatchingpos.internal

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import com.gurpreetsk.jobmatchingpos.Card
import com.gurpreetsk.jobmatchingpos.CardState
import com.gurpreetsk.jobmatchingpos.LOCK_THRESHOLD
import kotlin.math.absoluteValue

private const val CARD_SWIPE_THRESHOLD = 779f // Magic number for card swipe; directly depends on FINAL_ROTATION_DEGREE.
private const val SENSITIVITY_FACTOR = 5f
private const val OFFSET_THRESHOLD = 30

@Composable
fun BoxScope.FrontCard(
    state: CardState,
    frontCard: Card,
    onLock: (id: String, direction: CardState.Direction) -> Unit,
    onDrag: (progress: Float, direction: CardState.Direction?) -> Unit,
) {
    var dragOffset by remember(frontCard) { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .matchParentSize()
            .animateContentSize()
            .padding(top = 16.dp)
            .graphicsLayer {
                this.rotationZ = state.rotationZ.value
                this.alpha = state.alpha.value
            }
    ) {
        val progressVector by remember(key1 = dragOffset, key2 = frontCard) {
            mutableFloatStateOf(((dragOffset / CARD_SWIPE_THRESHOLD) * 100).coerceIn(-100f, 100f))
        }
        LaunchedEffect(key1 = progressVector) {
            val direction = when {
                progressVector < 0 -> CardState.Direction.LEFT
                progressVector > 0 -> CardState.Direction.RIGHT
                else -> null
            }
            onDrag(progressVector.absoluteValue, direction)
        }

        // TODO(gs) - Find a better way. This is probably required due to lack of compose knowledge.
        // We want to trigger onLock(...) only once and then reset UI state post successful action.
        var shouldNotify by remember(key1 = frontCard) { mutableStateOf(true) }
        LaunchedEffect(key1 = progressVector) {
            if (shouldNotify) {
                when {
                    progressVector > LOCK_THRESHOLD -> {
                        onLock(frontCard.id, CardState.Direction.RIGHT)
                        shouldNotify = false
                    }

                    progressVector <= -LOCK_THRESHOLD -> {
                        onLock(frontCard.id, CardState.Direction.LEFT)
                        shouldNotify = false
                    }
                }
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .matchParentSize()
                .background(Color.Blue)
                .align(Alignment.Center)
                .detectHorizontalDragGesturesIfUnlocked(state, frontCard, { dragOffset = 0f }) {
                    dragOffset += it
                }
        ) {
            Text(text = frontCard.id)
        }
    }
}

private fun Modifier.detectHorizontalDragGesturesIfUnlocked(
    state: CardState,
    frontCard: Card,
    resetOffset: () -> Unit,
    updateOffset: (offset: Float) -> Unit
): Modifier {
    if (state.progress.isLocked) return this

    return pointerInput(frontCard.id) {
        detectHorizontalDragGestures(
            onDragEnd = { resetOffset() },
            onDragCancel = { resetOffset() },
        ) { change, dragAmount ->
            val offset = (dragAmount / density) * SENSITIVITY_FACTOR
            if (offset.absoluteValue > OFFSET_THRESHOLD) {
                updateOffset(offset)
            }

            if (change.positionChange() != Offset.Zero) {
                change.consume()
            }
        }
    }
}
