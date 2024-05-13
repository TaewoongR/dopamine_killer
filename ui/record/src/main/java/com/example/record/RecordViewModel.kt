package com.example.record

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recorddomain.ReDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val reDomain: ReDomain,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    init {
        startPeriodicUpdate()
    }

    private fun startPeriodicUpdate() {
        viewModelScope.launch {
            while (isActive) { // isActive는 현재 코루틴이 활성 상태인지 확인합니다.
                updateUiState()
                delay(1000) // 1000ms = 5초
            }
        }
    }

    private fun updateUiState() {
        viewModelScope.launch {
            val recordList = reDomain.getRecordList()
            Log.d("recordList", recordList.size.toString())
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