package com.martmists.wallpaperengine

import com.martmists.wallpaperengine.data.DisplayData
import com.martmists.wallpaperengine.data.WallpaperConfig
import java.io.File

class WallpaperRunner(
    val config: WallpaperConfig.DisplayConfig
) {
    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            kill()
        })
    }

    private var process: Process? = null

    fun spawn() {
        if (process != null) {
            kill()
        }

        val id = File(config.wallpaperPath).parentFile.name
        val presetPath = Folders.PRESET_FOLDER.resolve("$id.json")
        val presetArgs = if (presetPath.exists()) {
            arrayOf("-preset", presetPath.absolutePath)
        } else {
            emptyArray()
        }

        val data = DisplayData.forDisplay(config.displayId)

        // TODO: Take `scale` into consideration for width and height
        process = ProcessBuilder(
            "wine", Folders.WALLPAPER_ENGINE_FOLDER.resolve("wallpaper64.exe").absolutePath,
            "-control", "openWallpaper",
            "-file", config.wallpaperPath,
            "-playInWindow", "Wallpaper Engine ${data.id}",
            "-borderless",
            *presetArgs,
            "-width", data.mode.size.width.toString(),
            "-height", data.mode.size.height.toString(),

            // Might remove these in favor of letting KWin rules handle it
            "-x", data.pos.x.toString(),
            "-y", data.pos.y.toString(),
        ).apply {
            if (Globals.DEBUG) {
                redirectOutput(ProcessBuilder.Redirect.INHERIT)
                redirectError(ProcessBuilder.Redirect.INHERIT)
            }
        }.start()
    }

    fun kill() {
        if (process?.isAlive == true) {
            process?.destroy()
        }
    }
}
