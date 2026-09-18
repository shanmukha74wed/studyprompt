package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("studyprompt_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME = "pref_theme"
        private const val KEY_DEFAULT_ACADEMIC_LEVEL = "pref_default_academic_level"
        private const val KEY_DEFAULT_LEARNING_STYLE = "pref_default_learning_style"
        private const val KEY_DEFAULT_LANGUAGE = "pref_default_language"
    }

    var themeMode: String
        get() = prefs.getString(KEY_THEME, "system") ?: "system"
        set(value) = prefs.edit().putString(KEY_THEME, value).apply()

    var defaultAcademicLevel: String
        get() = prefs.getString(KEY_DEFAULT_ACADEMIC_LEVEL, "B.Tech") ?: "B.Tech"
        set(value) = prefs.edit().putString(KEY_DEFAULT_ACADEMIC_LEVEL, value).apply()

    var defaultLearningStyle: String
        get() = prefs.getString(KEY_DEFAULT_LEARNING_STYLE, "Step-by-step") ?: "Step-by-step"
        set(value) = prefs.edit().putString(KEY_DEFAULT_LEARNING_STYLE, value).apply()

    var defaultLanguage: String
        get() = prefs.getString(KEY_DEFAULT_LANGUAGE, "English") ?: "English"
        set(value) = prefs.edit().putString(KEY_DEFAULT_LANGUAGE, value).apply()
}
