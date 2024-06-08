package com.example.analysis

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analysisdomain.AnDomain
import com.example.repository.SelectedAppRepository
import com.example.service.AppFetchingInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val anDomain: AnDomain,
    private val appFetchingInfo: AppFetchingInfo
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun loadReportData(app:String) {
        viewModelScope.launch {
            _uiState.value = ReportUiState(
                anDomain.get7daysAvgHourlyUsage(app),
                anDomain.get30DailyUsage(app),
                appFetchingInfo.getAppIcon(app)
            )
        }
    }

}
