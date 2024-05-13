package com.example.overview

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
class OverviewViewModel @Inject constructor(
    private val coreDomain: CoreDomain,
    private val anDomain: AnDomain
) : ViewModel() {
    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState.asStateFlow()

    init {
        showAppInfo()
    }

    private fun showAppInfo(){
        viewModelScope.launch {
            val appsUsageList = anDomain.getAllSelectedAppUsage()  // 비동기 작업을 기다림
            appsUsageList.map { appUsage ->
                if(appUsage.appName == "Youtube") {
                    _uiState.value = OverviewUiState(
                        appName = appUsage.appName,
                        dailyTime = appUsage.dailyTime,
                        yesterdayTime = appUsage.yesterdayTime,
                        lastWeekAvgTime = appUsage.lastWeekAvgTime,
                        lastMonthAvgTime = appUsage.lastMonthAvgTime,
                        appIcon = coreDomain.getAppIcon(appUsage.appName)
                    )
                }
            }
        }
    }

}