package com.example.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.LocalRepository
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
    private val repository: LocalRepository,
) : ViewModel() {
    private val _appUiState = MutableStateFlow(AnalysisUiState())
    val appUiState: StateFlow<AnalysisUiState> = _appUiState.asStateFlow()

    init {
        updateHourlyData()
    }

     fun updateHourlyData() {
         val fromMilli = dateFactory.returnTheDayStart(9)
         val stringDate = dateFactory.returnStringDate(fromMilli)
         val dayOfWeek = dateFactory.returnDayOfWeek(fromMilli)
         viewModelScope.launch {
            repository.updateHourlyTime(
                "com.google.android.youtube",
                fromMilli,
                stringDate,
                dayOfWeek)
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