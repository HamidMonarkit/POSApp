package com.example.posapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.posapp.ui.screens.LanguageSelectionScreen
import com.example.posapp.ui.screens.AdminSetupScreen
import com.example.posapp.ui.screens.StoreInfoScreen
import com.example.posapp.ui.screens.LogoUploadScreen
import com.example.posapp.ui.screens.PrinterSelectionScreen
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

//--------------------------------------------------------------------------------
// Enumération des différentes étapes du processus de configuration initiale
//--------------------------------------------------------------------------------
enum class SetupSteps(val route: String) {
    Language("language"),  // choix de la langue
    Admin("admin"),        // configuration de l'administrateur
    Store("store"),        // configuration du magasin
    Logo("logo"),          // configuration du logo
    Printer("printer")     // configuration de l'imprimante
}

@Composable
fun SetupNavGraph(navController: NavHostController) {
    var selectedLanguageCode by remember { mutableStateOf<String?>(null) }

    NavHost(
        navController = navController,
        startDestination = SetupSteps.Language.route
    ) {
        composable(SetupSteps.Language.route) {
            LanguageSelectionScreen(
                onNext = { languageCode ->
                    selectedLanguageCode = languageCode
                    navController.navigate(SetupSteps.Admin.route)
                }
            )
        }
        composable(SetupSteps.Admin.route) {
            AdminSetupScreen(
                languageCode = selectedLanguageCode ?: "en",  // par défaut anglais
                onNext = { navController.navigate(SetupSteps.Store.route) }
            )
        }
        composable(SetupSteps.Store.route) {
            StoreInfoScreen(
                languageCode = selectedLanguageCode ?: "en",
                onNext = { navController.navigate(SetupSteps.Logo.route) }
            )
        }
        composable(SetupSteps.Logo.route) {
            LogoUploadScreen(
                languageCode = selectedLanguageCode ?: "en",
                onNext = { navController.navigate(SetupSteps.Printer.route) }
            )
        }
        composable(SetupSteps.Printer.route) {
            PrinterSelectionScreen(
                languageCode = selectedLanguageCode ?: "en",
                onFinishSetup = { /* ... */ }
            )
        }
    }
}