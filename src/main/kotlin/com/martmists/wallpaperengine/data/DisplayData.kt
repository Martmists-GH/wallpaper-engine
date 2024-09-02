package com.martmists.wallpaperengine.data

import com.martmists.wallpaperengine.Globals
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream

@Serializable
data class DisplayData(
    val outputs: List<Output>
) {
    @Serializable
    data class Output(
        val currentModeId: String,
        val modes: List<Mode>,
        val id: Int,
        val pos: Position,
    ) {
        val mode by lazy { modes.find { it.id == currentModeId }!! }

        @Serializable
        data class Mode(
            val id: String,
            val name: String,
            val refreshRate: Float,
            val size: Size
        )
    }

    @Serializable
    data class Size(
        val height: Int,
        val width: Int
    )

    @Serializable
    data class Position(
        val x: Int,
        val y: Int
    )

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        private fun getData(): DisplayData {
            return Runtime.getRuntime().exec(arrayOf("kscreen-doctor", "--json")).inputStream.use {
                Globals.JSON.decodeFromStream<DisplayData>(it)
            }
        }

        fun forDisplay(displayId: Int): Output {
            val data = getData()

            return data.outputs.find { it.id == displayId }!!
        }

        fun displayCount(): Int {
            val data = getData()

            return data.outputs.size
        }
    }
}
