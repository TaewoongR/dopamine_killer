package com.example.overview

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
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
    val appUiState by viewModel.overviewUiState.collectAsState()

    MyScreenContent()
}


@Composable
fun DisplayImage(
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    contentDescription: String = "Displayed Image",
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Int = 0,  // 이미지 모서리 둥글기(0은 모서리가 둥글지 않음)
    borderWidth: Int = 2,  // 테두리의 두께
    borderColor: Color = Color.Black,  // 테두리의 색상
    onClick: (() -> Unit)? = null  // 클릭 이벤트 핸들러(null이면 클릭 이벤트 없음)
) {
    Image(
        bitmap = imageBitmap,
        contentDescription = contentDescription,
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .border(borderWidth.dp, borderColor, RoundedCornerShape(cornerRadius.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentScale = contentScale
    )
}


@Composable
fun DisplayAppName(appName: String){
    Text(text = appName)
}

val backgroundColor: Color = Color(android.graphics.Color.parseColor("#EFEFEF"))
val keyColor: Color = Color(android.graphics.Color.parseColor("#FF9A62"))
val subColor: Color = Color(android.graphics.Color.parseColor("#73A66A"))

@Composable
fun MyScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .offset(y = 70.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 라운딩 처리가 된 작은 정사각형 두 개
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DonutGraph(percent = 0.8f)
                Spacer(modifier = Modifier.width(10.dp))
                RoundedSquare()
            }

            // 라운딩 처리가 된 직사각형 세 개
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0 until 3) {
                    LongRectangle()
                }
            }
            Rectangle()
        }
    }
}

@Composable
fun DonutGraph(percent: Float) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .background(color = Color.White, shape = RoundedCornerShape(percent * 30.dp))
    ) {
        Canvas(
            modifier = Modifier.matchParentSize(),
            onDraw = {
                val boxSize = Size(size.width, size.height) // 박스의 크기를 Canvas 크기로 가져옴
                val center = Offset(boxSize.width / 2, boxSize.height / 2) // 박스의 중앙 좌표
                val radius = (boxSize.minDimension / 2) * 0.7f // 도넛 그래프의 반지름
                val startAngle = -90f
                val sweepAngle = percent * 360f
                drawArc(
                    color = keyColor,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 30.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        )
    }
}

@Composable
fun RoundedSquare() {
    Box(
        modifier = Modifier
            .size(150.dp)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
    )
}

@Composable
fun LongRectangle() {
    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 60.dp)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
    )
}

@Composable
fun Rectangle(){
    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 184.dp)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
    )
}

@Preview
@Composable
fun DefaultPreview() {
    MyScreenContent()
}
