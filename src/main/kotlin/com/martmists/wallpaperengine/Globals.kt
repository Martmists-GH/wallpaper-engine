package com.martmists.wallpaperengine

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object Globals {
    @OptIn(ExperimentalSerializationApi::class)
    val JSON = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
        decodeEnumsCaseInsensitive = true
    }

    const val DEBUG = false
}
