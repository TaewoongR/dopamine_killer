package com.example.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.local.AppData
import com.example.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppDataUiState(
    val items: List<AppData> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val repository: AppRepository // AppRepository 인터페이스 주입
) : ViewModel() {

    // 앱 사용 데이터를 저장하는 StateFlow
    private val _appUsageData = MutableStateFlow<List<AppData>>(emptyList())
    val appUsageData: StateFlow<List<AppData>> = _appUsageData.asStateFlow()

    init {
        loadAppUsageData()
    }

    private fun loadAppUsageData() {
        viewModelScope.launch {
            // 예: 앱 이름 데이터를 로드하고 처리하는 예시
            val appNames = repository.updateAppName()
            // 여기에서 받은 데이터를 가공하거나, UI에 표시하기 위해 StateFlow에 저장
        }
    }

}
