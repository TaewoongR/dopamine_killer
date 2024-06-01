package com.example.navigation.initialSetting

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

val backgroundColor: Color = Color(android.graphics.Color.parseColor("#EFEFEF"))
val keyColor: Color = Color(android.graphics.Color.parseColor("#FF9A62"))

@Composable
fun AppSettingScreen(
    navController: NavController,
    viewModel: AppSettingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    appSelection(uiState, viewModel, navController)
}

@Composable
fun appSelection(uiState: AppSettingUiState, viewModel: AppSettingViewModel, navController: NavController) {

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize(),
        contentAlignment = Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(totalWidth * 0.24f))
            Text(text = "분석할 앱을 선택하세요.",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = totalWidth * 0.16f))
            Box(
                modifier = Modifier
                    .width(totalWidth)
                    .aspectRatio(1f / 1.6f)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.height(totalWidth * 0.06f))
                    for (i in 0 until uiState.appList.size) {
                        IndividualAppSetting(totalWidth, uiState.appList[i], viewModel)
                    }
                }
            }
        }
        Box(modifier = Modifier.offset(y = totalWidth * 1f)){
            AppSetButton(totalWidth, viewModel, uiState, navController)
        }
    }
}

@Composable
fun IndividualAppSetting(totalWidth: Dp, appData: AppSettingData, viewModel: AppSettingViewModel) {
    val squareSize = (totalWidth - 10.dp) / 2 * 0.24f
    val icon = appData.icon
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = totalWidth * 0.08f, end = totalWidth * 0.08f,
                top = totalWidth * 0.03f, bottom = totalWidth * 0.03f
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconImage(
            imageBitmap = icon,
            modifier = Modifier.padding(
                end = totalWidth * 0.05f
            ),
            size = squareSize * 0.8f,
            cornerRadius = 8.dp
        )

        Text(text = appData.appName, style = TextStyle(fontSize = 14.sp))

        Spacer(modifier = Modifier.weight(1f))
        ToggleButtonSet(
            totalWidth = totalWidth,
            isButtonEnabled = appData.isButtonEnabled,
            onToggleChange = {isEnabled ->
                viewModel.updateToggleState(appData.appName, isEnabled)
            }
        )
    }
}

@Composable
fun ToggleButtonSet(totalWidth: Dp, isButtonEnabled: Boolean, onToggleChange: (Boolean) -> Unit) {
    val circlePosition by animateFloatAsState(targetValue = if (isButtonEnabled) 1f else 0f,
        label = "FloatAnimation"
    )
    val backgroundColor by animateColorAsState(targetValue = if (isButtonEnabled) keyColor else Color.LightGray,
        label = ""
    )


    Box(
        modifier = Modifier
            .width(totalWidth * 0.13f)
            .height(totalWidth * 0.07f)
            .background(backgroundColor, RoundedCornerShape(999.dp))
            .clickable { onToggleChange(!isButtonEnabled) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(totalWidth * 0.05f)
                .align(Alignment.CenterStart)
                .graphicsLayer {
                    // 동그라미의 수평 이동
                    val circleMargin = totalWidth * 0.01f
                    val motionRange =
                        totalWidth * 0.14f - totalWidth * 0.06f - circleMargin * 2
                    translationX =
                        circleMargin.toPx() + circlePosition * motionRange.toPx()
                }
                .background(Color.White, CircleShape)
        )
    }
}

@Composable
fun AppSetButton(totalWidth: Dp, viewModel: AppSettingViewModel, uiState: AppSettingUiState, navController: NavController){
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .width(totalWidth)
            .aspectRatio(1f / 0.1875f)
            .background(keyColor, shape = RoundedCornerShape(12.dp))
            .clickable {
                scope.launch { // 코루틴 시작
                    viewModel.updateSelectedApps(uiState.appList)  // 데이터 저장 함수 비동기 호출
                    navController.navigate("goal_setting") { // 데이터 저장 완료 후 화면 전환
                        popUpTo("goal_setting") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            },
        contentAlignment = Center
    ) {
        Text(text = "완료", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White))
    }
}

@Composable
fun IconImage(
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    size: Dp,
    cornerRadius: Dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius)),
        contentAlignment = Center
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}