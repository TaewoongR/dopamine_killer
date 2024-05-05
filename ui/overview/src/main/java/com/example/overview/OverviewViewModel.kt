package com.example.overview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.LocalRepository
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
    private val repository: LocalRepository,
) : ViewModel() {
    private val _overviewUiState = MutableStateFlow(OverviewUiState())
    val overviewUiState: StateFlow<OverviewUiState> = _overviewUiState.asStateFlow()

    init {
        showAppInfo()
    }

    fun showAppInfo(){
        viewModelScope.launch {
            val appName =
                try{
                    async { repository.findInstalledApp("youtube") }.await()
                } catch (e: Exception){
                    Log.d("not found","not found")
                    "none"
                }
            _overviewUiState.value = OverviewUiState(
                appName = appName,
                appIcon = repository.getAppIcon(appName)
            )

        }
    }
}