package com.example.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analysisdomain.AnDomain
import com.example.coredomain.CoreDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val anDomain: AnDomain,
    private val coreDomain: CoreDomain
) : ViewModel() {
    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    init {
        updateUiState()
    }

    private fun updateUiState() {
        viewModelScope.launch {
            loadData()
        }
    }

    fun loadData() {
        viewModelScope.launch {
            val appsUsageList = coreDomain.getAllSelectedAppUsage()  // 비동기 작업을 기다림
            val appList = appsUsageList.map { appUsage ->
                AnalysisAppData(
                    appName = appUsage.appName,
                    dailyTime = appUsage.dailyTime,
                    yesterdayTime = appUsage.yesterdayTime,
                    lastWeekAvgTime = appUsage.lastWeekAvgTime,
                    lastMonthAvgTime = appUsage.lastMonthAvgTime,
                    appIcon = coreDomain.getAppIcon(appUsage.appName)
                )
            }
            _uiState.value = AnalysisUiState(  // UI 상태를 업데이트
                appList = appList
            )
        }
    }
}
