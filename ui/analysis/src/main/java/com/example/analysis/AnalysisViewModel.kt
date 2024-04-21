package com.example.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val _appUiState = MutableStateFlow(AnalysisUiState())
    val appUiState: StateFlow<AnalysisUiState> = _appUiState.asStateFlow()

    init {
        updateHourlyData()
    }

     fun updateHourlyData() {
        viewModelScope.launch {
            repository.updateHourlyTime("com.google.android.youtube")
            loadHourlyData("com.google.android.youtube")
        }
    }

    fun loadHourlyData(appName: String){
        viewModelScope.launch {
            val appData = repository.getHourlyDataByName(appName)
            _appUiState.value = AnalysisUiState(
                appName = appData.appName,
                dailyTime = appData.totalHour,
                isCompleted = appData.isCompleted
            )
        }
    }
}