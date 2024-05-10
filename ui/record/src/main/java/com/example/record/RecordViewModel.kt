package com.example.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recorddomain.ReDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val reDomain: ReDomain,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    init {
        updateUiState()
    }

    private fun updateUiState(){
        viewModelScope.launch {
            val recordList = reDomain.getRecordList()
            _uiState.value = RecordUiState(
                recordList = recordList.map {
                    RecordDataUi(
                        appName = it.appName,
                        appIcon = it.appIcon,
                        date = it.date,
                        goalTime = it.goalTime,
                        howLong = it.howLong,
                        onGoing = it.onGoing
                    )
                }
            )
        }
    }
}