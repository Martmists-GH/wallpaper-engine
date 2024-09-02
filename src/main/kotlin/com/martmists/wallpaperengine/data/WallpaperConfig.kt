package com.martmists.wallpaperengine.data

import com.martmists.wallpaperengine.Folders
import com.martmists.wallpaperengine.Globals
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

@Serializable
data class WallpaperConfig(
    val displays: List<DisplayConfig>
) {
    @Serializable
    data class DisplayConfig(
        val displayId: Int,
        val wallpaperPath: String,
    )

    fun withDisplay(cfg: DisplayConfig): WallpaperConfig {
        val displays = displays.toMutableList()
        displays.removeIf { it.displayId == cfg.displayId }
        displays.add(cfg)
        return WallpaperConfig(displays)
    }

    fun withoutDisplay(displayId: Int): WallpaperConfig {
        val displays = displays.toMutableList()
        displays.removeIf { it.displayId == displayId }
        return WallpaperConfig(displays)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun save() {
        if (!Folders.CONFIG_FOLDER.exists()) {
            Folders.CONFIG_FOLDER.mkdirs()
        }

        if (!Folders.PRESET_FOLDER.exists()) {
            Folders.PRESET_FOLDER.mkdirs()
        }

        if (!CONFIG_FILE.exists()) {
            CONFIG_FILE.createNewFile()
        }

        CONFIG_FILE.outputStream().use {
            Globals.JSON.encodeToStream(this, it)
        }
    }

    companion object {
        private val CONFIG_FILE = Folders.CONFIG_FOLDER.resolve("config.json")

        @OptIn(ExperimentalSerializationApi::class)
        fun load(): WallpaperConfig {
            if (!CONFIG_FILE.exists()) {
                return WallpaperConfig(emptyList())
            }

            return CONFIG_FILE.inputStream().use {
                Globals.JSON.decodeFromStream(it)
            }
        }
    }
}
