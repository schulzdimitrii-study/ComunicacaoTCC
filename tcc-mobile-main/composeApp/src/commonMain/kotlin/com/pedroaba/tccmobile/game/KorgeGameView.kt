package com.pedroaba.tccmobile.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun KorgeGameView(
    controller: GameController,
    isActive: Boolean,
    modifier: Modifier = Modifier
)
