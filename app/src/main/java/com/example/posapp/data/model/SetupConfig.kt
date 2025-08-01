package com.example.posapp.data.model


import kotlinx.serialization.Serializable

@Serializable
data class SetupConfig(
    val language: String,
    val adminName: String,
    val adminPasswordHash: String,
    val storeName: String,
    val storeSector: String,
    val logoUri: String?,
    val defaultPrinterName: String?,
    val defaultPrinterAddress: String? = null
)



