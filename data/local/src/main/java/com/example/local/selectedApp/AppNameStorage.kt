package com.example.local.selectedApp

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class AppNameStorage @Inject constructor(
    @ApplicationContext val context: Context
):AppNameStorageInterface {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "selected_app",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getString(key: String, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    override fun getAll(): List<String> {
        val allEntries = sharedPreferences.all // 모든 키-값 쌍을 가져옴
        val valuesList = mutableListOf<String>()
        for (entry in allEntries.entries) {
            val value = entry.value
            if (value is String) {
                valuesList.add(value)
            }
        }
        return valuesList
    }

    override fun removeString(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    override fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
