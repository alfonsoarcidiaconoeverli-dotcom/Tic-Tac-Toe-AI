package com.org.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.ads.MobileAds
import com.org.tictactoe.ui.theme.TicTacToeTheme

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {

    private var feedbackManager: FeedbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // AdMob init
        MobileAds.initialize(this)

        feedbackManager = FeedbackManager(this)

        setContent {
            TicTacToeTheme {
                var currentScreen by remember { mutableStateOf("menu") }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
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

                                "game" -> feedbackManager?.let { feedback ->
                                    TicTacToeGame(
                                        gameState = GameState(),
                                        feedbackManager = feedback,
                                        onBackClick = { currentScreen = "menu" }
                                    )
                                }

                                "ai" -> feedbackManager?.let { feedback ->
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

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun TicTacToeGame(
    gameState: GameState,
    feedbackManager: FeedbackManager,
    onBackClick: () -> Unit
) {
    val xColor = MaterialTheme.colorScheme.secondary
    val oColor = MaterialTheme.colorScheme.tertiary

    val finished = (gameState.winner != null) || gameState.isDraw

    // Status text
    val statusText = when {
        gameState.winner != null -> stringResource(id = R.string.winner, gameState.winner.toString())
        gameState.isDraw -> stringResource(id = R.string.draw)
        else -> stringResource(id = R.string.current_player, gameState.currentPlayer.toString())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Text(
            text = statusText,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(18.dp))

        // Board container
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
            shape = RoundedCornerShape(26.dp),
            tonalElevation = 0.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (i in 0..2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (j in 0..2) {
                            PremiumCell(
                                value = gameState.getBoardValue(i, j),
                                xColor = xColor,
                                oColor = oColor,
                                enabled = (gameState.winner == null && !gameState.isDraw),
                                isWinning = gameState.winningCells.contains(i to j),
                                onClick = { gameState.makeMove(i, j, feedbackManager) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rivincita solo a fine partita
            AnimatedVisibility(
                visible = finished,
                enter = fadeIn() + scaleIn(initialScale = 0.92f),
                exit = fadeOut() + scaleOut(targetScale = 0.92f)
            ) {
                Button(
                    onClick = { gameState.reset() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text(text = "▶  " + stringResource(id = R.string.rematch))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text(text = "↩  " + stringResource(id = R.string.back_menu))
                }

                Button(
                    onClick = { gameState.reset() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text(text = "⟲  " + stringResource(id = R.string.reset_game))
                }
            }
        }

        // spazio extra sopra al banner fisso
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun PremiumCell(
    value: Char,
    xColor: Color,
    oColor: Color,
    enabled: Boolean,
    isWinning: Boolean,
    onClick: () -> Unit
) {
    val symbol = if (value == ' ') "" else value.toString()

    val symbolColor = when (value) {
        'X' -> xColor
        'O' -> oColor
        else -> MaterialTheme.colorScheme.onSurface
    }

    // ✅ Animazione compatibile (niente animateFloat "inesistente")
    val targetScale = if (isWinning) 1.04f else 1.0f
    val pulseScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 220),
        label = "pulseScale"
    )

    val bg by animateColorAsState(
        targetValue = if (isWinning) {
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.28f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        },
        label = "cellBg"
    )

    val shadow by animateDpAsState(
        targetValue = if (isWinning) 18.dp else 6.dp,
        label = "cellShadow"
    )

    Surface(
        modifier = Modifier
            .size(92.dp)
            .graphicsLayer(
                scaleX = pulseScale,
                scaleY = pulseScale
            ),
        shape = RoundedCornerShape(22.dp),
        color = bg,
        tonalElevation = 0.dp,
        shadowElevation = shadow,
        onClick = onClick,
        enabled = enabled && value == ' '
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = symbol.isNotEmpty(),
                enter = fadeIn() + scaleIn(initialScale = 0.75f),
                exit = fadeOut() + scaleOut(targetScale = 0.75f)
            ) {
                Text(
                    text = symbol,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = symbolColor
                )
            }
        }
    }
}
