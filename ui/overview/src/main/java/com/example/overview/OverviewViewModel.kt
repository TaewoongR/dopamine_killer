package com.example.overview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coredomain.CoreDomain
import com.example.coredomain.FourUsageDomainData
import com.example.recorddomain.ReDomain
import com.example.recorddomain.RecordDataDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val coreDomain: CoreDomain,
    private val reDomain: ReDomain,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState.asStateFlow()

    init {
        updateOverviewUiState()
    }

    private fun updateOverviewUiState(){
        viewModelScope.launch {
            while (isActive) { // isActive는 현재 코루틴이 활성 상태인지 확인합니다.
                loadData()
                delay(10000) // 10000ms = 10초
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {

            val recordList = reDomain.getRecordList()

            // ongoing이 true인 리스트
            val ongoingList = recordList.filter { it.onGoing }.sortedByDescending { it.howLong }
            // ongoing이 false인 리스트
            val finishedList = recordList.filter { !it.onGoing }
            val overviewList = mutableListOf<RecordDataDomain>()

            if (ongoingList.size >= 3) {
                // ongoingList에서 첫 세 개 요소를 추가
                overviewList.addAll(ongoingList.take(3))
            } else {
                // ongoingList의 모든 요소를 추가
                overviewList.addAll(ongoingList)

                // 부족한 개수를 finishedList에서 랜덤하게 선택하여 추가
                val remainingSlots = 3 - ongoingList.size
                if (finishedList.size >= remainingSlots) {
                    overviewList.addAll(finishedList.shuffled().take(remainingSlots))
                } else {
                    // finishedList의 모든 요소를 추가
                    overviewList.addAll(finishedList)
                }
            }

            // appsUsageList에서 랜덤한 항목 하나를 선택하고 매핑
            Log.d("loadData", "Ongoing list size: ${ongoingList.size}")
            val record = ongoingList.random()
            val randomAppUsage =
                coreDomain.getAllSelectedAppUsage()
                    .firstOrNull { it.appName == record.appName } ?: FourUsageDomainData(appName = "null")
            val analysisData = AnalysisData(
                appName = randomAppUsage.appName,
                goalTime = record.goalTime,
                dailyTime = randomAppUsage.dailyTime,
                yesterdayTime = randomAppUsage.yesterdayTime,
                lastWeekAvgTime = randomAppUsage.lastWeekAvgTime,
                lastMonthAvgTime = randomAppUsage.lastMonthAvgTime,
                appIcon = coreDomain.getAppIcon(randomAppUsage.appName)
            )

            _uiState.value = OverviewUiState(
                analysisData = analysisData,
                recordList = overviewList.map {
                    RecordData(
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