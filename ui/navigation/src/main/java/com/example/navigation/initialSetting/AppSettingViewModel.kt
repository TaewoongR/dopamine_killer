package com.example.navigation.initialSetting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coredomain.CoreDomain
import com.example.navigation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingViewModel @Inject constructor(
    private val coreDomain: CoreDomain,
    @ApplicationContext val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppSettingUiState())
    val uiState: StateFlow<AppSettingUiState> = _uiState.asStateFlow()

    init {
        updateUiState()
    }

    private fun updateUiState() {
        //viewModelScope.launch { coreDomain.clearAllDatabase() } // 테스트 시 데이터베이스 초기화
        val appNameList = mutableListOf<String>()
        viewModelScope.launch {
            val appObjectList = mutableListOf<AppSettingData>()
            val fields = R.string::class.java.fields // R.string 클래스의 모든 필드를 가져옴
            val prefix = "app_" // 필터링에 사용할 접두사

            for (field in fields) {
                try {
                    // 특정 접두사로 시작하는 문자열 리소스만 처리
                    if (field.name.startsWith(prefix)) {
                        val appName = field.name.removePrefix(prefix).replace("_", " ") // 접두사를 제거한 이름
                        val icon = coreDomain.getAppIconForAppSetting(appName)
                        if (icon != null) {
                            appObjectList.add(AppSettingData(appName = appName, icon = icon))
                            appNameList.add(appName)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace() // 예외 처리
                }
            }
            // 업데이트된 appList로 uiState 업데이트
            _uiState.value = AppSettingUiState(appList = appObjectList.toList())
        }
        viewModelScope.launch { coreDomain.updateInitialInstalledApp(appNameList) }
        viewModelScope.launch { coreDomain.initialUpdate(appNameList) }
    }

    fun updateToggleState(appName: String, isEnabled: Boolean) {
        val updatedList = uiState.value.appList.map { appData ->
            if (appData.appName == appName) {
                appData.copy(isButtonEnabled = isEnabled)
            } else {
                appData
            }
        }
        _uiState.value = uiState.value.copy(appList = updatedList)
    }

    fun updateSelectedApps(appList: List<AppSettingData>) {
        viewModelScope.launch {
            val list = mutableListOf<String>()
            appList.forEach {
                if (it.isButtonEnabled) {
                    list.add(it.appName)
                }
            }
            coreDomain.updateInitialSelectedApp(list) // 선택된 앱 이름 리스트를 저장
        }
    }
}