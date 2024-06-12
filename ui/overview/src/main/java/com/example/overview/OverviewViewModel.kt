package com.example.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coredomain.CoreDomain
import com.example.network.appUsage.NetworkDataSource
import com.example.recorddomain.ReDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val coreDomain: CoreDomain,
    private val reDomain: ReDomain,
    private val networkDatasource: NetworkDataSource
) : ViewModel() {
    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState.asStateFlow()

    init {
        loadOverviewData()

    }

    fun loadOverviewData() {
        viewModelScope.launch {
            val appAnalysis = coreDomain.getAllSelectedAppUsage()
            if(appAnalysis.isEmpty()){
                _uiState.value = OverviewUiState(AnalysisData())
            }else {
                val recordList = reDomain.getRecordList().filter { it1 ->
                    it1.onGoing
                }
                val recordDataList = if(recordList.isNotEmpty()){
                    recordList.map {
                        RecordData(
                            appName = it.appName,
                            appIcon = it.appIcon,
                            date = it.date,
                            goalTime = it.goalTime,
                            howLong = it.howLong,
                            onGoing = it.onGoing
                        )
                    }
                } else {
                    listOf()
                }
                val analysisData = if(recordDataList.isNotEmpty()){
                    val aRecord = recordDataList.random()
                    val four = try {
                        appAnalysis.first {
                            it.appName == aRecord.appName
                        }
                    }catch (e:NoSuchElementException){
                        appAnalysis.random()
                    }
                    AnalysisData(
                        appName = aRecord.appName,
                        goalTime = aRecord.goalTime,
                        dailyTime = four.dailyTime,
                        yesterdayTime = four.yesterdayTime,
                        lastWeekAvgTime = four.lastWeekAvgTime,
                        lastMonthAvgTime = four.lastMonthAvgTime,
                        appIcon = aRecord.appIcon
                    )
                }else{
                    val four = appAnalysis.random()
                    AnalysisData(
                        appName = four.appName,
                        goalTime = 0,
                        dailyTime = four.dailyTime,
                        yesterdayTime = four.yesterdayTime,
                        lastWeekAvgTime = four.lastWeekAvgTime,
                        lastMonthAvgTime = four.lastMonthAvgTime
                    )
                }
                _uiState.value = OverviewUiState(analysisData, recordDataList)
            }
        }
    }

    fun loadFlaskApiResponse(token: String){
        viewModelScope.launch {
            val response = networkDatasource.getFlaskApiResponse("Bearer $token")
            _uiState.value = _uiState.value.copy(flaskApiResponse = response)
        }
    }
}