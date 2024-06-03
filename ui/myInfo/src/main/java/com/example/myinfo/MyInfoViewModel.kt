package com.example.myinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.appUsage.NetworkDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyInfoViewModel @Inject constructor(
    private val networkDataSource: NetworkDataSource
): ViewModel(){
    private val _uiState = MutableStateFlow(MyInfoUiState())
    val uiState: StateFlow<MyInfoUiState> = _uiState.asStateFlow()

    init {
        updateUiState()
    }

    private fun updateUiState(){
        viewModelScope.launch {
            loadData()
        }
    }

    fun loadData(){

    }

    fun deleteUserData(token: String, username: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                networkDataSource.deleteAppTime(token, username)
                networkDataSource.deleteDailyUsage(token, username)
                networkDataSource.deleteWeeklyUsage(token, username)
                networkDataSource.deleteMonthlyUsage(token, username)
                networkDataSource.deleteGoalByUserName(token, username)
            } catch (e: Exception) {
                // Handle errors appropriately
            } finally {
                onComplete()
            }
        }
    }
}