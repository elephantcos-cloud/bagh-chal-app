package com.elephantcos.baghchal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elephantcos.baghchal.game.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state

    var playerRole: Player = Player.GOAT
    var difficulty: Difficulty = Difficulty.MEDIUM
    private var aiRunning = false

    fun startGame(role: Player, diff: Difficulty) {
        playerRole = role; difficulty = diff; aiRunning = false
        _state.value = GameState()
        if (playerRole == Player.TIGER) scheduleAi()  // Goat AI places first
    }

    fun onTap(pos: Int) {
        val s = _state.value
        if (s.result != GameResult.NONE || aiRunning || s.currentPlayer != playerRole) return
        when {
            s.currentPlayer == Player.GOAT && s.phase == GamePhase.PLACEMENT -> {
                if (s.board[pos] == CellState.EMPTY) commitMove(Move(to = pos))
            }
            s.currentPlayer == Player.TIGER -> handleTigerTap(s, pos)
            s.currentPlayer == Player.GOAT  -> handleGoatTap(s, pos)
        }
    }

    private fun handleTigerTap(s: GameState, pos: Int) {
        when {
            s.selectedPos == -1 && s.board[pos] == CellState.TIGER -> {
                _state.value = s.copy(selectedPos=pos, validMoves=GameEngine.tigerMoves(s.board,pos))
            }
            s.selectedPos != -1 -> {
                val move = s.validMoves.find { it.to == pos }
                when {
                    move != null                       -> commitMove(move)
                    s.board[pos] == CellState.TIGER    -> _state.value = s.copy(selectedPos=pos, validMoves=GameEngine.tigerMoves(s.board,pos))
                    else                               -> _state.value = s.copy(selectedPos=-1, validMoves=emptyList())
                }
            }
        }
    }

    private fun handleGoatTap(s: GameState, pos: Int) {
        when {
            s.selectedPos == -1 && s.board[pos] == CellState.GOAT -> {
                _state.value = s.copy(selectedPos=pos, validMoves=GameEngine.goatMoves(s.board,pos))
            }
            s.selectedPos != -1 -> {
                val move = s.validMoves.find { it.to == pos }
                when {
                    move != null                       -> commitMove(move)
                    s.board[pos] == CellState.GOAT     -> _state.value = s.copy(selectedPos=pos, validMoves=GameEngine.goatMoves(s.board,pos))
                    else                               -> _state.value = s.copy(selectedPos=-1, validMoves=emptyList())
                }
            }
        }
    }

    private fun commitMove(move: Move) {
        val ns = BaghChalAI.applyToState(_state.value, move)
        _state.value = ns
        if (ns.result == GameResult.NONE) scheduleAi()
    }

    private fun scheduleAi() {
        val s = _state.value
        if (s.currentPlayer == playerRole || s.result != GameResult.NONE) return
        aiRunning = true
        viewModelScope.launch(Dispatchers.Default) {
            delay(550)
            val move = BaghChalAI.bestMove(_state.value, difficulty)
            launch(Dispatchers.Main) {
                val ns = BaghChalAI.applyToState(_state.value, move)
                _state.value = ns
                aiRunning = false
                if (ns.result == GameResult.NONE && ns.currentPlayer != playerRole) scheduleAi()
            }
        }
    }

    fun reset() {
        aiRunning = false
        _state.value = GameState()
        if (playerRole == Player.TIGER) scheduleAi()
    }
}
