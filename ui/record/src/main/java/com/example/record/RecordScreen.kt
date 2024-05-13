package com.example.record

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


val backgroundColor: Color = Color(android.graphics.Color.parseColor("#EFEFEF"))
val keyColor: Color = Color(android.graphics.Color.parseColor("#FF9A62"))
val subColor: Color = Color(android.graphics.Color.parseColor("#73A66A"))
val vagueText: Color = Color(android.graphics.Color.parseColor("#777777"))
val vagueColor: Color = Color(android.graphics.Color.parseColor("#F0F0F0"))


val ongoingRecordAppStreaks = arrayOf(3, 14, 42) // 임의 날짜
val finishedRecordAppStreaks = arrayOf(20, 4, 13, 28, 2)

@Composable
fun RecordScreen(
    navController: NavController,
    viewModel: RecordViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    RecordContent(uiState)
}

@Composable
fun RecordContent(uiState: RecordUiState) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(86.dp))
            MostApps(modifier = Modifier, totalWidth = totalWidth, uiState)
            Spacer(modifier = Modifier.height(18.dp))
            Box(modifier = Modifier
                .width(totalWidth)
                .background(Color.White, shape = RoundedCornerShape(16.dp))) {
                Column {
                    for (i in 0 until 3)        // 진행중인 개수
                        ongoingRecords(
                            modifier = Modifier,
                            aspectRatio = 1 / 0.1875f,
                            totalWidth = totalWidth,
                            index = i,
                            uiState = uiState
                        )
                    for (i in 0 until 5)
                        finishedRecords(
                            modifier = Modifier,
                            aspectRatio = 1/0.1875f,
                            totalWidth = totalWidth,
                            index = i,
                            uiState = uiState
                        )
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun MostApps(modifier: Modifier, totalWidth: Dp, uiState: RecordUiState) {
    Box(
        modifier = Modifier
            .width(totalWidth)
            .wrapContentHeight()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(totalWidth * 0.06f)
    ) {
        Row {
            // 첫 번째 아이콘 이미지
            IconImage(
                imageBitmap = uiState.recordList[0].appIcon,
                size = totalWidth * 0.4f,
                cornerRadius = 12.dp
            )
            Spacer(modifier = Modifier.width(totalWidth * 0.04f))

            Column {
                // 두 번째 아이콘 이미지
                IconImage(
                    imageBitmap = uiState.recordList[1].appIcon,
                    size = totalWidth * 0.28f,
                    cornerRadius = 12.dp
                )
                Spacer(modifier = Modifier.height(totalWidth * 0.04f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 세 번째 아이콘 이미지
                    IconImage(
                        imageBitmap = uiState.recordList[2].appIcon,
                        size = totalWidth * 0.2f,
                        cornerRadius = 12.dp
                    )
                    Spacer(modifier = Modifier.weight(1f)) // 남은 공간을 채우기 위해 사용
                    Column(horizontalAlignment = Alignment.End) {
                        Spacer(modifier = Modifier.height(totalWidth * 0.04f))
                        // 네 번째 아이콘 이미지
                        IconImage(
                            imageBitmap = uiState.recordList[3].appIcon,
                            size = totalWidth * 0.14f,
                            cornerRadius = 12.dp
                        )
                    }
                }
            }
        }
        // 다섯 번째 아이콘 이미지
        IconImage(
            imageBitmap = uiState.recordList[4].appIcon,
            size = totalWidth * 0.1f,
            cornerRadius = 12.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(y = -(totalWidth * 0.19f))
        )
    }
}

@Composable
fun ongoingRecords(modifier: Modifier, aspectRatio: Float, totalWidth: Dp, index: Int, uiState: RecordUiState){
    val squareSize = (totalWidth - 10.dp) / 2 * 0.24f

    Box(
        modifier = Modifier
            .width(totalWidth)
            .aspectRatio(aspectRatio)
    ){
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = totalWidth * 0.02f),
            verticalAlignment = Alignment.CenterVertically // 앱 아이콘, 째깐둥이 수직 중앙 정렬
        ) {
            IconImage(
                modifier = Modifier.padding(start = totalWidth * 0.06f, end = totalWidth * 0.05f),
                imageBitmap = uiState.recordList[index].appIcon,
                size = squareSize,
                cornerRadius = 8.dp
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
            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "메뉴", // 접근성을 위한 설명
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun finishedRecords(modifier: Modifier, aspectRatio: Float, totalWidth: Dp, index: Int, uiState: RecordUiState){
    val squareSize = (totalWidth - 10.dp) / 2 * 0.24f

    Box(
        modifier = Modifier
            .width(totalWidth)
            .aspectRatio(aspectRatio)
    ){
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = totalWidth * 0.02f),
            verticalAlignment = Alignment.CenterVertically // 앱 아이콘, 째깐둥이 수직 중앙 정렬
        ) {
            IconImage(
                modifier = Modifier.padding(start = totalWidth * 0.06f, end = totalWidth * 0.05f),
                imageBitmap = uiState.recordList[index].appIcon,
                size = squareSize,
                cornerRadius = 8.dp
            )

            // 최대 18개까지, int 값만큼 째깐둥이를 그림
            for (k in 1..minOf(finishedRecordAppStreaks[index], 18)) {
                Box(
                    modifier = Modifier
                        .padding(end = totalWidth * 0.012f) // 째깐둥이 사이 간격 조절
                        .size(totalWidth * 0.020f, totalWidth * 0.032f) // 째깐둥이 크기
                        .background(Color.LightGray, shape = RoundedCornerShape(99.dp))
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            Canvas(modifier = Modifier) {
                val days = finishedRecordAppStreaks.getOrNull(index).toString()

                drawIntoCanvas {
                    val textPaint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        textSize = (totalWidth * 0.06f).toPx()
                        color = Color.DarkGray.toArgb()
                    }
                    val textWidth = textPaint.measureText(days)
                    val fontMetrics = textPaint.fontMetrics
                    val textHeight = fontMetrics.descent - fontMetrics.ascent

                    it.nativeCanvas.drawText(
                        days,
                        size.width - textWidth - totalWidth.value * 0.04f ,
                        (size.height / 2) + (textHeight / 2) - fontMetrics.descent,
                        textPaint
                    )
                }
            }
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "메뉴", // 접근성을 위한 설명
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
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
fun DefaultPreview2(){
    RecordContent(uiState = RecordUiState())
}