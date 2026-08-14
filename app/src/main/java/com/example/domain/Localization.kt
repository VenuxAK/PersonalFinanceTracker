package com.example.domain

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "koren_settings")

object SettingsKeys {
    val APP_LANGUAGE = stringPreferencesKey("pref_app_language")
    val APP_THEME = stringPreferencesKey("pref_app_theme")
}

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    BURMESE("my", "Burmese", "မြန်မာစာ")
}

enum class AppThemeMode(val titleEn: String, val titleMy: String) {
    SYSTEM("System Default", "စနစ်အတိုင်း"),
    DARK("Dark Obsidian", "အမှောင် (Dark)"),
    LIGHT("Clean Light", "အလင်း (Light)")
}

class LocalizationState(
    private val context: Context? = null,
    private val scope: CoroutineScope? = null,
    initialLanguage: AppLanguage = AppLanguage.ENGLISH,
    initialTheme: AppThemeMode = AppThemeMode.DARK
) {
    var currentLanguage by mutableStateOf(initialLanguage)
    var currentThemeMode by mutableStateOf(initialTheme)

    init {
        if (context != null && scope != null) {
            scope.launch {
                context.settingsDataStore.data.collect { prefs ->
                    val savedLangCode = prefs[SettingsKeys.APP_LANGUAGE]
                    if (savedLangCode != null) {
                        AppLanguage.entries.firstOrNull { it.code == savedLangCode }?.let {
                            currentLanguage = it
                        }
                    }

                    val savedThemeName = prefs[SettingsKeys.APP_THEME]
                    if (savedThemeName != null) {
                        AppThemeMode.entries.firstOrNull { it.name == savedThemeName }?.let {
                            currentThemeMode = it
                        }
                    }
                }
            }
        }
    }

    fun setLanguage(language: AppLanguage) {
        currentLanguage = language
        if (context != null && scope != null) {
            scope.launch {
                context.settingsDataStore.edit { prefs ->
                    prefs[SettingsKeys.APP_LANGUAGE] = language.code
                }
            }
        }
    }

    fun setThemeMode(themeMode: AppThemeMode) {
        currentThemeMode = themeMode
        if (context != null && scope != null) {
            scope.launch {
                context.settingsDataStore.edit { prefs ->
                    prefs[SettingsKeys.APP_THEME] = themeMode.name
                }
            }
        }
    }

    fun isBurmese() = currentLanguage == AppLanguage.BURMESE

    fun t(en: String, my: String): String {
        return if (currentLanguage == AppLanguage.BURMESE) my else en
    }
}

@Composable
fun rememberLocalizationState(): LocalizationState {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    return remember {
        LocalizationState(context = context, scope = scope)
    }
}

val LocalAppLocalization = compositionLocalOf { LocalizationState() }

// Category localization mapping helper
object CategoryLocalization {
    fun getLocalizedCategoryName(categoryName: String, isBurmese: Boolean): String {
        if (!isBurmese) return categoryName
        return when (categoryName.trim().lowercase()) {
            "food & dining", "food", "dining" -> "အစားအသောက်"
            "transport", "transportation" -> "သယ်ယူပို့ဆောင်ရေး"
            "housing & rent", "housing", "rent" -> "အိမ်လခနှင့် အိမ်စရိတ်"
            "utilities", "bills" -> "မီတာခနှင့် ရေမီး"
            "shopping" -> "ဈေးဝယ်ခြင်း"
            "entertainment" -> "အပန်းဖြေခြင်း"
            "healthcare", "health", "medical" -> "ကျန်းမာရေးနှင့် ဆေးဝါး"
            "education" -> "ပညာရေး"
            "travel" -> "ခရီးသွားခြင်း"
            "salary", "monthly salary" -> "လစာ"
            "freelance & gigs", "freelance" -> "အလွတ်တန်းဝင်ငွေ"
            "investments", "investment" -> "ရင်းနှီးမြှုပ်နှံမှု"
            "business" -> "စီးပွားရေး"
            "gifts & rewards", "gifts" -> "လက်ဆောင်နှင့် ဆုငွေ"
            "other expense", "other" -> "အခြား အထွေထွေအသုံး"
            "other income" -> "အခြား ဝင်ငွေ"
            else -> categoryName
        }
    }
}
