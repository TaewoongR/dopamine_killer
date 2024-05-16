package com.example.navigation.initialSetting

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.coroutines.launch

@Composable
fun GoalSettingScreen(
    navController: NavController,
    viewModel: GoalSettingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    goalSetContent(uiState, viewModel, navController)
}


@Composable
fun goalSetContent(uiState: GoalSettingUiState, viewModel: GoalSettingViewModel, navController:NavController) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f
    val minuteMarks = (0..12).map { it * 5 }

    val sliderPositions = remember { mutableStateListOf<Float>() }
    repeat(uiState.goalList.size) {
        sliderPositions.add(0f)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(com.example.record.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally // 여기에 있던 spacedby코드 삭제
        ) {
            item {
                Spacer(modifier = Modifier.height(totalWidth * 0.24f))
                Text(text = "목표를 설정하세요.", // 텍스트 내용 수정
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = totalWidth * 0.16f))
            }
            items(uiState.goalList.size) { index ->
                goalSetBox(
                    modifier = Modifier,
                    totalWidth = totalWidth,
                    minuteMarks = minuteMarks,
                    sliderPosition = sliderPositions[index],
                    onSliderPositionChanged = { newPosition ->
                        sliderPositions[index] = newPosition
                    },
                    goalInfo = uiState.goalList[index],
                    viewModel = viewModel
                )
                Spacer(modifier = Modifier.height(totalWidth * 0.1f)) // 박스들 사이 스페이서 추가
            }
            item {
                Spacer(modifier = Modifier.height(totalWidth * 0.32f)) // 하단 여유 공간 수정
            }
        }
        Box(modifier = Modifier.offset(y = totalWidth * 1f)){
            setButton(totalWidth = totalWidth, viewModel = viewModel, uiState = uiState, navController = navController) // 텍스트 변경?
        }
    }
}

@Composable
fun goalSetBox(
    modifier: Modifier,
    totalWidth: Dp,
    minuteMarks: List<Int>,
    sliderPosition: Float,
    onSliderPositionChanged: (Float) -> Unit,
    goalInfo: GoalInfo,
    viewModel: GoalSettingViewModel
) {
    Box(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .width(totalWidth)
            .wrapContentHeight()
            .padding(
                top = totalWidth * 0.06f,
                bottom = totalWidth * 0.1f,
                start = totalWidth * 0.08f,
                end = totalWidth * 0.08f
            )
    ) {
        Column(modifier = Modifier) {
            Row(
                modifier = Modifier.padding(start = totalWidth * 0.04f, end = totalWidth * 0.04f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                com.example.record.IconImage(
                    modifier = Modifier,
                    imageBitmap = goalInfo.appIcon,
                    size = totalWidth * 0.16f,
                    cornerRadius = 8.dp
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${minuteMarks[sliderPosition.toInt()]}분",
                    style = TextStyle(fontSize = 20.sp)
                )
            }
            Spacer(modifier = Modifier.height(totalWidth * 0.08f))
            MinuteSlider(
                value = sliderPosition,
                onValueChange = { newValue ->
                    onSliderPositionChanged(newValue)
                    viewModel.updateGoalTime(goalInfo.appName, minuteMarks[newValue.toInt()])
                },
                valueRange = 0f..12f,
                sliderHeight = 10.dp,
                thumbRadius = 14.dp,
                goalInfo = goalInfo,
                viewModel = viewModel
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable // 수정 있습니다! (첫 번 째 drawRoundRect 컬러 부분)
fun MinuteSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    sliderHeight: Dp = 10.dp,
    thumbRadius: Dp = 14.dp,
    goalInfo: GoalInfo,
    viewModel: GoalSettingViewModel
) {
    BoxWithConstraints(modifier = modifier) {
        val sliderRange = valueRange.endInclusive - valueRange.start
        val position = remember { mutableFloatStateOf(value) }
        val normalized = (position.value - valueRange.start) / sliderRange
        val offsetX = remember { mutableFloatStateOf(0f) }

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(sliderHeight)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val newValue = (offset.x / size.width) * sliderRange + valueRange.start
                    position.value = newValue.coerceIn(valueRange)
                    onValueChange(position.value)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    offsetX.value += dragAmount.x
                    val newValue =
                        ((offsetX.value / size.width) * sliderRange + valueRange.start).coerceIn(
                            valueRange
                        )
                    position.value = newValue
                    onValueChange(newValue)
                }
            }
        ) {
            val thumbPos = (size.width - thumbRadius.toPx() * 2) * normalized + thumbRadius.toPx()

            drawRoundRect(
                color = com.example.record.keyColor.copy(alpha = 0.8f), // 핸들 지나간 부분 키컬러 적용, 투명도 조절
                topLeft = Offset(0f, center.y - sliderHeight.toPx() / 2),
                size = Size(size.width, sliderHeight.toPx()),
                cornerRadius = CornerRadius(x = sliderHeight.toPx() / 2, y = sliderHeight.toPx() / 2)
            )

            drawRoundRect(
                color = Color.LightGray,
                topLeft = Offset(thumbPos, center.y - sliderHeight.toPx() / 2),
                size = Size(size.width - thumbPos, sliderHeight.toPx()),
                cornerRadius = CornerRadius(x = sliderHeight.toPx() / 2, y = sliderHeight.toPx() / 2)
            )

            drawCircle(
                com.example.record.keyColor,
                thumbRadius.toPx(),
                Offset(thumbPos, center.y)
            )
        }
    }
}

@Composable
fun setButton(totalWidth: Dp, viewModel: GoalSettingViewModel, uiState: GoalSettingUiState, navController: NavController){
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .width(totalWidth)
            .aspectRatio(1f / 0.1875f)
            .background(com.example.record.keyColor, shape = RoundedCornerShape(12.dp))
            .clickable {
                scope.launch {
                    viewModel.saveAppSettings(uiState.goalList)  // ViewModel에 전체 상태 전송
                    navController.navigate("bot_nav_bar") { // 대상 루트로 변경하세요
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "완료", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White))
    }
}
