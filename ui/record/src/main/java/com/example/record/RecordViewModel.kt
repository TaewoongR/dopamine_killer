package com.example.record

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analysisdomain.AnDomain
import com.example.local.user.UserTokenStore
import com.example.recorddomain.ReDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val reDomain: ReDomain,
    private val anDomain: AnDomain,
    @ApplicationContext context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    init {
        loadRecordData()
    }

    fun loadRecordData() {
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
                        todayUsage = anDomain.getTodayUsage(it.appName),
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
    fun deleteOngoingRecord(record: Pair<String, String>, token: String, username: String) {
        viewModelScope.launch {
            reDomain.deleteGoal(record, token, username)
            _uiState.value = _uiState.value.copy(ongoingList = _uiState.value.ongoingList.filterNot {
                it.appName == record.first && it.date == record.second
            })
        }
    }

    fun deleteFinishedRecord(record: Pair<String, String>, token: String, username: String) {
        viewModelScope.launch {
            reDomain.deleteGoal(record, token, username)
            _uiState.value = _uiState.value.copy(finishedList = _uiState.value.finishedList.filterNot {
                it.appName == record.first && it.date == record.second
            })
        }
    }

}