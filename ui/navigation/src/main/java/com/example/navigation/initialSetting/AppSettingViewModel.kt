package com.example.navigation.initialSetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coredomain.CoreDomain
import com.example.navigation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingViewModel @Inject constructor(
    private val coreDomain: CoreDomain
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppSettingUiState())
    val uiState: StateFlow<AppSettingUiState> = _uiState.asStateFlow()

    init {
        updateUiState()
    }

    private fun updateUiState() {
        val appNameList = mutableListOf<String>()
        viewModelScope.launch {
            val appObjectList = mutableListOf<AppSettingData>()
            val fields = R.string::class.java.fields // R.string 클래스의 모든 필드를 가져옴
            for (field in fields) {
                try {
                    val iconNpackagename = coreDomain.getAppIconForAppSetting(field.name)    // 항상 null이 아닌 함수로 변경, 수정필요
                    if(iconNpackagename.first != null){
                        appObjectList.add(AppSettingData(appName = field.name, icon = iconNpackagename.first!!))
                        appNameList.add(field.name)
                    }
                } catch (e: Exception) {
                    e.printStackTrace() // 예외 처리
                }
            }
            // 업데이트된 appList로 uiState 업데이트
            _uiState.value = AppSettingUiState(appList = appObjectList.toList())
        }
        viewModelScope.launch{coreDomain.updateInitialInstalledApp(appNameList)}
        viewModelScope.launch{coreDomain.initialUpdate(appNameList)}
        viewModelScope.launch{coreDomain.initialHourlyUpdate(appNameList)}
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
            coreDomain.updateInitialSelectedApp(list, true) // 선택된 앱 이름 리스트를 저장
        }
    }
}