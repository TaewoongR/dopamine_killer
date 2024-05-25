package com.example.record

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analysisdomain.AnDomain
import com.example.coredomain.CoreDomain
import com.example.recorddomain.GoalDataDomain
import com.example.recorddomain.ReDomain
import com.example.service.DateFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalCreateViewModel @Inject constructor(
    private val reDomain: ReDomain,
    private val anDomain: AnDomain,
    private val coreDomain: CoreDomain,
    private val dateFactory: DateFactory
) : ViewModel() {
    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    init {
        updateUiState()
    }

    fun updateUiState() {
        viewModelScope.launch {
            val goalInfoList = mutableListOf<GoalUiInfo>()
            val selectedList = anDomain.getEntireSelectedApp()
            for(name in selectedList) {
                val icon = coreDomain.getAppIcon(name)
                goalInfoList.add(
                    GoalUiInfo(
                        appName = name,
                        appIcon = icon,
                        date = dateFactory.returnStringDate(dateFactory.returnToday())
                    )
                )
            }
            _uiState.value = GoalUiState(goalInfoList.toList())
        }
    }

    fun updateGoalTime(appName: String, newGoalTime: Int) {
        val updatedList = _uiState.value.goalList.map { goalInfo ->
            if (goalInfo.appName == appName) goalInfo.copy(goalTime = newGoalTime) else goalInfo
        }
        Log.d("GoalSettingUiState",_uiState.value.goalList[0].goalTime.toString())
        _uiState.value = _uiState.value.copy(goalList = updatedList)
    }


    fun saveAppSettings(goalList: List<GoalUiInfo>) {
        val list = mutableListOf<GoalDataDomain>()
        goalList.forEach {
            if (it.goalTime != 0) {
                list.add(
                    GoalDataDomain(
                        appName = it.appName,
                        date = it.date,
                        goalTime = it.goalTime * 60     // 초 단위로 저장
                    )
                )
            }
        }
        viewModelScope.launch {
            reDomain.createGoal(list.toList()) // reDomain에서 record 저장소로 저장되어야함
        }
    }

}
