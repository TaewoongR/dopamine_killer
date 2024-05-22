package com.example.myinfo

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coredomain.CoreDomain
import com.example.recorddomain.ReDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectedAppEditViewModel @Inject constructor(
    private val coreDomain: CoreDomain,
    private val reDomain: ReDomain,
    @ApplicationContext val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(SelectedAppEditUiState())
    val uiState: StateFlow<SelectedAppEditUiState> = _uiState.asStateFlow()

    init {
        updateEditUiState()
    }

    private fun updateEditUiState() {
        viewModelScope.launch {
            val appObjectList = reDomain.getInstalledSelected().map {
                AppEditSettingData(
                    appName = it.first,
                    icon = coreDomain.getAppIcon(it.first),
                    isButtonEnabled = it.second
                )
            }
            // 업데이트된 appList로 uiState 업데이트
            _uiState.value = SelectedAppEditUiState(appList = appObjectList)
        }
    }

    fun updateEditToggleState(appName: String, isEnabled: Boolean) {
        val updatedList = uiState.value.appList.map { appData ->
            if (appData.appName == appName) {
                appData.copy(isButtonEnabled = isEnabled)
            } else {
                appData
            }
        }
        _uiState.value = uiState.value.copy(appList = updatedList)
    }

    fun updateEditSelectedApps(appList: List<AppEditSettingData>) {
        viewModelScope.launch {
            val list = mutableListOf<String>()
            appList.forEach {
                if (it.isButtonEnabled) {
                    list.add(it.appName)
                }
            }
            reDomain.updateSelected(list) // 선택된 앱 이름 리스트를 저장
        }
    }
}