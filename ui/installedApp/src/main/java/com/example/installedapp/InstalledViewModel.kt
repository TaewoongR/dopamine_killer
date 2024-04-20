package com.example.installedapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class InstalledViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {
        private val _appUiState = MutableStateFlow(InstalledUiState())
        val appUiState: StateFlow<InstalledUiState> = _appUiState.asStateFlow()

        init {
            loadAppName()
        }

        private fun loadAppName(){
            viewModelScope.launch {
                _appUiState.value = InstalledUiState(
                    nameList = repository.getAppName()
                )
            }
        }
    }
