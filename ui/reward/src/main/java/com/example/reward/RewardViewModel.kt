package com.example.reward

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardViewModel @Inject constructor(
) : ViewModel() {

    init {

    }

    private fun updateUiState(){
        viewModelScope.launch {
            loadData()
        }
    }

    fun loadData(){

    }
}
