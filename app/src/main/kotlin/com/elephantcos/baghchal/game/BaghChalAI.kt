package com.elephantcos.baghchal.game

import kotlin.random.Random

enum class Difficulty { EASY, MEDIUM, HARD }

object BaghChalAI {

    fun bestMove(state: GameState, diff: Difficulty): Move {
        val moves = availableMoves(state)
        require(moves.isNotEmpty()) { "No moves available" }
        return when (diff) {
            Difficulty.EASY   -> moves.random()
            Difficulty.MEDIUM -> if (Random.nextFloat() < 0.4f) moves.random() else minimax(state, 2)
            Difficulty.HARD   -> minimax(state, 4)
        }
    }

    private fun availableMoves(state: GameState): List<Move> = when (state.currentPlayer) {
        Player.TIGER -> GameEngine.allTigerMoves(state.board)
        Player.GOAT  -> if (state.phase == GamePhase.PLACEMENT) {
            (0..24).filter { state.board[it] == CellState.EMPTY }.map { Move(to = it) }
        } else {
            GameEngine.allGoatMoves(state.board)
        }
    }

    private fun minimax(state: GameState, depth: Int): Move {
        val moves = availableMoves(state)
        val maximizing = state.currentPlayer == Player.TIGER
        var best = moves.first()
        var bestScore = if (maximizing) Int.MIN_VALUE else Int.MAX_VALUE
        for (m in moves) {
            val s = alphabeta(applyToState(state, m), depth-1, Int.MIN_VALUE, Int.MAX_VALUE, !maximizing)
            if (maximizing && s > bestScore) { bestScore = s; best = m }
            if (!maximizing && s < bestScore) { bestScore = s; best = m }
        }
        return best
    }

    private fun alphabeta(state: GameState, depth: Int, a: Int, b: Int, maximizing: Boolean): Int {
        if (state.result == GameResult.TIGER_WINS) return  10000 + depth
        if (state.result == GameResult.GOAT_WINS)  return -10000 - depth
        if (depth == 0) return evaluate(state)
        val moves = availableMoves(state)
        if (moves.isEmpty()) return if (maximizing) -10000 else 10000
        var alpha = a; var beta = b
        return if (maximizing) {
            var v = Int.MIN_VALUE
            for (m in moves) { v = maxOf(v, alphabeta(applyToState(state,m), depth-1, alpha, beta, false)); alpha = maxOf(alpha,v); if (beta<=alpha) break }
            v
        } else {
            var v = Int.MAX_VALUE
            for (m in moves) { v = minOf(v, alphabeta(applyToState(state,m), depth-1, alpha, beta, true)); beta = minOf(beta,v); if (beta<=alpha) break }
            v
        }
    }

    private fun evaluate(state: GameState): Int {
        var s = state.goatsCaptured * 100
        s += GameEngine.tigerMobility(state.board) * 5
        s += GameEngine.tigerCaptures(state.board) * 20
        return s
    }

    fun applyToState(state: GameState, move: Move): GameState {
        val nb       = GameEngine.applyMove(state.board, move)
        val captured = if (move.capturedPos != -1) state.goatsCaptured+1 else state.goatsCaptured
        val toPlace  = if (move.from == -1) state.goatsToPlace-1 else state.goatsToPlace
        val onBoard  = if (move.from == -1) state.goatsOnBoard+1 else state.goatsOnBoard
        val phase    = if (toPlace == 0) GamePhase.MOVEMENT else state.phase
        val next     = if (state.currentPlayer == Player.TIGER) Player.GOAT else Player.TIGER
        val result   = GameEngine.checkResult(nb, captured)
        return state.copy(
            board=nb, phase=phase, currentPlayer=next,
            goatsToPlace=toPlace, goatsCaptured=captured, goatsOnBoard=onBoard,
            result=result, selectedPos=-1, validMoves=emptyList(), lastMove=move
        )
    }
}
