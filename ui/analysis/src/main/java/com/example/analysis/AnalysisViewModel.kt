package com.example.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analysisdomain.AnDomain
import com.example.recorddomain.ReDomain
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
    private val AnDomain: AnDomain,
    private val ReDomain: ReDomain
) : ViewModel() {
    private val _appUiState = MutableStateFlow(AnalysisUiState())
    val appUiState: StateFlow<AnalysisUiState> = _appUiState.asStateFlow()

    private val today = dateFactory.returnStringDate(dateFactory.returnToday())
    private val yesterday = dateFactory.returnStringDate(dateFactory.returnTheDayStart(1))

    init {
        updateUiState()
    }

    private fun updateUiState() {

    }
}