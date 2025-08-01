package com.example.posapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.posapp.data.repository.ConfigRepository
import androidx.compose.ui.platform.LocalContext

@Composable
fun StoreInfoScreen(languageCode: String, onNext: () -> Unit) {
    val context = LocalContext.current

    var storeName by remember { mutableStateOf("") }
    var selectedSector by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    val isFormValid = storeName.isNotBlank() && selectedSector.isNotBlank()

    val title = when (languageCode) {
        "fr" -> "Informations du magasin"
        "es" -> "Información de la tienda"
        "ar" -> "معلومات المتجر"
        else -> "Store Information"
    }

    val storeLabel = when (languageCode) {
        "fr" -> "Nom du magasin"
        "es" -> "Nombre de la tienda"
        "ar" -> "اسم المتجر"
        else -> "Store Name"
    }

    val sectorTitle = when (languageCode) {
        "fr" -> "Secteur d’activité"
        "es" -> "Sector de actividad"
        "ar" -> "قطاع النشاط"
        else -> "Business Sector"
    }

    val errorMsg = when (languageCode) {
        "fr" -> "Veuillez remplir tous les champs"
        "es" -> "Por favor complete todos los campos"
        "ar" -> "يرجى ملء جميع الحقول"
        else -> "Please fill all fields"
    }

    val configError = when (languageCode) {
        "fr" -> "Erreur : config non trouvée"
        "es" -> "Error: configuración no encontrada"
        "ar" -> "خطأ: لم يتم العثور على الإعدادات"
        else -> "Error: config not found"
    }

    val nextBtn = when (languageCode) {
        "fr" -> "Suivant"
        "es" -> "Siguiente"
        "ar" -> "التالي"
        else -> "Next"
    }

    val sectors = when (languageCode) {
        "fr" -> listOf("Épicier", "Magasin", "Café", "Restaurant", "Salon de beauté", "Pharmacie", "Boulangerie", "Autre")
        "es" -> listOf("Tienda", "Supermercado", "Café", "Restaurante", "Salón de belleza", "Farmacia", "Panadería", "Otro")
        "ar" -> listOf("بقالة", "متجر", "مقهى", "مطعم", "صالون تجميل", "صيدلية", "مخبز", "أخرى")
        else -> listOf("Grocery", "Store", "Café", "Restaurant", "Beauty Salon", "Pharmacy", "Bakery", "Other")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = storeName,
            onValueChange = { storeName = it },
            label = { Text(storeLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(sectorTitle, style = MaterialTheme.typography.bodyMedium)

        sectors.forEach { sector ->
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedSector == sector,
                    onClick = { selectedSector = sector }
                )
                Text(text = sector)
            }
        }

        Button(
            onClick = {
                if (isFormValid) {
                    val currentConfig = ConfigRepository.loadSetupConfig(context)

                    if (currentConfig != null) {
                        val updatedConfig = currentConfig.copy(
                            storeName = storeName,
                            storeSector = selectedSector
                        )
                        ConfigRepository.saveSetupConfig(context, updatedConfig)
                        onNext()
                    } else {
                        errorMessage = configError
                    }
                } else {
                    errorMessage = errorMsg
                }
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(nextBtn)
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
