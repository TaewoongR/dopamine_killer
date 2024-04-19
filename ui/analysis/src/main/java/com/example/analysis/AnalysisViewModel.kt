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
        loadAppUsageData()
    }

    private fun loadAppUsageData() {
        viewModelScope.launch {
            repository.updateAppTime("com.google.android.youtube")
            val appData = repository.getAppDataByName("com.google.android.youtube")
            _appUiState.value = AnalysisUiState(
                appName = appData.appName,
                appTime = appData.hour15,
                isCompleted = appData.isCompleted
            )
        }
    }
    /*
    private fun loadAppNameList() {
        viewModelScope.launch {
            val appList = repository.updateAppName()
            if (appList.isNotEmpty()) {
                _appUiState.value = AnalysisUiState(appNameList = appList)
                loadAppUsageData()
            } else {
                _appUiState.value = AnalysisUiState(appNameList = listOf("No applications available"))
            }
        }
    }
    */
}
