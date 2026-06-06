package com.elephantcos.baghchal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.elephantcos.baghchal.game.Difficulty
import com.elephantcos.baghchal.game.Player
import com.elephantcos.baghchal.ui.GameScreen
import com.elephantcos.baghchal.ui.HomeScreen
import com.elephantcos.baghchal.ui.theme.BaghChalTheme

class MainActivity : ComponentActivity() {
    private val vm: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BaghChalTheme {
                var started by remember { mutableStateOf(false) }
                if (!started) {
                    HomeScreen { role, diff ->
                        vm.startGame(role, diff)
                        started = true
                    }
                } else {
                    GameScreen(vm = vm, onBack = { started = false })
                }
            }
        }
    }
}
