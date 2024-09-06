package com.martmists.wallpaperengine

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.martmists.wallpaperengine.component.CustomTray
import com.martmists.wallpaperengine.component.DynamicImage
import com.martmists.wallpaperengine.component.NonLazyGrid
import com.martmists.wallpaperengine.component.Pill
import com.martmists.wallpaperengine.data.DisplayData
import com.martmists.wallpaperengine.data.WallpaperConfig
import com.martmists.wallpaperengine.data.WallpaperProject
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    val lockFile = File("/tmp/wallpaper-engine.lock")
    val isLocked = lockFile.exists()

    if (!isLocked) {
        lockFile.createNewFile()

        Runtime.getRuntime().addShutdownHook(Thread {
            lockFile.delete()
        })
    }

    application {
        if (isLocked) {
            Window(onCloseRequest = ::exitApplication) {
                MaterialTheme {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Wallpaper Engine is already running.")
                        Text("Please close the other instance before starting a new one.")
                        Text("If you believe this is an error, please delete /tmp/wallpaper-engine.lock")
                        Button(onClick = ::exitApplication) {
                            Text("Exit")
                        }
                    }
                }
            }
        } else {
            var isOpen by remember { mutableStateOf(!Folders.CONFIG_FOLDER.exists()) }
            val appIcon = remember {
                val bitmap = Folders.WALLPAPER_ENGINE_FOLDER.resolve("ui/dist/images/wp_logo.png").inputStream()
                    .use(::loadImageBitmap)
                BitmapPainter(bitmap)
            }
            var config by remember { mutableStateOf(WallpaperConfig.load()) }
            val wallpapers = remember {
                Folders.WALLPAPER_WORKSHOP_FOLDER.listFiles { it: File ->
                    it.isDirectory && it.resolve("project.json").exists()
                }!!.associate {
                    it.resolve("project.json").inputStream().use { stream ->
                        it to Globals.JSON.decodeFromStream<WallpaperProject>(stream)
                    }
                }
            }
            val runners = remember { mutableListOf<WallpaperRunner>() }
            val displayCount = remember { DisplayData.displayCount() }

            LaunchedEffect(config) {
                config.save()

                for (runner in runners.toTypedArray()) {
                    if (runner.config !in config.displays) {
                        runner.kill()
                        runners.remove(runner)
                    }
                }

                for (display in config.displays) {
                    if (runners.none { it.config == display }) {
                        val runner = WallpaperRunner(display)
                        runner.spawn()
                        runners.add(runner)
                    }
                }
            }

            CustomTray(
                icon = appIcon,
            ) {
                item("Open Wallpaper Engine") { isOpen = true }
                if (displayCount > 1) {
                    for (d in 1..displayCount) {
                        item("Remove from Display $d") {
                            config = config.withoutDisplay(d)
                        }
                    }
                    item("Remove from All Displays") {
                        config = WallpaperConfig(emptyList())
                    }
                } else {
                    item("Remove Wallpaper") {
                        config = WallpaperConfig(emptyList())
                    }
                }

                separator()

                item("Quit") { exitApplication() }
            }

            val state = rememberWindowState(
                width = 1280.dp,
                height = 720.dp,
                position = WindowPosition.Aligned(Alignment.Center)
            )

            Window(state = state, onCloseRequest = { isOpen = false }, visible = isOpen, icon = appIcon) {
                MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val items = remember(wallpapers) {
                            wallpapers.entries.toList()
                        }

                        NonLazyGrid(
                            2, wallpapers.size,
                            modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())
                        ) {
                            val (folder, wallpaper) = items[it]
                            Card(modifier = Modifier.fillMaxSize().padding(32.dp).aspectRatio(2.5f)) {
                                Layout(
                                    modifier = Modifier.fillMaxSize().padding(16.dp),
                                    content = {
                                        Column(
                                            Modifier.fillMaxWidth().padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                wallpaper.title,
                                                style = MaterialTheme.typography.headlineLarge,
                                            )

                                            Row {
                                                // TODO: Give certain pills different colors? Perhaps through a config?
                                                Pill(wallpaper.type.name.lowercase().capitalize(Locale.current))

                                                for (tag in wallpaper.tags) {
                                                    Pill(tag)
                                                }

                                                if (wallpaper.metadata.supportsAudioProcessing) {
                                                    Pill("Audio Processing")
                                                }
                                            }
                                        }

                                        Text(
                                            wallpaper.description.takeIf(String::isNotBlank) ?: "No Description",
                                            style = MaterialTheme.typography.bodyMedium,
                                            overflow = TextOverflow.Ellipsis,
                                        )

                                        DynamicImage(
                                            folder.resolve(wallpaper.preview),
                                            Modifier.aspectRatio(1f).fillMaxHeight().clip(MaterialTheme.shapes.medium)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            if (displayCount > 1) {
                                                for (display in 1..displayCount) {
                                                    Button(
                                                        onClick = {
                                                            config = config.withDisplay(
                                                                WallpaperConfig.DisplayConfig(
                                                                    display,
                                                                    folder.resolve(folder.resolve("project.json")).absolutePath
                                                                )
                                                            )
                                                        }
                                                    ) {
                                                        Text("Apply on Display $display")
                                                    }
                                                    if (display != displayCount) Spacer(modifier = Modifier.width(16.dp))
                                                }
                                            } else {
                                                Button(
                                                    onClick = {
                                                        config = config.withDisplay(
                                                            WallpaperConfig.DisplayConfig(
                                                                1,
                                                                folder.resolve(folder.resolve("project.json")).absolutePath
                                                            )
                                                        )
                                                    }
                                                ) {
                                                    Text("Apply")
                                                }
                                            }
                                        }
                                    }
                                ) { measurables, constraints ->
                                    val baseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

                                    val titleRow = measurables[0]
                                    val text = measurables[1]
                                    val image = measurables[2]
                                    val buttons = measurables[3]
                                    val titleRowPlaceable = titleRow.measure(baseConstraints)
                                    val buttonsPlaceable = buttons.measure(baseConstraints)
                                    val titleRowHeight = titleRowPlaceable.measuredHeight
                                    val buttonsHeight = buttonsPlaceable.measuredHeight

                                    val imageSize =
                                        (constraints.maxHeight - titleRowHeight - buttonsHeight).coerceAtLeast(0)
                                    val textWidth = constraints.maxWidth - imageSize

                                    layout(constraints.maxWidth, constraints.maxHeight) {
                                        titleRowPlaceable.placeRelative(0, 0)
                                        text.measure(baseConstraints.copy(maxWidth = textWidth, maxHeight = imageSize))
                                            .placeRelative(0, titleRowHeight)
                                        image.measure(baseConstraints.copy(maxWidth = imageSize, maxHeight = imageSize))
                                            .placeRelative(textWidth, titleRowHeight)
                                        buttonsPlaceable.placeRelative(0, titleRowHeight + imageSize)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
