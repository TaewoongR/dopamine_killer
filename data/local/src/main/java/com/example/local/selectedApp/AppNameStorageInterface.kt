package com.example.local.selectedApp

interface AppNameStorageInterface {
    fun saveString(key: String, value: String)
    fun getString(key: String, defaultValue: String? = null): String?
    fun getAll(): List<String>
    fun removeString(key: String)
    fun clearAll()
}