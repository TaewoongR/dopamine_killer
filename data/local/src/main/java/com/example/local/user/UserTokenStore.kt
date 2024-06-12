package com.example.local.user

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object UserTokenStore {
    private const val PREF_NAME = "user_token_prefs"
    private const val TOKEN_KEY = "token"
    private const val USER_ID = "user"
    private const val LOGIN_POST = "post"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        val editor = getPreferences(context).edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun saveUserId(context: Context, id: String){
        val editor = getPreferences(context).edit()
        editor.putString(USER_ID, id)
        editor.apply()
    }

    fun getToken(context: Context): String? {
        return getPreferences(context).getString(TOKEN_KEY, null)
    }

    fun getUserId(context: Context): String{
        Log.d("username", getPreferences(context).getString(USER_ID, null).toString())
        return getPreferences(context).getString(USER_ID, null).toString()
    }

    fun clearToken(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(TOKEN_KEY)
        editor.apply()
    }

    fun clearUserId(context: Context){
        val editor = getPreferences(context).edit()
        editor.remove(USER_ID)
        editor.apply()
    }

    fun saveLoginPost(context: Context){
        val editor = getPreferences(context).edit()
        editor.putBoolean(LOGIN_POST, true)
        editor.apply()
    }

    fun clearLoginPost(context: Context){
        val editor = getPreferences(context).edit()
        editor.putBoolean(LOGIN_POST, false)
        editor.apply()
    }

    fun getLoginPost(context: Context): Boolean{
        return getPreferences(context).getBoolean(LOGIN_POST, false)
    }
}