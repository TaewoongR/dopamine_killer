package com.example.overview

import androidx.compose.animation.core.VisibilityThreshold
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
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun OverviewScreen(
    navController: NavController,
    viewModel: OverviewViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val overviewUiState by viewModel.overviewUiState.collectAsState()

    MyScreenContent(overviewUiState)
}


val backgroundColor: Color = Color(android.graphics.Color.parseColor("#EFEFEF"))
val keyColor: Color = Color(android.graphics.Color.parseColor("#FF9A62"))
val subColor: Color = Color(android.graphics.Color.parseColor("#73A66A"))
val vagueText: Color = Color(android.graphics.Color.parseColor("#777777"))
val vagueColor: Color = Color(android.graphics.Color.parseColor("#F0F0F0"))

val ongoingRecordAppStreaks = arrayOf(3, 14, 42) // 임의 연속 날짜 데이터

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
                DonutGraph(modifier = Modifier.size(individualWidth), size = individualWidth, overviewUiState = overviewUiState)
                Spacer(modifier = Modifier.width(10.dp))
                barGraphOverview(modifier = Modifier.size(individualWidth), size = individualWidth)
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0 until 3) {
                    recordOverview(modifier = Modifier, aspectRatio = 1f/0.1875f, totalWidth = totalWidth, i)
                }
            }
            Rectangle(modifier = Modifier, aspectRatio = 1f/0.6f, totalWidth = totalWidth)
            //showImage()
        }
    }
}

@Composable
fun DonutGraph(modifier: Modifier, size: Dp, overviewUiState: OverviewUiState) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val percent = overviewUiState.dailyTime / 200
    val icon = overviewUiState.appIcon
    val iconSize = size * 0.24f

    Box(
        modifier = modifier
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
    ) {
        Canvas(

            modifier = Modifier.matchParentSize(),
            onDraw = {
                val boxSize = Size(size.toPx(), size.toPx())
                val center = Offset(boxSize.width / 2, boxSize.height / 2)
                val radius = (boxSize.minDimension / 2) * 0.6f // 도넛 반지름
                val strokeWidth = screenWidth.toPx() * 0.08f
                val startAngle = -90f // 12시 방향 각도 시작
                val sweepAngle = percent * 360f
                val squareSize = (size * 0.24f).toPx()// 아이콘 크기 설정
                val topLeftSquare = Offset(center.x - squareSize / 2, center.y - squareSize / 2)

                drawCircle(
                    color = vagueColor,
                    center = center,
                    radius = radius,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                drawArc(
                    color = keyColor,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )

                drawImage(
                    image = icon
                )
            }
        )
    }

}

@Composable
fun DisplayImage(
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Int = 0,  // 이미지 모서리 둥글기(0은 모서리가 둥글지 않음)
    borderWidth: Int = 2,  // 테두리의 두께
    borderColor: Color = Color.Black,  // 테두리의 색상
    onClick: (() -> Unit)? = null  // 클릭 이벤트 핸들러(null이면 클릭 이벤트 없음)
) {
    Image(
        bitmap = imageBitmap,
        contentDescription = "icon",
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .border(borderWidth.dp, borderColor, RoundedCornerShape(cornerRadius.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentScale = contentScale
    )
}

@Composable
fun barGraphOverview(modifier: Modifier, size: Dp) {
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
            val random = java.util.Random()

            val paddingTop = (size * 0.2f).toPx() // 위쪽 패딩
            val paddingBottom = (size * 0.24f).toPx() // 아래쪽 패딩, 텍스트 공간
            val totalBarsWidth = barWidth * 4 + barSpacing * 3
            val startX = (size.toPx() - totalBarsWidth) / 2

            // 임의의 높이를 가진 4개의 막대 그리기
            repeat(4) { index ->
                val barHeight =
                    (random.nextFloat()) // 일단 높이 랜덤으로 설정
                val barStartX = startX + index * (barWidth + barSpacing)
                val barTopY =
                    (1 - barHeight) * (size.toPx() - paddingTop - paddingBottom) // 패딩 적용

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
fun recordOverview(
    modifier: Modifier,
    aspectRatio: Float,
    totalWidth: Dp,
    index: Int
) {
    Box(
        modifier = modifier
            .width(totalWidth)
            .aspectRatio(aspectRatio)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically // 앱 아이콘, 째깐둥이 수직 중앙 정렬
        ) {
            // 정사각형 이미지 대신 사용할 Box
            Box(
                modifier = Modifier
                    .padding(totalWidth * 0.06f)
                    .size(((totalWidth) - 10.dp) / 2 * 0.24f)
                    .aspectRatio(1f)
                    .background(color = vagueColor, shape = RoundedCornerShape(8.dp))
            )

            // 최대 20개까지, int 값만큼 째깐둥이를 그림
            for (k in 1..minOf(ongoingRecordAppStreaks[index], 20)) {
                Box(
                    modifier = Modifier
                        .padding(end = totalWidth * 0.012f) // 째깐둥이 사이 간격 조절
                        .size(totalWidth * 0.020f, totalWidth * 0.032f) // 째깐둥이 크기
                        .background(subColor, shape = RoundedCornerShape(99.dp))
                )
            }
        }
    }
}

@Composable
fun Rectangle(modifier: Modifier, aspectRatio: Float, totalWidth: Dp){
    Box(
        modifier = Modifier
            .width(totalWidth)
            .aspectRatio(aspectRatio)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
    )
}

@Preview
@Composable
fun DefaultPreview() {
    MyScreenContent(overviewUiState = OverviewUiState())
}


/*@Composable
fun showImage(){
    Image(
        painter = painterResource(id = R.drawable.ex),
        contentDescription = "A call icon for calling"
    )
}*/