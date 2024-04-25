package com.example.installedapp

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
class InstalledViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val _installedUiState = MutableStateFlow(InstalledUiState())
    val installedUiState: StateFlow<InstalledUiState> = _installedUiState.asStateFlow()

    init {
        loadAppList()
    }

    private fun loadAppList(){
        viewModelScope.launch {
            _installedUiState.value = InstalledUiState(
                nameList = repository.getInstalledNameList()
            )
        }
    }

    private fun findApp(){
        viewModelScope.launch {
            _installedUiState.value = InstalledUiState(
                appName = repository.findInstalledApp("youtube")
            )
        }
    }
}
