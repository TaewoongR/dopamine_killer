package com.example.analysis

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

val backgroundColor: Color = Color(android.graphics.Color.parseColor("#EFEFEF"))
val keyColor: Color = Color(android.graphics.Color.parseColor("#FF9A62"))
val vagueText: Color = Color(android.graphics.Color.parseColor("#777777"))


@Composable
fun AnalysisScreen(
    navController: NavController,
    viewModel: AnalysisViewModel = hiltViewModel(),
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.loadAnalysisData()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .padding(top = 18.dp), // 상단에 여백 추가
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "History",
            fontSize = 28.sp, // 글자 크기 키움
            fontWeight = FontWeight.Bold,
        )
        analysisContent(uiState, navController, reportViewModel)
    }
    BackHandler {
        navController.navigate("overview_route") {
            popUpTo(0) { inclusive = true }
        }
    }
}


@Composable
fun analysisContent(uiState: AnalysisUiState, navController: NavController, reportViewModel: ReportViewModel) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp)) // 여유 공간
            }
            items(uiState.appList.size) {//우선 카운트 3으로 설정, 분석하는 앱의 개수 넘겨 받아 변경
                analysisGraphBox(
                    modifier = Modifier,
                    aspectRatio = 1f / 0.6f,
                    totalWidth = totalWidth,
                    stateData = uiState.appList[it],
                    navController = navController,
                    reportViewModel
                )
            }
            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}

@Composable
fun analysisGraphBox(
    modifier: Modifier,
    aspectRatio: Float,
    totalWidth: Dp,
    stateData: AnalysisAppData,
    navController: NavController,
    reportViewModel: ReportViewModel
) {
    val times = listOf(
        stateData.lastMonthAvgTime,
        stateData.lastWeekAvgTime,
        stateData.yesterdayTime,
        stateData.dailyTime
    )
    var showTooltip by remember { mutableStateOf(false) }
    var tooltipText by remember { mutableStateOf("") }
    val density = LocalDensity.current
    var tooltipOffset by remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = modifier
                .width(totalWidth)
                .aspectRatio(aspectRatio)
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                .clickable {
                    navController.navigate("report_screen/${stateData.appName}") {
                        popUpTo("analysis_route") { inclusive = false }
                    }
                }
        ) {
            IconImage(
                modifier = Modifier
                    .offset(totalWidth * 0.8f, totalWidth * 0.04f),
                imageBitmap = stateData.appIcon,
                size = totalWidth * 0.16f,
                cornerRadius = 8.dp
            )

            var selectedBarIndex by remember { mutableIntStateOf(-1) }

            Canvas(
                modifier = modifier
                    .matchParentSize()
            ) {
                val barWidth = (size.width * 0.3f) / 4 // 막대 너비
                val barSpacing = (size.width * 0.36f) / 5 // 막대 사이 간격

                val barColors = listOf(
                    keyColor.copy(alpha = 0.3f),
                    keyColor.copy(alpha = 0.6f),
                    keyColor.copy(alpha = 0.85f),
                    keyColor.copy(alpha = 1.0f)
                )
                val maxTime = listOf(
                    stateData.lastMonthAvgTime,
                    stateData.lastWeekAvgTime,
                    stateData.yesterdayTime,
                    stateData.dailyTime
                ).maxOrNull() ?: 1  // 0을 방지하기 위해 최소값은 1로 설정

                val paddingTop = size.height * 0.16f
                val paddingBottom = size.height * 0.2f
                val startX = (size.width - ((barWidth * 4) + (barSpacing * 3))) / 2

                // Y축 라벨 추가
                val yLabels = listOf(0, maxTime / 2, maxTime).map { time2 ->
                    val time = time2 / 60
                    if (time >= 60) {
                        val hours = time / 60
                        val minutes = time % 60
                        if (minutes > 0) {
                            "${hours}시간 ${minutes}분 -"
                        } else {
                            "${hours}시간 -"
                        }
                    } else {
                        "${time}분 -"
                    }
                }

                val yLabelSpacing = (size.height - paddingTop - paddingBottom) / 2
                val textPaint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    textSize = 12.sp.toPx()
                    color = vagueText.toArgb()
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
                val textPaint2 = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    textSize = 12.sp.toPx()
                    color = vagueText.toArgb()
                }

                yLabels.forEachIndexed { index, label ->
                    val y = size.height - paddingBottom - (yLabelSpacing * index) - 4
                    drawIntoCanvas {
                        val textHeight = textPaint.fontMetrics.run { descent - ascent }
                        it.nativeCanvas.drawText(
                            label,
                            startX - 30, // 원하는 위치로 조정
                            y + textHeight / 2,
                            textPaint
                        )
                    }
                }

                //막대 4개; 지난 달, 지난 주, 어제, 오늘
                times.forEachIndexed { index, time ->
                    val barHeight = time.toFloat() / maxTime  // 높이 계산
                    val barStartX = startX + index * (barWidth + barSpacing)
                    val barTopY = (1 - barHeight) * (size.height - paddingTop - paddingBottom)

                    drawRoundRect(
                        color = barColors[index],
                        topLeft = Offset(barStartX, barTopY + paddingTop),
                        size = Size(
                            barWidth,
                            barHeight * (size.height - paddingTop - paddingBottom)
                        ),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )

                    val text = when (index) {
                        0 -> "지난 달"
                        1 -> "지난 주"
                        2 -> "어제"
                        else -> "오늘"
                    }

                    drawIntoCanvas {
                        val textWidth = textPaint2.measureText(text)
                        it.nativeCanvas.drawText(
                            text,
                            barStartX + (barWidth - textWidth) / 2,
                            size.height - paddingBottom + textPaint2.textSize + 12,
                            textPaint2
                        )
                    }
                }
            }
            DropdownMenu(
                modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp)),
                expanded = showTooltip,
                onDismissRequest = { showTooltip = false },
                offset = tooltipOffset
            ) {
                Text(text = tooltipText, modifier = Modifier.padding(8.dp))
            }
        }
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
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}