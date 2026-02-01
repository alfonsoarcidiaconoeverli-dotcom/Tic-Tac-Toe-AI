 package com.org.tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class GameState {

    private val board = Array(3) { CharArray(3) { ' ' } }

    var currentPlayer by mutableStateOf('X')
        private set

    var winner by mutableStateOf<Char?>(null)
        private set

    var isDraw by mutableStateOf(false)
        private set

    // ✅ NUOVO: celle vincenti (per highlight)
    var winningCells by mutableStateOf<List<Pair<Int, Int>>>(emptyList())
        private set

    fun getBoardValue(i: Int, j: Int): Char = board[i][j]

    fun makeMove(i: Int, j: Int, feedbackManager: FeedbackManager) {
        if (winner != null || isDraw) return
        if (board[i][j] != ' ') return

        board[i][j] = currentPlayer

        // feedback (se ce l’hai)
        try {
            feedbackManager.playMoveEffect()
        } catch (_: Throwable) {
            // ignora se non esiste
        }

        val (w, winLine) = checkWinner()
        if (w != null) {
            winner = w
            winningCells = winLine
            return
        }

        if (isBoardFull()) {
            isDraw = true
            winningCells = emptyList()
            return
        }

        currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
    }

    fun reset() {
        for (i in 0..2) for (j in 0..2) board[i][j] = ' '
        currentPlayer = 'X'
        winner = null
        isDraw = false
        winningCells = emptyList()
    }

    private fun isBoardFull(): Boolean {
        for (i in 0..2) for (j in 0..2) if (board[i][j] == ' ') return false
        return true
    }

    private fun checkWinner(): Pair<Char?, List<Pair<Int, Int>>> {
        val lines = listOf(
            // rows
            listOf(0 to 0, 0 to 1, 0 to 2),
            listOf(1 to 0, 1 to 1, 1 to 2),
            listOf(2 to 0, 2 to 1, 2 to 2),
            // cols
            listOf(0 to 0, 1 to 0, 2 to 0),
            listOf(0 to 1, 1 to 1, 2 to 1),
            listOf(0 to 2, 1 to 2, 2 to 2),
            // diagonals
            listOf(0 to 0, 1 to 1, 2 to 2),
            listOf(0 to 2, 1 to 1, 2 to 0)
        )

        for (line in lines) {
            val (a, b, c) = line
            val v1 = board[a.first][a.second]
            val v2 = board[b.first][b.second]
            val v3 = board[c.first][c.second]
            if (v1 != ' ' && v1 == v2 && v2 == v3) {
                return v1 to line
            }
        }
        return null to emptyList()
    }
}
