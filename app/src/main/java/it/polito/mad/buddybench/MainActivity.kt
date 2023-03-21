package it.polito.mad.buddybench

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import it.polito.mad.buddybench.ui.screens.Onboarding
import it.polito.mad.buddybench.ui.theme.BuddyBenchTheme
import it.polito.mad.buddybench.ui.theme.PADDING_LG

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BuddyBenchTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize().padding(PADDING_LG),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Onboarding()
                }
            }
        }
    }
}