package com.example.posapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.posapp.R
import androidx.compose.ui.draw.clip


@Composable
fun LanguageSelectionScreen(onNext: (String) -> Unit) {
    val languages = listOf(
        LanguageItem("en", "English", R.drawable.flag_uk),
        LanguageItem("fr", "Français", R.drawable.flag_fr),
        LanguageItem("es", "Español", R.drawable.flag_es),
        LanguageItem("ar", "العربية", R.drawable.flag_ma)
    )

    var selectedLanguage by remember { mutableStateOf<LanguageItem?>(null) }

    // Traduction dynamique du titre
    val title = when (selectedLanguage?.code) {
        "fr" -> "Sélectionnez votre langue"
        "es" -> "Seleccione su idioma"
        "ar" -> "اختر لغتك"
        else -> "Select your language"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Grille 2x2
        Column {
            for (i in 0 until languages.size step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LanguageButton(
                        language = languages[i],
                        isSelected = selectedLanguage?.code == languages[i].code,
                        onClick = { selectedLanguage = languages[i] }
                    )
                    LanguageButton(
                        language = languages[i + 1],
                        isSelected = selectedLanguage?.code == languages[i + 1].code,
                        onClick = { selectedLanguage = languages[i + 1] }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (selectedLanguage != null) {
            Text(
                text = when (selectedLanguage!!.code) {
                    "fr" -> "Français"
                    "es" -> "Español"
                    "ar" -> "العربية"
                    else -> "English"
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }


        Button(
            onClick = { selectedLanguage?.let { onNext(it.code) } },
            enabled = selectedLanguage != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = when (selectedLanguage?.code) {
                "fr" -> "Suivant"
                "es" -> "Siguiente"
                "ar" -> "التالي"
                else -> "Next"
            })
        }
    }
}

@Composable
fun LanguageButton(
    language: LanguageItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Appliquer un filtre niveau de gris
    val grayscaleMatrix = ColorMatrix().apply { setToSaturation(0f) }
    val grayFilter = ColorFilter.colorMatrix(grayscaleMatrix)

    Surface(
        shape = CircleShape,
        tonalElevation = 4.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .size(100.dp)
            .clickable { onClick() },
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 2.dp,
            brush = SolidColor(
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        )
    ) {
        // L'image remplit tout le cercle
        Image(
            painter = painterResource(id = language.flagRes),
            contentDescription = "Langue ${language.label}",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            colorFilter = if (isSelected) null else grayFilter
        )
    }
}


data class LanguageItem(val code: String, val label: String, val flagRes: Int)
