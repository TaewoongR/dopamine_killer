package com.example.record

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
        updateUiState()
    }

    private fun updateUiState() {
        viewModelScope.launch {
            while (isActive) { // isActive는 현재 코루틴이 활성 상태인지 확인합니다.
                loadData()
                delay(1000) // 1000ms = 5초
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            val recordList = reDomain.getRecordList()

            // howLong의 큰 순서로 정렬된 리스트
            val sortedByHowLong = recordList.sortedByDescending { it.howLong }

            // ongoing이 true인 리스트
            val ongoingList = recordList.filter { it.onGoing }

            // ongoing이 false인 리스트
            val notOngoingList = recordList.filter { !it.onGoing }

            _uiState.value = RecordUiState(
                descendingList = sortedByHowLong.map {
                    DescendingRecord(
                        appName = it.appName,
                        appIcon = it.appIcon,
                        date = it.date,
                        goalTime = it.goalTime,
                        howLong = it.howLong,
                        onGoing = it.onGoing
                    )
                },
                ongoingList = ongoingList.map {
                    OngoingRecord(
                        appName = it.appName,
                        appIcon = it.appIcon,
                        date = it.date,
                        goalTime = it.goalTime,
                        howLong = it.howLong,
                        onGoing = it.onGoing
                    )
                },
                finishedList = notOngoingList.map {
                    FinishedRecord(
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