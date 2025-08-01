package com.example.posapp.ui.screens


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.posapp.data.repository.ConfigRepository

@Composable
fun LogoUploadScreen(
    languageCode: String,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    var logoUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        logoUri = uri
    }

    val title = when (languageCode) {
        "fr" -> "Ajoutez un logo"
        "es" -> "Agregar un logotipo"
        "ar" -> "أضف شعارًا"
        else -> "Add a Logo"
    }

    val selectLabel = when (languageCode) {
        "fr" -> "Choisir une image"
        "es" -> "Seleccionar una imagen"
        "ar" -> "اختر صورة"
        else -> "Select Image"
    }

    val nextLabel = when (languageCode) {
        "fr" -> "Suivant"
        "es" -> "Siguiente"
        "ar" -> "التالي"
        else -> "Next"
    }

    val errorText = when (languageCode) {
        "fr" -> "Veuillez sélectionner une image"
        "es" -> "Por favor seleccione una imagen"
        "ar" -> "يرجى اختيار صورة"
        else -> "Please select an image"
    }

    val errorconfnotfound = when (languageCode) {
        "fr" -> "Erreur: configuration introuvable"
        "es" -> "Error: configuración no encontrada"
        "ar" -> "خطأ: لم يتم العثور على التكوين"
        else -> "Error: config not found"
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)

        Button(onClick = { launcher.launch("image/*") }) {
            Text(selectLabel)
        }

        logoUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Logo sélectionné",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Button(
            onClick = {
                if (logoUri != null) {
                    val currentConfig = ConfigRepository.loadSetupConfig(context)
                    if (currentConfig != null) {
                        val updatedConfig = currentConfig.copy(logoUri = logoUri.toString())
                        ConfigRepository.saveSetupConfig(context, updatedConfig)
                        onNext()
                    } else {
                        errorMessage = errorconfnotfound
                    }
                } else {
                    errorMessage = errorText
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = logoUri != null
        ) {
            Text(nextLabel)
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
