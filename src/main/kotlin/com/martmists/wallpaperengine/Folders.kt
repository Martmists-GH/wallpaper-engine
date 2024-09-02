package com.martmists.wallpaperengine

import java.io.File

object Folders {
    // TODO: Add support for unusual steam library locations? (e.g. Flatpak)
    val STEAM_FOLDER = File(System.getProperty("user.home")).resolve(".steam/steam/steamapps")

    val CONFIG_FOLDER = File(System.getProperty("user.home")).resolve(".config/wallpaper_engine")
    val PRESET_FOLDER = CONFIG_FOLDER.resolve("presets")

    val WALLPAPER_ENGINE_FOLDER = STEAM_FOLDER.resolve("common/wallpaper_engine")
    val WALLPAPER_WORKSHOP_FOLDER = STEAM_FOLDER.resolve("workshop/content/431960")
}
