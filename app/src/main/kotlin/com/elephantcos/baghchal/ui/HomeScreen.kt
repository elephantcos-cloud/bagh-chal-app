package com.elephantcos.baghchal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elephantcos.baghchal.game.Difficulty
import com.elephantcos.baghchal.game.Player
import com.elephantcos.baghchal.ui.theme.*

@Composable
fun HomeScreen(onStart: (Player, Difficulty) -> Unit) {
    var role by remember { mutableStateOf(Player.GOAT) }
    var diff by remember { mutableStateOf(Difficulty.MEDIUM) }
    Box(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkGreen, DeepGreen, Color(0xFF050F02)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("🐯", fontSize = 72.sp)
                Text("বাঘ-ছাগল", color = TigerOrange, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold)
                Text("ঐতিহ্যবাহী বাংলাদেশি বোর্ড গেম", color = TextLight.copy(alpha = 0.65f), textAlign = TextAlign.Center, fontSize = 14.sp)
            }
            Divider(color = WoodBrown.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 24.dp))
            Text("তুমি কে হবে?", color = TextLight, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PickButton(emoji = "🐐", label = "ছাগল", hint = "২০টি ছাগল সাজাও", selected = role == Player.GOAT, modifier = Modifier.weight(1f)) { role = Player.GOAT }
                PickButton(emoji = "🐯", label = "বাঘ", hint = "৫টি ছাগল ধরো", selected = role == Player.TIGER, modifier = Modifier.weight(1f)) { role = Player.TIGER }
            }
            Divider(color = WoodBrown.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 24.dp))
            Text("কঠিনতা বেছে নাও", color = TextLight, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DiffChip("সহজ", Color(0xFF4CAF50), diff == Difficulty.EASY) { diff = Difficulty.EASY }
                DiffChip("মাঝারি", Color(0xFFFF9800), diff == Difficulty.MEDIUM) { diff = Difficulty.MEDIUM }
                DiffChip("কঠিন", Color(0xFFF44336), diff == Difficulty.HARD) { diff = Difficulty.HARD }
            }
            Spacer(Modifier.height(4.dp))
            Button(onClick = { onStart(role, diff) }, Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TigerOrange, contentColor = TextDark),
                shape = RoundedCornerShape(14.dp)) {
                Text("খেলা শুরু করো", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
            Card(colors = CardDefaults.cardColors(containerColor = WoodBrown.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("📋 নিয়মাবলী", color = TigerOrange, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    RuleItem("বাঘ কোণে শুরু করে, ছাগল প্রথমে সাজানো হয়")
                    RuleItem("বাঘ: ৫টি ছাগল ধরলে জয়")
                    RuleItem("ছাগল: ৪টি বাঘ আটকে দিলে জয়")
                    RuleItem("তির্যক চাল সম্ভব (নির্দিষ্ট পয়েন্টে)")
                    RuleItem("সব ছাগল সাজানোর পর নিজেরাও চলতে পারে")
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun PickButton(emoji: String, label: String, hint: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val bg = if (selected) TigerOrange else WoodBrown.copy(alpha = 0.25f)
    val border = if (selected) TigerOrange else WoodBrown.copy(alpha = 0.5f)
    Column(
        modifier.clip(RoundedCornerShape(14.dp)).background(bg)
            .border(2.dp, border, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick).padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 36.sp)
        Text(label, color = if (selected) TextDark else TextLight, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(hint, color = if (selected) TextDark.copy(alpha = 0.7f) else TextLight.copy(alpha = 0.55f), fontSize = 11.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun DiffChip(label: String, color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.clip(RoundedCornerShape(10.dp))
            .background(if (selected) color else color.copy(alpha = 0.15f))
            .border(2.dp, color, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick).padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (selected) Color.White else color, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun RuleItem(text: String) {
    Text("• $text", color = TextLight.copy(alpha = 0.8f), fontSize = 13.sp)
}
