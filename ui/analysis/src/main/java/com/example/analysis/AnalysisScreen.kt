package com.example.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
) {
    val uiState by viewModel.uiState.collectAsState()
    analysisContent(uiState)
}

@Composable
fun analysisContent(uiState: AnalysisUiState) {
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
                    stateData = uiState.appList[it]
                )
            }
            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}

@Composable
fun analysisGraphBox(modifier: Modifier, aspectRatio: Float, totalWidth: Dp, stateData: AnalysisAppData) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IconImage(modifier = Modifier
            .offset(-totalWidth * 0.05f)
            .align(Alignment.End),
            imageBitmap = stateData.appIcon,
            size = totalWidth * 0.16f,
            cornerRadius = 8.dp)

        Box(
            modifier = modifier
                .width(totalWidth)
                .aspectRatio(aspectRatio)
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
        ) {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                val barWidth = (size.width * 0.4f) / 4 // 막대 너비
                val barSpacing = (size.width * 0.4f) / 5 // 막대 사이 간격

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

                val times = listOf(
                    stateData.lastMonthAvgTime,
                    stateData.lastWeekAvgTime,
                    stateData.yesterdayTime,
                    stateData.dailyTime
                )

                val paddingTop = size.height * 0.16f
                val paddingBottom = size.height * 0.2f
                val startX = (size.width - ((barWidth * 4) + (barSpacing * 3))) / 2

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
                        val textPaint = Paint().asFrameworkPaint().apply {
                            isAntiAlias = true
                            textSize = 12.sp.toPx()
                            color = vagueText.toArgb()
                        }
                        val textWidth = textPaint.measureText(text)
                        it.nativeCanvas.drawText(
                            text,
                            barStartX + (barWidth - textWidth) / 2,
                            size.height - paddingBottom + textPaint.textSize + 12,
                            textPaint
                        )
                    }
                }
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

@Preview
@Composable
fun DefaultPreview() {
    analysisContent(uiState = AnalysisUiState())
}