package com.example.reward

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rewardDomain.RewardDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardViewModel @Inject constructor(
    private val rewardDomain: RewardDomain
) : ViewModel() {
    private val _uiState = MutableStateFlow(listOf(RewardUiState()))
    val uiState: StateFlow<List<RewardUiState>> = _uiState.asStateFlow()

    init {
        loadRewardData()
    }

    fun loadRewardData(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val badges = rewardDomain.getRewardInfo()
                _uiState.value =
                    badges.map {
                        RewardUiState(
                            name = it.first,
                            description = it.second,
                            imageUrl = it.third
                        )
                    }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }
}
