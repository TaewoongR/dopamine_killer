package com.example.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.AppRepository
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
    private val repository: AppRepository,
) : ViewModel() {
    private val _appUiState = MutableStateFlow(AnalysisUiState())
    val appUiState: StateFlow<AnalysisUiState> = _appUiState.asStateFlow()

    init {
        updateHourlyData()
    }

     fun updateHourlyData() {
        viewModelScope.launch {
            val fromMilli = dateFactory.returnTheDayStart(0)
            val stringDate = dateFactory.returnStringDate(fromMilli)
            val toMilli = dateFactory.returnTheDayEnd(fromMilli)
            repository.updateHourlyTime(
                "com.google.android.youtube",
                fromMilli,
                toMilli,
                stringDate)
            loadHourlyData("com.google.android.youtube", stringDate)
        }
    }

    fun loadHourlyData(appName: String, date: String){
        viewModelScope.launch {
            val appData = repository.getHourlyDataByNameDate(appName, date)
            _appUiState.value = AnalysisUiState(
                appName = appData.appName,
                dailyTime = appData.totalHour,
                isCompleted = appData.isCompleted
            )
        }
    }
}