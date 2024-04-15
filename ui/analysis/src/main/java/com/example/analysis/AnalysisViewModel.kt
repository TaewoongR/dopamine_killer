import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.local.AppData
import com.example.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _appUsageData = MutableStateFlow<List<AppData>>(emptyList())
    val appUsageData: StateFlow<List<AppData>> = _appUsageData.asStateFlow()

    init {
        loadAppUsageData()
    }

    private fun loadAppUsageData() {
        viewModelScope.launch {
            val appData = repository.getAppData()
            _appUsageData.value = appData
        }
    }
}
