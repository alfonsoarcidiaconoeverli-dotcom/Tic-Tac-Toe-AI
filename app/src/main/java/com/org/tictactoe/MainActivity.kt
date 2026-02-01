package com.org.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.MobileAds
import com.org.tictactoe.ui.theme.TicTacToeTheme

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {

    private var feedbackManager: FeedbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // AdMob init (ok)
        MobileAds.initialize(this)

        feedbackManager = FeedbackManager(this)

        setContent {
            TicTacToeTheme {

                var currentScreen by remember { mutableStateOf("menu") }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        // Altezza stabile = banner sempre visibile
                        Surface(tonalElevation = 0.dp) {
                            AdMobBanner(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            )
                        }
                    }
                ) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "screen"
                        ) { screen ->

                            when (screen) {
                                "menu" -> PremiumMenu(
                                    onStartClick = { currentScreen = "game" },
                                    onStartAIClick = { currentScreen = "ai" }
                                )

                                "game" -> {
                                    feedbackManager?.let { feedback ->
                                        TicTacToeGame(
                                            gameState = GameState(),
                                            feedbackManager = feedback,
                                            onBackClick = { currentScreen = "menu" }
                                        )
                                    }
                                }

                                "ai" -> {
                                    feedbackManager?.let { feedback ->
                                        AIGameScreen(
                                            feedbackManager = feedback,
                                            onBackClick = { currentScreen = "menu" }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        feedbackManager?.release()
        feedbackManager = null
    }
}

@Composable
private fun PremiumMenu(
    onStartClick: () -> Unit,
    onStartAIClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.menu_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.menu_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Text(text = stringResource(id = R.string.start_game))
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onStartAIClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Text(text = stringResource(id = R.string.start_ai))
        }
    }
}                    
