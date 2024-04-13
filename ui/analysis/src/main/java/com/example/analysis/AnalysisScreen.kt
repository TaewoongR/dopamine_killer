import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.analysis.AnalysisViewModel
import com.example.local.AppData

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AnalysisScreen(viewModel: AnalysisViewModel = hiltViewModel()) {
    val appUsageData by viewModel.appUsageData.collectAsState()

    Surface(color = MaterialTheme.colorScheme.background) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(appUsageData, key = { it.appName }) { appUsage ->
                AppUsageItem(appUsage)
            }
        }
    }
}

@Composable
fun AppUsageItem(appUsage: AppData) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "${appUsage.appName}: ${appUsage.appTime}시간",
            style = MaterialTheme.typography.titleMedium
        )
    }
}
