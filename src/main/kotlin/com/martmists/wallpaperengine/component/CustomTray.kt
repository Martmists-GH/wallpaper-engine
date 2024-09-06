package com.martmists.wallpaperengine.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import dorkbox.systemTray.Checkbox
import dorkbox.systemTray.MenuItem
import dorkbox.systemTray.Separator
import dorkbox.systemTray.SystemTray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.GraphicsConfiguration
import java.awt.GraphicsEnvironment

class TrayBuilder(private val tray: SystemTray) {
    fun item(
        text: String,
        onClick: () -> Unit
    ) {
        tray.menu.add(MenuItem(text) { onClick() })
    }

    fun checkbox(
        text: String,
        checked: Boolean,
        onClick: (Boolean) -> Unit
    ) {
        tray.menu.add(Checkbox(text) { onClick(!checked) }).apply {
            this.checked = checked
        }
    }

    fun separator() {
        tray.menu.add(Separator())
    }
}

@Composable
fun CustomTray(
    icon: Painter,
    builder: TrayBuilder.() -> Unit
) {
    val scope = rememberCoroutineScope()
    val tray = remember { SystemTray.get() }

    DisposableEffect(tray) {
        scope.launch(Dispatchers.IO) {
            tray.setImage(icon.toAwtImage(GlobalDensity, LayoutDirection.Ltr, Size(16f, 16f)))
            TrayBuilder(tray).builder()
        }

        onDispose {
            tray.remove()
        }
    }
}

internal val GlobalDensity get() = GraphicsEnvironment.getLocalGraphicsEnvironment()
    .defaultScreenDevice
    .defaultConfiguration
    .density

private val GraphicsConfiguration.density: Density
    get() = Density(
        defaultTransform.scaleX.toFloat(),
        fontScale = 1f
    )
