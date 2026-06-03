package com.pedroaba.tccmobile.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import com.pedroaba.tccmobile.game.scenes.MainScene
import korlibs.image.color.Colors
import korlibs.image.format.PNG
import korlibs.korge.Korge
import korlibs.korge.android.KorgeAndroidView
import korlibs.korge.scene.sceneContainer
import korlibs.math.geom.Size

@Composable
actual fun KorgeGameView(
    controller: GameController,
    isActive: Boolean,
    modifier: Modifier
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "KorGE preview unavailable in Compose Preview",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            KorgeAndroidView(context).apply {
                loadModule(
                    Korge(
                        imageFormats = PNG,
                        windowSize = Size(800, 600),
                        virtualSize = Size(480, 320),
                        backgroundColor = Colors["#1a1a1a"],
                        title = "TCC Mobile Game",
                        main = {
                            sceneContainer().changeTo { MainScene(controller, isActive) }
                        }
                    )
                )
            }
        }
    )
}
