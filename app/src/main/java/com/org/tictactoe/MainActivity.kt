package com.org.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

                // Background premium (gradient leggero)
                val premiumBg = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.92f),
                        MaterialTheme.colorScheme.background
                    )
                )

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        // Banner sempre visibile + safe area navigation bar
                        Column(Modifier.fillMaxWidth()) {
                            Surface(tonalElevation = 0.dp) {
                                AdMobBanner(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                )
                            }
                            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                        }
                    }
                ) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(premiumBg)
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
                                        TicTacToeGamePremium(
                                            gameState = remember { GameState() },
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

/**
 * UI premium del gioco:
 * - celle Card arrotondate
 * - animazione X/O (scale+fade)
 * - highlight celle vincenti (usa gameState.winningCells)
 */
@Composable
fun TicTacToeGamePremium(
    gameState: GameState,
    feedbackManager: FeedbackManager,
    onBackClick: () -> Unit
) {
    val xColor = MaterialTheme.colorScheme.secondary
    val oColor = MaterialTheme.colorScheme.tertiary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // Header / Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.menu_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(6.dp))

                val status = when {
                    gameState.winner != null && gameState.winner != ' ' ->
                        stringResource(id = R.string.winner, gameState.winner.toString())
                    gameState.isDraw ->
                        stringResource(id = R.string.draw)
                    else ->
                        stringResource(id = R.string.current_player, gameState.currentPlayer.toString())
                }

                Text(
                    text = status,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f)
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // Board
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0..2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 6.dp)
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

    val bg by animateColorAsState(
        targetValue = if (isWinning) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.22f)
        else MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
        label = "cellBg"
    )

    val shadow by animateDpAsState(
        targetValue = if (isWinning) 14.dp else 6.dp,
        label = "cellShadow"
    )

    Surface(
        modifier = Modifier.size(92.dp),
        shape = RoundedCornerShape(22.dp),
        color = bg,
        tonalElevation = 0.dp,
        shadowElevation = shadow,
        onClick = onClick,
        enabled = enabled && value == ' '
    ) {
        Box(contentAlignment = Alignment.Center) {
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
