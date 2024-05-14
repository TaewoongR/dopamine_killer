package com.example.installedapp

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class InstalledViewModel @Inject constructor(
) : ViewModel() {
    private val _installedUiState = MutableStateFlow(InstalledUiState())
    val installedUiState: StateFlow<InstalledUiState> = _installedUiState.asStateFlow()

    init {
    }


    private fun findApp(){

    }
}
