package com.example.overview

import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.local.user.UserTokenStore

@Composable
fun OverviewScreen(
    navController: NavController,
    viewModel: OverviewViewModel = hiltViewModel(),
) {
    val overviewUiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val token = UserTokenStore.getToken(context) ?: ""
    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.loadOverviewData()
    }
    if(token.isNotEmpty()){
        //viewModel.loadFlaskApiResponse(token)
    }
    MyScreenContent(overviewUiState)
}


val backgroundColor: Color = Color(parseColor("#EFEFEF"))
val keyColor: Color = Color(parseColor("#FF9A62"))
val vagueText: Color = Color(parseColor("#777777"))
val vagueColor: Color = Color(parseColor("#F0F0F0"))
val brushColor: Color = Color(parseColor("#FFC2A0"))
val brusherColor: Color = Color(parseColor("#FFF4EF"))


@Composable
fun MyScreenContent(overviewUiState: OverviewUiState) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f
    val individualWidth = ((totalWidth) - 10.dp) / 2 // 도넛 그래프 박스와 막대 그래프 오버뷰 박스 크기 설정


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .offset(y = 70.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Log.d("goalTIme", overviewUiState.analysisData.goalTime.toString())
                Log.d("dailyTIme", overviewUiState.analysisData.dailyTime.toString())

                val percent = try{
                    overviewUiState.analysisData.dailyTime.toFloat() / overviewUiState.analysisData.goalTime.toFloat()
                }catch(e: Exception){
                    0f
                }


                DonutGraph(percent = percent, modifier = Modifier.size(individualWidth), size = individualWidth, overviewUiState)
                Spacer(modifier = Modifier.width(10.dp))
                barGraphOverview(modifier = Modifier.size(individualWidth), size = individualWidth, overviewUiState.analysisData)
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0 until minOf(3, overviewUiState.recordList.size)) {
                    recordOverview(modifier = Modifier, aspectRatio = 1f/0.1875f, totalWidth = totalWidth, i, overviewUiState.recordList)
                }
            }
            aiOverview(modifier = Modifier, aspectRatio = 1f/0.6f, totalWidth = totalWidth, flaskApiResponse = overviewUiState.flaskApiResponse)
        }
    }
}

@Composable
fun DonutGraph(percent: Float, modifier: Modifier, size: Dp, overviewUiState: OverviewUiState) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val squareSize = (size.value * 0.24f)// 아이콘 크기 설정

    Box(
        modifier = Modifier
            .size(size)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.matchParentSize(),
            onDraw = {
                val boxSize = Size(size.toPx(), size.toPx())
                val center = Offset(boxSize.width / 2, boxSize.height / 2)
                val radius = (boxSize.minDimension / 2) * 0.7f // 도넛 반지름
                val strokeWidth = screenWidth.toPx() * 0.06f
                val startAngle = 0f // 3시 방향 각도 시작
                val sweepAngle = minOf(percent, 1f) * 360f

                drawCircle(
                    color = vagueColor,
                    center = center,
                    radius = radius,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                val gradientBrush = Brush.sweepGradient(
                    colors = listOf(brusherColor, brushColor, keyColor),
                    center = Offset(boxSize.width / 2, boxSize.height / 2)
                )

                drawArc(
                    brush = gradientBrush,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
            }
        )
        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            IconImage(
                modifier = Modifier,
                imageBitmap = overviewUiState.analysisData.appIcon,
                size = squareSize.dp,
                cornerRadius = 8.dp
            )
            val minute = overviewUiState.analysisData.goalTime / 60
            Text(text = if(minute != 0)"${minute}분" else "미지정", style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp))
        }
    }
}


