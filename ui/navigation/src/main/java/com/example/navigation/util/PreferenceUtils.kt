package com.example.navigation.util

import android.content.Context

object PreferenceUtils {

    private const val INITIAL_SETUP = "dopamine_killer_prefs"
    private const val SETUP_COMPLETE_KEY = "SetupComplete"

    fun saveSetupComplete(context: Context) {
        val sharedPreferences = context.getSharedPreferences(INITIAL_SETUP, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(SETUP_COMPLETE_KEY, true)
        editor.apply()
    }

    fun resetSetup(context: Context) {
        val sharedPreferences = context.getSharedPreferences(INITIAL_SETUP, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(SETUP_COMPLETE_KEY, false)
        editor.apply()
    }

    fun isSetupComplete(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(INITIAL_SETUP, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(SETUP_COMPLETE_KEY, false)
    }

}
