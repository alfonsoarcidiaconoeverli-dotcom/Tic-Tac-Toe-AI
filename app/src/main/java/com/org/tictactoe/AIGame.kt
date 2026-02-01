package com.org.tictactoe

import kotlin.math.max
import kotlin.math.min

class AIGame(private val gameState: GameState) {
    private val player = 'X' // User
    private val ai = 'O' // AI
    private val corners = listOf(0 to 0, 0 to 2, 2 to 0, 2 to 2)
    private val edges = listOf(0 to 1, 1 to 0, 1 to 2, 2 to 1)

    fun bestMove(): Int {
        // ✅ usa snapshot (board resta private in GameState)
        val board = gameState.getBoardSnapshot()

        val moveCount = board.sumOf { row -> row.count { it != ' ' } }

        // Opening moves strategy
        if (moveCount == 0) {
            // First move: Take a corner
            return 0 // Top-left corner
        }

        if (moveCount == 1) {
            // If player took center, take corner
            if (board[1][1] == player) return 0
            // If player took corner or edge, take center
            return 4
        }

        // First check for winning moves
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == ' ') {
                    val tempBoard = toBoxedBoardWithMove(board, i, j, ai)
                    if (checkWinner(tempBoard) == ai) return i * 3 + j
                }
            }
        }

        // Then check for blocking moves
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == ' ') {
                    val tempBoard = toBoxedBoardWithMove(board, i, j, player)
                    if (checkWinner(tempBoard) == player) return i * 3 + j
                }
            }
        }

        // Minimax with alpha-beta pruning
        var bestScore = Int.MIN_VALUE
        var bestMove = -1

        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == ' ') {
                    val tempBoard = toBoxedBoardWithMove(board, i, j, ai)
                    val score = minimax(tempBoard, 0, false, Int.MIN_VALUE, Int.MAX_VALUE)
                    if (score > bestScore) {
                        bestScore = score
                        bestMove = i * 3 + j
                    }
                }
            }
        }

        // Fallback strategy
        if (bestMove == -1) {
            if (board[1][1] == ' ') return 4
            for ((i, j) in corners) if (board[i][j] == ' ') return i * 3 + j
            for ((i, j) in edges) if (board[i][j] == ' ') return i * 3 + j
        }

        return bestMove
    }

    private fun minimax(
        board: Array<Array<Char>>,
        depth: Int,
        isMaximizing: Boolean,
        alphaIn: Int,
        betaIn: Int
    ): Int {
        val winner = checkWinner(board)
        if (winner != ' ') {
            return when (winner) {
                ai -> 10 - depth
                player -> depth - 10
                else -> 0
            }
        }

        if (isGameOver(board)) return 0

        var alpha = alphaIn
        var beta = betaIn

        return if (isMaximizing) {
            var bestScore = Int.MIN_VALUE
            loop@ for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == ' ') {
                        val tempBoard = copyBoardWithMove(board, i, j, ai)
                        val score = minimax(tempBoard, depth + 1, false, alpha, beta)
                        bestScore = max(bestScore, score)
                        alpha = max(alpha, bestScore)
                        if (beta <= alpha) break@loop
                    }
                }
            }
            bestScore
        } else {
            var bestScore = Int.MAX_VALUE
            loop@ for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == ' ') {
                        val tempBoard = copyBoardWithMove(board, i, j, player)
                        val score = minimax(tempBoard, depth + 1, true, alpha, beta)
                        bestScore = min(bestScore, score)
                        beta = min(beta, bestScore)
                        if (beta <= alpha) break@loop
                    }
                }
            }
            bestScore
        }
    }

    private fun isGameOver(board: Array<Array<Char>>): Boolean {
        return board.all { row -> row.all { it != ' ' } }
    }

    private fun checkWinner(board: Array<Array<Char>>): Char {
        for (i in 0..2) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0]
            }
        }

        for (i in 0..2) {
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return board[0][i]
            }
        }

        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0]
        }
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2]
        }

        return ' '
    }

    // -------- helpers --------

    // Converte Array<CharArray> -> Array<Array<Char>> e applica una mossa
    private fun toBoxedBoardWithMove(
        base: Array<CharArray>,
        i: Int,
        j: Int,
        move: Char
    ): Array<Array<Char>> {
        return Array(3) { r ->
            Array(3) { c ->
                if (r == i && c == j) move else base[r][c]
            }
        }
    }

    private fun copyBoardWithMove(
        base: Array<Array<Char>>,
        i: Int,
        j: Int,
        move: Char
    ): Array<Array<Char>> {
        return Array(3) { r ->
            Array(3) { c ->
                if (r == i && c == j) move else base[r][c]
            }
        }
    }
}