@Composable
fun barGraphOverview(modifier: Modifier, size: Dp, analysisData: AnalysisData) {
    Box(
        modifier = modifier
            .size(size)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val barWidth = (size.toPx() * 0.4f) / 4 // 막대 너비
            val barSpacing = (size.toPx() * 0.35f) / 5 // 막대 사이 간격

            val barColors = listOf(
                keyColor.copy(alpha = 0.3f),
                keyColor.copy(alpha = 0.6f),
                keyColor.copy(alpha = 0.85f),
                keyColor.copy(alpha = 1.0f)
            )
            val maxTime = listOf(
                analysisData.lastMonthAvgTime,
                analysisData.lastWeekAvgTime,
                analysisData.yesterdayTime,
                analysisData.dailyTime
            ).maxOrNull() ?: 1  // 0을 방지하기 위해 최소값은 1로 설정

            val times = listOf(
                analysisData.lastMonthAvgTime,
                analysisData.lastWeekAvgTime,
                analysisData.yesterdayTime,
                analysisData.dailyTime
            )
            val paddingTop = (size * 0.2f).toPx() // 위쪽 패딩
            val paddingBottom = (size * 0.24f).toPx() // 아래쪽 패딩, 텍스트 공간
            val totalBarsWidth = barWidth * 4 + barSpacing * 3
            val startX = (size.toPx() - totalBarsWidth) / 2

            // 임의의 높이를 가진 4개의 막대 그리기
            times.forEachIndexed { index, time ->
                val barHeight = time.toFloat() / maxTime  // 높이 계산
                val barStartX = startX + index * (barWidth + barSpacing)
                val barTopY = (1 - barHeight) * (size.toPx() - paddingTop - paddingBottom)

                drawRoundRect(
                    color = barColors[index],
                    topLeft = Offset(barStartX, barTopY + paddingTop),
                    size = Size(
                        barWidth,
                        barHeight * (size.toPx() - paddingTop - paddingBottom)
                    ),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun recordOverview(modifier: Modifier, aspectRatio: Float, totalWidth: Dp, index: Int, recordList: List<RecordData>) {
    val squareSize = (totalWidth - 10.dp) / 2 * 0.24f
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .width(totalWidth)
            .aspectRatio(aspectRatio)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = totalWidth * 0.02f),
            verticalAlignment = Alignment.CenterVertically // 앱 아이콘, 째깐둥이 수직 중앙 정렬
        ) {
            IconImage(
                modifier = Modifier.padding(start = totalWidth * 0.06f, end = totalWidth * 0.05f),
                imageBitmap = recordList[index].appIcon,
                size = squareSize,
                cornerRadius = 8.dp
            )

            // 최대 20개까지, int 값만큼 째깐둥이를 그림
            val howLong = recordList[index].howLong
            Row(
                modifier = Modifier
                    .width(totalWidth * 0.635f)
                    .border(1.dp, vagueText.copy(0.2f), shape = RoundedCornerShape(4.dp))
                    .background(vagueText.copy(0.2f), shape = RoundedCornerShape(4.dp))
            ) {
                Box(modifier.padding(start = totalWidth * 0.0035f))
                for (k in 1..if (howLong == 0) 0 else if (howLong % 18 == 0) 18 else if (howLong > 90) 18 else howLong % 18) {
                    Box(
                        modifier = Modifier
                            .padding(end = totalWidth * 0.0035f) // 패딩 수정
                            .padding(start = totalWidth * 0.0035f)
                            .padding(top = totalWidth * 0.003f)
                            .padding(bottom = totalWidth * 0.003f)
                            .size(totalWidth * 0.028f, totalWidth * 0.05f)
                            .background(
                                color = when (howLong) {
                                    in 0..18 -> Color(android.graphics.Color.parseColor("#BEBEBE"))
                                    in 19..36 -> Color(android.graphics.Color.parseColor("#BFCBB4"))
                                    in 37..54 -> Color(android.graphics.Color.parseColor("#ABCC96"))
                                    in 55..72 -> Color(android.graphics.Color.parseColor("#96CE78"))
                                    in 73..90 -> Color(android.graphics.Color.parseColor("#82CF59"))
                                    else -> Color(android.graphics.Color.parseColor("#73A66A"))
                                },
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
                Box(modifier.padding(end = totalWidth * 0.0035f))
            }
            Canvas(modifier = Modifier.weight(1f)) { // 날짜 카운트 텍스트 추가
                val days = recordList[index].howLong.toString() + "일" // "일" 추가

                drawIntoCanvas {
                    val textPaint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        textSize = (totalWidth * 0.04f).toPx()
                        color = Color.Black.toArgb()
                    }
                    val textWidth = textPaint.measureText(days)
                    val fontMetrics = textPaint.fontMetrics
                    val textHeight = fontMetrics.descent - fontMetrics.ascent

                    it.nativeCanvas.drawText(
                        days,
                        size.width - textWidth - 30, // 위치 수정
                        (size.height / 2) + (textHeight / 2) - fontMetrics.descent,
                        textPaint
                    )
                }
            }
        }
        Text(
            text = recordList[index].date.run{ "${this.substring(0, 4)}/${this.substring(4, 6)}/${this.substring(6, 8)}"},
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = totalWidth * 0.04f, top = totalWidth * 0.005f),
            style = TextStyle(Color.Gray, fontSize = 9.sp)
        )
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

@Composable
fun aiOverview(modifier: Modifier, aspectRatio: Float, totalWidth: Dp, flaskApiResponse: String?){
    Box(
        modifier = Modifier
            .width(totalWidth)
            .aspectRatio(aspectRatio)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
    ) {
        flaskApiResponse?.let { response ->
            Text(
                text = response,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black),
                modifier = Modifier.padding(16.dp)
            )
        } ?: run {
            Text(
                text = "Loading...",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyScreenContent(overviewUiState = OverviewUiState())
}