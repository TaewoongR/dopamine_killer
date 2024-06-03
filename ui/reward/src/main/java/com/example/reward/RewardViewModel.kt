package com.example.reward

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.local.user.UserTokenStore
import com.example.rewardDomain.RewardDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardViewModel @Inject constructor(
    private val rewardDomain: RewardDomain,
    @ApplicationContext val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(listOf(RewardUiState()))
    val uiState: StateFlow<List<RewardUiState>> = _uiState.asStateFlow()

    init {
        loadRewardData()
    }

    fun loadRewardData(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val badges = rewardDomain.getRewardInfo(UserTokenStore.getUserId(context))
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
