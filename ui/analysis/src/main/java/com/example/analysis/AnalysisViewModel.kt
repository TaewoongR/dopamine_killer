package com.example.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analysisdomain.AnDomain
import com.example.recorddomain.ReDomain
import com.example.service.DateFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val dateFactory: DateFactory,
    private val AnDomain: AnDomain,
    private val ReDomain: ReDomain
) : ViewModel() {
    private val _appUiState = MutableStateFlow(AnalysisUiState())
    val appUiState: StateFlow<AnalysisUiState> = _appUiState.asStateFlow()

    private val today = dateFactory.returnStringDate(dateFactory.returnToday())
    private val yesterday = dateFactory.returnStringDate(dateFactory.returnTheDayStart(1))

    init {
        updateUiState()
    }

    private fun updateUiState() {
        viewModelScope.launch {
            tempPutAppName()
            val appList = ReDomain.getSelectedAppName()
            val appStates = mutableListOf<AppState>()  // 새로운 AppState 리스트를 생성
            for (app in appList) {
                val appState = AppState(
                    appName = app,
                    dailyTime = AnDomain.getTodayHour(app, today),
                    yesterdayTime = AnDomain.getYesterdayHour(app, yesterday),
                    lastWeekAvgTime = AnDomain.getLastWeekAvgHour(app, today),
                    lastMonthAvgTime = AnDomain.getLastMonthAvgHour(app, today)
                )
                appStates.add(appState)  // 각 앱의 상태를 리스트에 추가
            }
            _appUiState.value = AnalysisUiState(appList = appStates)  // 전체 상태를 업데이트
        }
    }


    private fun tempPutAppName(){
        ReDomain.saveAppName("youtube")
    }
}