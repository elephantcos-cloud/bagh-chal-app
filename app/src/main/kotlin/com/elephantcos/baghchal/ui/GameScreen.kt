package com.elephantcos.baghchal.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elephantcos.baghchal.GameViewModel
import com.elephantcos.baghchal.game.*
import com.elephantcos.baghchal.ui.theme.*

@Composable
fun GameScreen(vm: GameViewModel, onBack: () -> Unit) {
    val s by vm.state.collectAsState()

    val inf = rememberInfiniteTransition(label = "pulse")
    val pulse by inf.animateFloat(
        initialValue = 0.6f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(DarkGreen, DeepGreen, Color(0xFF050F02)))
        )
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            // Top bar
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) { Text("← পেছনে", color = TigerOrange, fontSize = 14.sp) }
                Spacer(Modifier.weight(1f))
                Text("বাঘ-ছাগল", color = TigerOrange, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Spacer(Modifier.weight(1f))
                Text(
                    if (s.phase == GamePhase.PLACEMENT) "বসানো" else "চলাচল",
                    color = TextLight.copy(alpha = 0.55f), fontSize = 13.sp,
                    modifier = Modifier.width(60.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            // Status
            val isMeTurn = s.currentPlayer == vm.playerRole
            Text(
                text = when {
                    s.result != GameResult.NONE -> ""
                    isMeTurn && s.phase == GamePhase.PLACEMENT -> "ছাগল রাখো"
                    isMeTurn -> "তোমার চাল"
                    else -> "AI ভাবছে..."
                },
                color = if (isMeTurn) HighlightGold else TextLight.copy(alpha = 0.5f),
                fontWeight = if (isMeTurn) FontWeight.Bold else FontWeight.Normal,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(10.dp))

            // Board
            BoardView(s = s, pulse = pulse, onTap = vm::onTap)

            Spacer(Modifier.height(12.dp))

            // Score bar
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(WoodBrown.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatBox("ধরা 🐐", "${s.goatsCaptured}/5", CapturedRed)
                Divider(Modifier.height(36.dp).width(1.dp), color = WoodBrown)
                StatBox("মাঠে 🐐", "${s.goatsOnBoard}", GoatCream)
                Divider(Modifier.height(36.dp).width(1.dp), color = WoodBrown)
                StatBox("বাকি 🐐", "${s.goatsToPlace}", HighlightGold)
            }

            Spacer(Modifier.weight(1f))
        }

        // Game over overlay
        if (s.result != GameResult.NONE) {
            GameOver(result = s.result, playerRole = vm.playerRole,
                onRestart = vm::reset, onBack = onBack)
        }
    }
}

