package com.example.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import android.graphics.Paint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun ReportScreen(
    navController: NavController,
    appName: String,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.loadReportData(appName)
    }
    BackHandler {
        navController.popBackStack("analysis_route", false)
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(totalWidth * 0.1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { 
            Spacer(modifier = Modifier.height(totalWidth * 0.02f))
        }
        item { 
            Text(text = "앱 상세 보고서", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        item{
            IconImageForReport(imageBitmap = uiState.appIcon, size = totalWidth * 0.2f, cornerRadius = 8.dp)
        }
        item {
            Column {
                Text(text = "지난 30일 일일 사용 시간", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.padding(start = totalWidth * 0.04f, bottom = totalWidth * 0.04f))
                Graph(uiState.dailyList)
            }
        }
        item {
            Column {
                Text(text = "지난 7일 평균 사용 시간대", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.padding(start = totalWidth * 0.04f, bottom = totalWidth * 0.04f))
                Graph2(uiState.hourlyList)
            }
        }
    }
    //IconImage(imageBitmap = , size = , cornerRadius = , modifier = Modifier.offset(totalWidth * 0.2f, - totalWidth * 0.2f))
}

@Composable
fun Graph(dailyList: List<Int>) {

    val maxValue = dailyList.maxOrNull() ?: 0
    val minValue = 0
    val midValue = (maxValue - minValue) / 2

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val graphWidth = screenWidth * 0.85f

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {// 아이콘 이미지를 박스 내부로 옮김

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(color = Color.White, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .width(graphWidth)
                        .aspectRatio(1f / 0.6f)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxHeight(0.9f)
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, top = 20.dp, start = 50.dp, end = 10.dp)
                    ) {
                        val width = size.width
                        val height = size.height
                        val stepX = width / (dailyList.size - 1)
                        val stepY = height / (maxValue - minValue)

                        val path = Path().apply {
                            moveTo(0f, height - (dailyList[0] - minValue) * stepY)
                            for (i in 1 until dailyList.size) {
                                lineTo(i * stepX, height - (dailyList[i] - minValue) * stepY)
                            }
                        }

                        // Draw the graph path
                        drawPath(
                            path = path,
                            color = Color(0xFFFF9A62),
                            style = Stroke(width = 4f)
                        )

                        // Draw circles at data points
                        dailyList.forEachIndexed { index, value ->
                            val x = index * stepX
                            val y = height - (value - minValue) * stepY
                            drawCircle(
                                color = Color(0xFFBF6A40), // Slightly darker color
                                radius = 6f,
                                center = Offset(x, y)
                            )
                        }

                        // Draw X axis labels
                        val xLabelPaint = Paint().apply {
                            textAlign = Paint.Align.CENTER
                            textSize = 30f
                            color = android.graphics.Color.BLACK
                        }
                        dailyList.indices.forEach { index ->
                            val x = index * stepX
                            val label = when (index) {
                                0 -> "30일전"
                                14 -> "15일 전"
                                29 -> "오늘"
                                else -> ""
                            }
                            drawContext.canvas.nativeCanvas.drawText(
                                label,
                                x,
                                height + 30f,
                                xLabelPaint
                            )
                        }

                        // Draw Y axis labels with 60-minute units
                        val yLabelPaint = Paint().apply {
                            textAlign = Paint.Align.RIGHT
                            textSize = 30f
                            color = android.graphics.Color.BLACK
                        }
                        val ySteps = listOf(minValue, midValue, maxValue)
                        ySteps.forEach { value ->
                            val y = height - (value - minValue) * stepY
                            val hours = value / 60 / 60
                            val minutes = (value / 60) % 60
                            val label = when {
                                hours == 0 && minutes == 0 -> ""
                                hours == 0 -> "${minutes}분"
                                minutes == 0 -> "${hours}시간"
                                else -> "${hours}시간${minutes}분"
                            }
                            drawContext.canvas.nativeCanvas.drawText(
                                label,
                                -10f,
                                y + 10f,
                                yLabelPaint
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Graph2(hourlyList: List<Int>) {

    val maxValue = hourlyList.maxOrNull() ?: 0
    val minValue = 0
    val midValue = (maxValue - minValue) / 2

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val graphWidth = screenWidth * 0.85f

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(graphWidth)
                    .aspectRatio(1f / 0.6f)
            ) {
                Canvas(modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, top = 20.dp, start = 50.dp, end = 10.dp)) {
                    val width = size.width
                    val height = size.height
                    val stepX = width / (hourlyList.size - 1)
                    val stepY = height / (maxValue - minValue)

                    val path = Path().apply {
                        moveTo(0f, height - (hourlyList[0] - minValue) * stepY)
                        for (i in 1 until hourlyList.size) {
                            lineTo(i * stepX, height - (hourlyList[i] - minValue) * stepY)
                        }
                    }

                    // Draw the graph path
                    drawPath(
                        path = path,
                        color = Color(0xFFFF9A62),
                        style = Stroke(width = 4f)
                    )

                    // Draw circles at data points
                    hourlyList.forEachIndexed { index, value ->
                        val x = index * stepX
                        val y = height - (value - minValue) * stepY
                        drawCircle(
                            color = Color(0xFFBF6A40), // Slightly darker color
                            radius = 6f,
                            center = Offset(x, y)
                        )
                    }

                    // Draw X axis labels
                    val xLabelPaint = Paint().apply {
                        textAlign = Paint.Align.CENTER
                        textSize = 30f
                        color = android.graphics.Color.BLACK
                    }
                    val labelIndices = listOf(0, 6, 12, 18, 23)
                    val labels = listOf("0시", "6시", "12시", "18시", "23시")
                    labelIndices.forEachIndexed { index, labelIndex ->
                        val x = labelIndex * stepX
                        val label = labels[index]
                        drawContext.canvas.nativeCanvas.drawText(
                            label,
                            x,
                            height + 30f,
                            xLabelPaint
                        )
                    }

                    // Draw Y axis labels with 60-minute units
                    val yLabelPaint = Paint().apply {
                        textAlign = Paint.Align.RIGHT
                        textSize = 30f
                        color = android.graphics.Color.BLACK
                    }
                    val ySteps = listOf(minValue, midValue, maxValue)
                    ySteps.forEach { value ->
                        val y = height - (value - minValue) * stepY
                        val hours = value / 60 / 60
                        val minutes = (value / 60) % 60
                        val label = when {
                            hours == 0 && minutes == 0 -> ""
                            hours == 0 -> "${minutes}분"
                            minutes == 0 -> "${hours}시간"
                            else -> "${hours}시간${minutes}분"
                        }
                        drawContext.canvas.nativeCanvas.drawText(
                            label,
                            -10f,
                            y + 10f,
                            yLabelPaint
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IconImageForReport(
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