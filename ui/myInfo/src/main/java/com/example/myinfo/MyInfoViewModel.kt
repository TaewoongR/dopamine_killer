package com.example.myinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyInfoViewModel @Inject constructor(

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

}