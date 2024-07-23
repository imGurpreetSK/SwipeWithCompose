package com.gurpreetsk.jobmatchingpos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.runtime.Stable
import kotlin.math.absoluteValue

data class Card(val id: String)

@Stable
data class CardState(
    val rotationZ: Animatable<Float, AnimationVector1D>,
    val alpha: Animatable<Float, AnimationVector1D>,
    val progress: Progress
) {
    @Stable
    data class Progress(
        val value: Float,
        val direction: Direction?,
        val isLocked: Boolean = value.absoluteValue >= MAX_PROGRESS
    )

    enum class Direction(val multiplier: Int) {
        RIGHT(1),
        LEFT(-1)
    }

    companion object {
        fun default(): CardState {
            val rotationZ = Animatable(0f)
            val cardAlpha = Animatable(1f)

            return CardState(
                rotationZ,
                cardAlpha,
                Progress(0f, null)
            )
        }
    }
}

@Stable
data class ActionButtonState(
    val offset: Pair<Animatable<Float, AnimationVector1D>, Animatable<Float, AnimationVector1D>>, // (x,y) offsets.
    val alpha: Animatable<Float, AnimationVector1D>
) {
    companion object {
        fun default(): ActionButtonState {
            val negativeButtonOffsetX = Animatable(0f)
            val negativeButtonOffsetY = Animatable(0f)
            val negativeButtonAlpha = Animatable(1f)

            return ActionButtonState(negativeButtonOffsetX to negativeButtonOffsetY, negativeButtonAlpha)
        }
    }
}
