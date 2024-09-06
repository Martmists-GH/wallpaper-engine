package com.martmists.wallpaperengine.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.loadImageBitmap
import org.jetbrains.compose.animatedimage.AnimatedImage
import org.jetbrains.compose.animatedimage.animate
import org.jetbrains.compose.animatedimage.loadAnimatedImage
import java.io.File

/**
 * A dynamic image composable that can load both static and animated images.
 */
@Composable
fun DynamicImage(
    source: File,
    modifier: Modifier = Modifier
) {
    val isAnimated by derivedStateOf { source.extension == "gif" }
    var forceStatic by remember { mutableStateOf(false) }

    if (isAnimated) {
        var imageAnimated by remember { mutableStateOf<AnimatedImage?>(null) }
        val imageReady by derivedStateOf { imageAnimated != null }

        LaunchedEffect(source) {
            imageAnimated = loadAnimatedImage(source.absolutePath)
            if (imageAnimated!!.codec.frameCount == 0) {
                println("Warning: ${source.absolutePath} has no frames")
                forceStatic = true
            }
        }

        if (imageReady) {
            val bitmap = imageAnimated!!.animate()
            Image(bitmap, null, modifier = modifier)
        } else {
            // Placeholder while loading (or if loading failed)
            Box(modifier = modifier.background(color = MaterialTheme.colorScheme.surfaceDim))
        }
    } else {
        val bitmap = remember {
            source.inputStream().use { stream ->
                loadImageBitmap(stream)
            }
        }
        Image(bitmap, null, modifier = modifier)
    }
}
