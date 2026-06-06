package com.elephantcos.baghchal.game

enum class CellState { EMPTY, TIGER, GOAT }
enum class Player    { TIGER, GOAT }
enum class GamePhase { PLACEMENT, MOVEMENT }
enum class GameResult{ NONE, TIGER_WINS, GOAT_WINS }

data class Move(
    val from: Int = -1,   // -1 means goat placement
    val to: Int,
    val capturedPos: Int = -1
)

data class GameState(
    val board: List<CellState> = initialBoard(),
    val phase: GamePhase       = GamePhase.PLACEMENT,
    val currentPlayer: Player  = Player.GOAT,
    val goatsToPlace: Int      = 20,
    val goatsCaptured: Int     = 0,
    val goatsOnBoard: Int      = 0,
    val result: GameResult     = GameResult.NONE,
    val selectedPos: Int       = -1,
    val validMoves: List<Move> = emptyList(),
    val lastMove: Move?        = null
)

fun initialBoard(): List<CellState> = List(25) { pos ->
    when (pos) { 0, 4, 20, 24 -> CellState.TIGER else -> CellState.EMPTY }
}
