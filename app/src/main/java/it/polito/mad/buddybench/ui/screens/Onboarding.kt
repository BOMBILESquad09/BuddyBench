package it.polito.mad.buddybench.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import it.polito.mad.buddybench.ui.theme.PADDING_MD

@Composable
fun Onboarding() {
    Column(
        modifier = Modifier.fillMaxHeight(0.7f),
        verticalArrangement = Arrangement.spacedBy(PADDING_MD)
    ) {
        Text(text = "Hi", style = MaterialTheme.typography.titleLarge)
        Text(text = "Welcome to BuddyBench, here you can book your next sport session with your friends", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Enter your name to get started", style = MaterialTheme.typography.bodySmall)
    }
}