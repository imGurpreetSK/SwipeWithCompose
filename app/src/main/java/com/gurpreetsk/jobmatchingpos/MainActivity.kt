package com.gurpreetsk.jobmatchingpos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gurpreetsk.jobmatchingpos.ui.theme.JobMatchingPocTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JobMatchingPocTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val cards = remember {
                        mutableStateListOf(Card("1"), Card("2"), Card("3"), Card("4"), Card("5"))
                    }
                    var toAdd by remember { mutableIntStateOf(6) }
                    CardStack(
                        cards = cards,
                        onAction = { cards.removeFirst() },
                        onRestock = {
                            cards.add(Card(toAdd.toString()))
                            cards.add(Card((toAdd + 1).toString()))
                            toAdd += 2
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
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
