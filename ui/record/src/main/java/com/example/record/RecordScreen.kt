package com.example.record

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


val backgroundColor: Color = Color(android.graphics.Color.parseColor("#EFEFEF"))
val keyColor: Color = Color(android.graphics.Color.parseColor("#FF9A62"))
val subColor: Color = Color(android.graphics.Color.parseColor("#73A66A"))
val vagueText: Color = Color(android.graphics.Color.parseColor("#777777"))
val vagueColor: Color = Color(android.graphics.Color.parseColor("#F0F0F0"))


@Composable
fun RecordScreen(
    navController: NavController,
    viewModel: RecordViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.loadRecordData()
    }
    RecordContent(uiState,viewModel, navController)
    BackHandler {
        navController.navigate("overview_route") {
            popUpTo(0) { inclusive = true }
        }
    }
}

@Composable
fun RecordContent(uiState: RecordUiState, viewModel: RecordViewModel, navController: NavController) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ){
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(totalWidth * 0.06f),
            modifier = Modifier.fillMaxSize()
        ){

            item {
                Spacer(modifier = Modifier.height(totalWidth * 0.1f))
                Row(modifier = Modifier
                    .width(totalWidth), // end 패딩 삭제
                    horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = {
                        navController.navigate("goal_create_screen") { // 대상 루트로 변경하세요
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                        },
                        modifier = Modifier
                            .width(totalWidth * 0.18f)
                            .height(totalWidth * 0.1f)
                            .background(Color.Transparent, RoundedCornerShape(16.dp))) {
                        // 아이콘을 텍스트로 대체
                        Text(text = "추가 >", style = TextStyle(fontSize = 18.sp, color = Color.Black, fontWeight = SemiBold))
                    }
                }
            }

            if (uiState.ongoingList.isEmpty() && uiState.finishedList.isEmpty()) {
                item {
                    EmptyState(modifier = Modifier, totalWidth = totalWidth)
                }
            } else {
                item {
                    Box(modifier = Modifier.width(totalWidth)) {
                        Column(verticalArrangement = Arrangement.spacedBy(totalWidth * 0.04f)) {
                            if (uiState.ongoingList.isNotEmpty()) {
                                for (i in 0 until uiState.ongoingList.size) {      // 진행중인 개수
                                    val thisUiState = uiState.ongoingList[i]
                                    ongoingRecords(
                                        modifier = Modifier,
                                        aspectRatio = 1 / 0.26f,
                                        totalWidth = totalWidth,
                                        index = i,
                                        ongoingList = uiState.ongoingList,
                                        onDelete = {
                                            viewModel.deleteRecord(
                                                Pair(
                                                    thisUiState.appName,
                                                    thisUiState.date
                                                )
                                            )
                                            navController.navigate("record_route") {
                                                popUpTo("record_route") { inclusive = true }
                                            }
                                        }
                                    )
                                }
                            }
                            if (uiState.finishedList.isNotEmpty()) {
                                for (i in 0 until uiState.finishedList.size) {
                                    val thisUiState = uiState.ongoingList[i]
                                    finishedRecords(
                                        modifier = Modifier,
                                        aspectRatio = 1 / 0.26f,
                                        totalWidth = totalWidth,
                                        index = i,
                                        finishedList = uiState.finishedList,
                                        onDelete = {
                                            viewModel.deleteRecord(
                                                Pair(
                                                    thisUiState.appName,
                                                    thisUiState.date
                                                )
                                            )
                                            navController.navigate("record_route") {
                                                popUpTo("record_route") { inclusive = true }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item{Spacer(modifier = Modifier.height(10.dp))}
        }
    }
}

@Composable
fun ongoingRecords(modifier: Modifier, aspectRatio: Float, totalWidth: Dp, index: Int, ongoingList: List<OngoingRecord>, onDelete: () -> Unit){
    val squareSize = (totalWidth - 10.dp) / 2 * 0.24f
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(totalWidth)
            .background(Color.White, RoundedCornerShape(16.dp)) // 백그라운드 추가
            .aspectRatio(aspectRatio)
    ){
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = totalWidth * 0.02f),
            verticalAlignment = Alignment.CenterVertically // 앱 아이콘, 째깐둥이 수직 중앙 정렬
        ) {
            IconImage(
                modifier = Modifier.padding(start = totalWidth * 0.05f, end = totalWidth * 0.05f),
                imageBitmap = ongoingList[index].appIcon,
                size = squareSize,
                cornerRadius = 8.dp
            )

            // 최대 20개까지, int 값만큼 째깐둥이를 그림
            for (k in 1..minOf(ongoingList[index].howLong / 5, 18)){
                Box(
                    modifier = Modifier
                        .padding(end = totalWidth * 0.012f) // 째깐둥이 사이 간격 조절
                        .size(totalWidth * 0.020f, totalWidth * 0.032f) // 째깐둥이 크기
                        .background(subColor, shape = RoundedCornerShape(99.dp))
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            Canvas(modifier = Modifier) {
                val days = ongoingList[index].howLong.toString()

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
            IconButton(onClick = {expanded = ! expanded}, modifier = Modifier.width(24.dp)) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "메뉴",
                    tint = Color.Gray
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = (16).dp, y = 0.dp), // 클릭 아이콘 바로 밑에 위치
                modifier = Modifier
                    .background(Color.White)
                    .width(totalWidth * 0.3f) // 메뉴 크기 조정
            ){
                DropdownMenuItem(
                    text = { Text("삭제") },
                    modifier = Modifier.background(Color.White),
                    onClick = {
                        onDelete ()
                        expanded = false
                    }
                )
            }
        }
        Text(
            text = ongoingList[index].date.run{ "${this.substring(0, 4)}/${this.substring(4, 6)}/${this.substring(6, 8)}"},
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = totalWidth * 0.05f, top = totalWidth * 0.024f),
            style = TextStyle(Color.Gray, fontSize = 12.sp)
        )
    }
}

@Composable
fun finishedRecords(modifier: Modifier, aspectRatio: Float, totalWidth: Dp, index: Int, finishedList: List<FinishedRecord>, onDelete: () -> Unit){
    val squareSize = (totalWidth - 10.dp) / 2 * 0.24f
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(totalWidth)
            .background(Color.White, RoundedCornerShape(16.dp)) // 백그라운드 추가
            .aspectRatio(aspectRatio)
    ){
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = totalWidth * 0.02f),
            verticalAlignment = Alignment.CenterVertically // 앱 아이콘, 째깐둥이 수직 중앙 정렬
        ) {
            IconImage(
                modifier = Modifier.padding(start = totalWidth * 0.05f, end = totalWidth * 0.05f),
                imageBitmap = finishedList[index].appIcon,
                size = squareSize,
                cornerRadius = 8.dp
            )

            // 최대 18개까지, int 값만큼 째깐둥이를 그림
            for (k in 1..minOf(finishedList[index].howLong / 5, 18)) {
                Box(
                    modifier = Modifier
                        .padding(end = totalWidth * 0.012f) // 째깐둥이 사이 간격 조절
                        .size(totalWidth * 0.020f, totalWidth * 0.032f) // 째깐둥이 크기
                        .background(Color.LightGray, shape = RoundedCornerShape(99.dp))
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            Canvas(modifier = Modifier) {
                val days = finishedList[index].howLong.toString()

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
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = totalWidth * 0.68f, y = 0.dp),
                modifier = Modifier.background(Color.White)
            ) {
                DropdownMenuItem(
                    text = { Text("삭제") },
                    modifier = Modifier.background(Color.White),
                    onClick = {
                        onDelete ()
                        expanded = false
                    }
                )
            }
        }
        Text(
            text = finishedList[index].date.run{"${this.substring(0, 4)}/${this.substring(4, 6)}/${this.substring(6, 8)}"},
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = totalWidth * 0.05f, top = totalWidth * 0.024f),
            style = TextStyle(Color.Gray, fontSize = 12.sp)
        )
    }
}

@Composable
fun EmptyState(modifier: Modifier, totalWidth: Dp) {
    Box(
        modifier = modifier
            .padding(horizontal = totalWidth * 0.1f)
            .fillMaxWidth()
            .height(totalWidth * 0.25f)
            .background(Color.White, shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "표시할 기록이 없습니다.",
            style = TextStyle(
                fontSize = 18.sp,
                color = Color.Gray
            )
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