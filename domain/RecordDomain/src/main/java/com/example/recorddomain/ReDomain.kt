package com.example.recorddomain

interface ReDomain{
    fun getSelectedAppName(): List<String>
    fun saveAppName(appName: String)
}