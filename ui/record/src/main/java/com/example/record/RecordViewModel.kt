package com.example.record

import androidx.lifecycle.ViewModel
import com.example.service.DateFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val dateFactory: DateFactory,
) : ViewModel() {
    private val _appUiState = MutableStateFlow(RecordUiState())
    val appUiState: StateFlow<RecordUiState> = _appUiState.asStateFlow()

}