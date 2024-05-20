package com.example.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coredomain.CoreDomain
import com.example.myinfo.AppSettingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val coreDomain: CoreDomain
): ViewModel() {
    private val _functionComplete = MutableStateFlow(false)
    val functionComplete: StateFlow<Boolean> get() = _functionComplete


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

    fun executeFunction() {
        viewModelScope.launch {

            // 함수 실행 (예: 네트워크 요청, 데이터베이스 작업 등)
            delay(2000) // 예제: 2초 지연 후 함수 완료
            _functionComplete.value = true
        }
    }

    fun resetFunctionComplete() {
        _functionComplete.value = false
    }
}