@Composable
fun BoardView(s: GameState, pulse: Float, onTap: (Int) -> Unit) {
    Canvas(
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val w    = size.width.toFloat()
                    val pad  = w * 0.1f
                    val cell = (w - 2f * pad) / 4f
                    val col  = ((offset.x - pad) / cell + 0.5f).toInt().coerceIn(0, 4)
                    val row  = ((offset.y - pad) / cell + 0.5f).toInt().coerceIn(0, 4)
                    onTap(row * 5 + col)
                }
            }
    ) {
        val w    = size.width
        val pad  = w * 0.10f
        val cell = (w - 2f * pad) / 4f

        fun pt(pos: Int) = Offset(pad + (pos % 5) * cell, pad + (pos / 5) * cell)

        // Board background
        drawRoundRect(
            brush = Brush.radialGradient(
                listOf(Color(0xFF9C6B3C), Color(0xFF6B3D1A), Color(0xFF4A2800)),
                center = Offset(w / 2, w / 2), radius = w * 0.7f
            ),
            cornerRadius = CornerRadius(20f), size = Size(w, w)
        )
        // Subtle grain overlay
        drawRoundRect(
            color = Color(0x10FFFFFF), cornerRadius = CornerRadius(20f), size = Size(w, w)
        )

        // Grid lines
        for (r in 0..4) {
            for (c in 0..4) {
                val p = r * 5 + c
                if (c < 4) drawLine(BoardLine.copy(alpha=0.8f), pt(p), pt(p+1), 2.5f)
                if (r < 4) drawLine(BoardLine.copy(alpha=0.8f), pt(p), pt(p+5), 2.5f)
                // Diagonals at even-sum positions
                if ((r+c) % 2 == 0) {
                    if (r < 4 && c < 4) drawLine(BoardLine.copy(alpha=0.5f), pt(p), pt((r+1)*5+(c+1)), 1.5f)
                    if (r < 4 && c > 0) drawLine(BoardLine.copy(alpha=0.5f), pt(p), pt((r+1)*5+(c-1)), 1.5f)
                }
            }
        }

        // Valid move dots
        for (mv in s.validMoves) {
            val p = pt(mv.to)
            drawCircle(HighlightGreen.copy(alpha = 0.45f * pulse), cell * 0.26f, p)
            drawCircle(HighlightGreen.copy(alpha = 0.85f),         cell * 0.10f, p)
        }

        // Last-move highlight
        s.lastMove?.let { mv ->
            if (mv.from != -1) drawCircle(HighlightGold.copy(alpha = 0.25f), cell * 0.40f, pt(mv.to))
        }

        // Pieces
        for (pos in 0..24) {
            val p    = pt(pos)
            val cell2 = cell  // alias for lambda use
            when (s.board[pos]) {
                CellState.TIGER -> {
                    val sel = pos == s.selectedPos
                    if (sel) drawCircle(TigerOrange.copy(alpha = 0.45f * pulse), cell2 * 0.44f, p)
                    // Shadow
                    drawCircle(Color.Black.copy(alpha = 0.3f), cell2 * 0.32f, p + Offset(2f, 3f))
                    // Body gradient
                    drawCircle(
                        Brush.radialGradient(listOf(TigerLight, TigerOrange, TigerDark),
                            center = p - Offset(cell2*0.08f, cell2*0.08f), radius = cell2*0.35f),
                        cell2 * 0.30f, p
                    )
                    drawCircle(Color(0xFF3A1800), cell2 * 0.30f, p, style = Stroke(2.5f))
                    // Stripes
                    for (dx in listOf(-0.10f, 0.05f)) {
                        drawLine(Color(0x883A1800),
                            p + Offset(cell2*dx, -cell2*0.18f),
                            p + Offset(cell2*(dx+0.04f), cell2*0.18f), 2.2f)
                    }
                    // Eyes
                    drawCircle(Color.Black, cell2*0.045f, p + Offset(-cell2*0.10f, -cell2*0.08f))
                    drawCircle(Color.Black, cell2*0.045f, p + Offset( cell2*0.10f, -cell2*0.08f))
                }
                CellState.GOAT -> {
                    val sel = pos == s.selectedPos
                    if (sel) drawCircle(GoatCream.copy(alpha = 0.35f * pulse), cell2 * 0.40f, p)
                    drawCircle(Color.Black.copy(alpha = 0.25f), cell2 * 0.28f, p + Offset(2f, 3f))
                    drawCircle(
                        Brush.radialGradient(listOf(GoatCream, Color(0xFFDDC88A), GoatBrown),
                            center = p - Offset(cell2*0.06f, cell2*0.06f), radius = cell2*0.30f),
                        cell2 * 0.26f, p
                    )
                    drawCircle(GoatBrown, cell2 * 0.26f, p, style = Stroke(2f))
                    // Horns
                    drawLine(GoatBrown, p+Offset(-cell2*0.10f,-cell2*0.22f), p+Offset(-cell2*0.07f,-cell2*0.36f), 2.5f)
                    drawLine(GoatBrown, p+Offset( cell2*0.10f,-cell2*0.22f), p+Offset( cell2*0.07f,-cell2*0.36f), 2.5f)
                    // Eyes
                    drawCircle(Color(0xFF5A3A1A), cell2*0.04f, p+Offset(-cell2*0.09f,-cell2*0.07f))
                    drawCircle(Color(0xFF5A3A1A), cell2*0.04f, p+Offset( cell2*0.09f,-cell2*0.07f))
                }
                CellState.EMPTY -> drawCircle(BoardLine.copy(alpha=0.55f), 3.5f, p)
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
        Text(label, color = TextLight.copy(alpha = 0.6f), fontSize = 11.sp)
    }
}

@Composable
fun GameOver(result: GameResult, playerRole: Player, onRestart: () -> Unit, onBack: () -> Unit) {
    val won = (result == GameResult.TIGER_WINS && playerRole == Player.TIGER) ||
              (result == GameResult.GOAT_WINS  && playerRole == Player.GOAT)
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.78f)), Alignment.Center) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1810)),
            shape = RoundedCornerShape(22.dp), modifier = Modifier.padding(32.dp)
        ) {
            Column(
                Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(if (won) "🎉" else "😔", fontSize = 72.sp)
                Text(
                    if (result == GameResult.TIGER_WINS) "বাঘের জয়! 🐯" else "ছাগলের জয়! 🐐",
                    color = TigerOrange, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp
                )
                Text(
                    if (won) "অভিনন্দন! তুমি জিতেছ!" else "হেরে গেছ — আবার চেষ্টা করো!",
                    color = TextLight, fontSize = 15.sp
                )
                Spacer(Modifier.height(6.dp))
                Button(onClick = onRestart, Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TigerOrange, contentColor = TextDark),
                    shape = RoundedCornerShape(12.dp)) {
                    Text("আবার খেলো", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                OutlinedButton(onClick = onBack, Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text("মেনুতে ফিরে যাও", color = TextLight)
                }
            }
        }
    }
}
