package com.example.posapp.data.repository


import android.content.Context
import com.example.posapp.data.model.SetupConfig
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object ConfigRepository {
    private const val CONFIG_FILE_NAME = "setup_config.json"

    fun saveSetupConfig(context: Context, config: SetupConfig) {
        val json = Json.encodeToString(config)
        val file = File(context.filesDir, CONFIG_FILE_NAME)
        file.writeText(json)
    }

    fun loadSetupConfig(context: Context): SetupConfig? {
        val file = File(context.filesDir, CONFIG_FILE_NAME)
        if (!file.exists()) return null
        val json = file.readText()
        return Json.decodeFromString(json)
    }
}
