package com.elephantcos.baghchal.game

object GameEngine {

    private val adjacency: Array<List<Int>> = Array(25) { pos ->
        val r = pos / 5; val c = pos % 5
        buildList {
            if (r > 0) add((r-1)*5+c)
            if (r < 4) add((r+1)*5+c)
            if (c > 0) add(r*5+(c-1))
            if (c < 4) add(r*5+(c+1))
            if ((r+c) % 2 == 0) {
                if (r > 0 && c > 0) add((r-1)*5+(c-1))
                if (r > 0 && c < 4) add((r-1)*5+(c+1))
                if (r < 4 && c > 0) add((r+1)*5+(c-1))
                if (r < 4 && c < 4) add((r+1)*5+(c+1))
            }
        }
    }

    fun adjacent(pos: Int): List<Int> = adjacency[pos]

    fun tigerMoves(board: List<CellState>, pos: Int): List<Move> = buildList {
        for (adj in adjacency[pos]) {
            when (board[adj]) {
                CellState.EMPTY -> add(Move(pos, adj))
                CellState.GOAT  -> {
                    val dr = adj/5 - pos/5; val dc = adj%5 - pos%5
                    val dr2 = adj/5 + dr;   val dc2 = adj%5 + dc
                    if (dr2 in 0..4 && dc2 in 0..4) {
                        val dest = dr2*5+dc2
                        if (adjacency[adj].contains(dest) && board[dest] == CellState.EMPTY)
                            add(Move(pos, dest, adj))
                    }
                }
                else -> {}
            }
        }
    }

    fun goatMoves(board: List<CellState>, pos: Int): List<Move> =
        adjacency[pos].filter { board[it] == CellState.EMPTY }.map { Move(pos, it) }

    fun allTigerMoves(board: List<CellState>): List<Move> = buildList {
        for (p in 0..24) if (board[p] == CellState.TIGER) addAll(tigerMoves(board, p))
    }

    fun allGoatMoves(board: List<CellState>): List<Move> = buildList {
        for (p in 0..24) if (board[p] == CellState.GOAT) addAll(goatMoves(board, p))
    }

    fun applyMove(board: List<CellState>, move: Move): List<CellState> {
        val nb = board.toMutableList()
        if (move.from == -1) {
            nb[move.to] = CellState.GOAT
        } else {
            nb[move.to] = nb[move.from]
            nb[move.from] = CellState.EMPTY
            if (move.capturedPos != -1) nb[move.capturedPos] = CellState.EMPTY
        }
        return nb
    }

    fun checkResult(board: List<CellState>, captured: Int): GameResult {
        if (captured >= 5) return GameResult.TIGER_WINS
        if (allTigerMoves(board).isEmpty()) return GameResult.GOAT_WINS
        return GameResult.NONE
    }

    fun tigerMobility(board: List<CellState>)  = allTigerMoves(board).size
    fun tigerCaptures(board: List<CellState>)   = allTigerMoves(board).count { it.capturedPos != -1 }
}
