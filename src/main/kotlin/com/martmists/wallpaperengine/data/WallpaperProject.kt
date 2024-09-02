package com.martmists.wallpaperengine.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class WallpaperProject(
    val title: String,
    val description: String = "No Description",
    val preview: String,
    val type: Type,
    val tags: List<String>,
    @SerialName("general")
    val metadata: Metadata,
    @SerialName("workshopid")
    val workshopId: Long? = null,
) {
    @Serializable
    data class Metadata(
        val properties: Map<String, JsonObject> = emptyMap(),
        @SerialName("supportsaudioprocessing")
        val supportsAudioProcessing: Boolean = false,
        @SerialName("supportsvideo")
        val supportsVideo: Boolean = false,  // Undocumented?
        @SerialName("supportsvideoflags")
        val supportsVideoFlags: Int = 0,  // Undocumented?
    ) {
        // TODO: Type safety by `type` field and polymorphic serializer
        @Serializable
        data class Property(
            val index: Int? = null,
            val options: List<JsonObject> = emptyList(),
            val order: Int,
            val text: String,
            val type: PropertyType,
            val value: JsonPrimitive,
        ) {
            @Serializable
            enum class PropertyType {
                TEXT,  // Undocumented, used to represent plain text?

                COLOR,
                SLIDER,
                BOOL,
                COMBO,
                @SerialName("textinput")
                TEXT_INPUT,
                FILE,
                DIRECTORY,

                @SerialName("scenetexture")
                SCENE_TEXTURE,  // Undocumented?
            }
        }
    }

    @Serializable
    enum class Type {
        VIDEO,
        SCENE,
    }
}
