package com.example.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.analysisdomain.AnDomain
import com.example.coredomain.CoreDomain
import com.example.local.R
import com.example.navigation.initialSetting.AppSettingData
import com.example.recorddomain.ReDomain
import com.example.service.DateFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val coreDomain: CoreDomain
) : ViewModel() {

     suspend fun initialUpdate(){
         val appNameList = mutableListOf<String>()
         val appObjectList = mutableListOf<AppSettingData>()
         val fields = R.string::class.java.fields // R.string 클래스의 모든 필드를 가져옴
         val prefix = "app_" // 필터링에 사용할 접두사
         for (field in fields) {
            try {
                // 특정 접두사로 시작하는 문자열 리소스만 처리
                if (field.name.startsWith(prefix)) {
                    val appName = field.name.removePrefix(prefix).replace("_", " ") // 접두사를 제거한 이름
                    val icon = withContext(Dispatchers.IO) {coreDomain.getAppIconForAppSetting(appName)}
                    if (icon != null) {
                        appObjectList.add(AppSettingData(appName = appName, icon = icon))
                        appNameList.add(appName)
                    }
                }
            } catch (e: Exception) {
                appNameList.add("null")
            }
         }
         Log.d("mainViewModel","initial update running")
         withContext(Dispatchers.IO) {coreDomain.updateInitialInstalledApp(appNameList)}
         withContext(Dispatchers.IO) {coreDomain.initialUpdate(appNameList)}
    }

    suspend fun loginUpdate(token: String,username: String){
        withContext(Dispatchers.IO) {coreDomain.loginUpdate(token, username)}
    }


    suspend fun clearDatabase(){
        withContext(Dispatchers.IO){coreDomain.clearAllDatabase()}
    }
}
