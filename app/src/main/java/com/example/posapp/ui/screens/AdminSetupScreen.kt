package com.example.posapp.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.example.posapp.utils.Validator

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import com.example.posapp.data.model.SetupConfig
import com.example.posapp.data.repository.ConfigRepository

import com.example.posapp.utils.HashUtils


@Composable
fun AdminSetupScreen(languageCode: String, onNext: () -> Unit) {
    var adminName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isPasswordValid = Validator.isPasswordStrong(password)
    val doPasswordsMatch = password == confirmPassword

    val isFormValid = adminName.isNotBlank() && isPasswordValid && doPasswordsMatch

    val context = LocalContext.current

    val title = when(languageCode) {
        "fr" -> "Création de l’administrateur"
        "es" -> "Creación del administrador"
        "ar" -> "إنشاء المشرف"
        else -> "Admin Setup"
    }

    val adminame = when(languageCode) {
        "fr" -> "Nom d’administrateur"
        "es" -> "Nombre del administrador"
        "ar" -> "اسم المشرف"
        else -> "Administrator name"
    }

    val adminpas = when(languageCode) {
        "fr" -> "Mot de pass"
        "es" -> "Contraseña"
        "ar" -> "كلمة المرور"
        else -> "Password"
    }

    val passhide = when(languageCode) {
        "fr" -> "Afficher/Cacher"
        "es" -> "Mostrar/Ocultar"
        "ar" -> "إظهار/إخفاء"
        else -> "Show/Hide"
    }

    val cofirmpass = when(languageCode) {
        "fr" -> "Confirmer le mot de passe"
        "es" -> "Confirmar Contraseña"
        "ar" -> "تأكيد كلمة المرور"
        else -> "Confirm password"
    }

    val passcond = when(languageCode) {
        "fr" -> "Mot de passe faible : 1 majuscule, 1 minuscule, 1 chiffre, 1 caractère spécial"
        "es" -> "Contraseña débil: 1 letra mayúscula, 1 letra minúscula, 1 número, 1 carácter especial"
        "ar" -> "كلمة مرور ضعيفة: حرف كبير واحد، حرف صغير واحد، رقم واحد، حرف خاص واحد"
        else -> "Weak password: 1 uppercase letter, 1 lowercase letter, 1 number, 1 special character"
    }

    val passmatch = when(languageCode) {
        "fr" -> "Les mots de passe ne correspondent pas"
        "es" -> "Las contraseñas no coinciden"
        "ar" -> "كلمات المرور غير متطابقة"
        else -> "Passwords do not match"
    }

    val passerror = when(languageCode) {
        "fr" -> "Veuillez corriger les erreurs"
        "es" -> "Por favor corrija los errores"
        "ar" -> "يرجى تصحيح الأخطاء"
        else -> "Please correct the errors"
    }

    val nextBtn = when (languageCode) {
        "fr" -> "Suivant"
        "es" -> "Siguiente"
        "ar" -> "التالي"
        else -> "Next"
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = adminName,
            onValueChange = { adminName = it },
            label = { Text(adminame) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = { Text(adminpas) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(imageVector = icon, contentDescription = passhide)
                }
            },
            singleLine = true
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = null
            },
            label = { Text(cofirmpass) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true
        )

        if (!isPasswordValid && password.isNotBlank()) {
            Text(
                text = passcond,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (!doPasswordsMatch && confirmPassword.isNotBlank()) {
            Text(
                text = passmatch,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {
                if (isFormValid) {
                    val hashedPassword = HashUtils.sha256(password)

                    // Créer une première configuration
                    val config = SetupConfig(
                        language = languageCode,
                        adminName = adminName,
                        adminPasswordHash = hashedPassword,
                        storeName = "",
                        storeSector = "",
                        logoUri = null,
                        defaultPrinterName = null,
                        defaultPrinterAddress = null
                    )


                    // Sauvegarder dans le fichier JSON
                    ConfigRepository.saveSetupConfig(context, config)

                    onNext()
                } else {
                    errorMessage = passerror
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
