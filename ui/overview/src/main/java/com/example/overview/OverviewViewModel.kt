package com.example.overview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coredomain.CoreDomain
import com.example.repository.AppRepository
import com.example.service.DateFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val dateFactory: DateFactory,
    private val repository: AppRepository,  // domain으로 바꿀 예정
    private val coreDomain: CoreDomain      // 임시
) : ViewModel() {
    private val _overviewUiState = MutableStateFlow(OverviewUiState())
    val overviewUiState: StateFlow<OverviewUiState> = _overviewUiState.asStateFlow()

    init {
        showAppInfo()
    }

    private fun showAppInfo(){
        viewModelScope.launch {
            val appName =
                try{
                    async { repository.getInstalledAppName("youtube") }.await()
                } catch (e: Exception){
                    Log.d("not found","not found")
                    "none"
                }
            _overviewUiState.value = OverviewUiState(
                appName = appName,
                dailyTime = repository.getDailyUsageByApp(appName,dateFactory.returnStringDate(dateFactory.returnToday())),
                appIcon = repository.getAppIcon(appName)
            )
        }
    }

    // 임시 사용 함수
    private fun updateCore(){
        coreDomain.updateSelectedApp()
        coreDomain.updateDailyInfo()
        coreDomain.updateEntireAppUsage()
    }
}