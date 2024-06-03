package com.example.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analysisdomain.AnDomain
import com.example.repository.SelectedAppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val anDomain: AnDomain,
    private val selectedAppRepository: SelectedAppRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        loadReportData("Youtube")
    }

    fun loadReportData(app:String) {
        viewModelScope.launch {
            _uiState.value = ReportUiState(
                anDomain.get7daysAvgHourlyUsage(app),
                anDomain.get30DailyUsage(app)
            )
        }
    }
}
