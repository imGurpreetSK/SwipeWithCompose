package com.gurpreetsk.jobmatchingpos.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gurpreetsk.jobmatchingpos.Card

@Composable
fun BoxScope.CardsStack(stack: List<Card>) {
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
