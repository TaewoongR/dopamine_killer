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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
            modifier = Modifier.fillMaxSize()
        ){
            item {
                Spacer(modifier = Modifier.height(86.dp))
                MostApps(modifier = Modifier, totalWidth = totalWidth, uiState.descendingList)
                Spacer(modifier = Modifier.height(18.dp))
            }

            item {
                Row(modifier = Modifier
                    .width(totalWidth)
                    .padding(end = totalWidth * 0.02f),
                    horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = {},
                        modifier = Modifier
                            .size(totalWidth * 0.1f)
                            .background(Color.White, RoundedCornerShape(8.dp, 8.dp))) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "추가",
                            tint = Color.DarkGray
                        )
                    }
                }
            }

            if (uiState.ongoingList.isEmpty() && uiState.finishedList.isEmpty()) {
                item {
                    EmptyState(modifier = Modifier.fillMaxWidth(), totalWidth = totalWidth)
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .width(totalWidth)
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                    ) {
                        Column {
                            if (uiState.ongoingList.isNotEmpty()) {
                                for (i in 0 until uiState.ongoingList.size) {      // 진행중인 개수
                                    val thisUiState = uiState.ongoingList[i]
                                    ongoingRecords(
                                        modifier = Modifier,
                                        aspectRatio = 1 / 0.1875f,
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
                                        }
                                    )
                                }
                            }
                            if (uiState.finishedList.isNotEmpty()) {
                                for (i in 0 until uiState.finishedList.size) {
                                    val thisUiState = uiState.ongoingList[i]
                                    finishedRecords(
                                        modifier = Modifier,
                                        aspectRatio = 1 / 0.1875f,
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
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item{Spacer(modifier = Modifier.height(100.dp))}
        }
    }
}

@Composable
fun MostApps(modifier: Modifier, totalWidth: Dp, descendingList: List<DescendingRecord>) {
    Box(
        modifier = Modifier
            .width(totalWidth)
            .wrapContentHeight()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(totalWidth * 0.06f)
    ) {
        Row {
            if(descendingList.isNotEmpty()){
                IconImage(
                    imageBitmap = descendingList[0].appIcon,
                    size = totalWidth * 0.4f,
                    cornerRadius = 12.dp
                )
                Spacer(modifier = Modifier.width(totalWidth * 0.04f))

                if(descendingList.size > 1){
                    Column {
                    // 두 번째 아이콘 이미지
                        IconImage(
                            imageBitmap = descendingList[1].appIcon,
                            size = totalWidth * 0.28f,
                            cornerRadius = 12.dp
                        )
                        Spacer(modifier = Modifier.height(totalWidth * 0.04f))

                        if(descendingList.size > 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Bottom
                            ) {
                            // 세 번째 아이콘 이미지
                                IconImage(
                                    imageBitmap = descendingList[2].appIcon,
                                    size = totalWidth * 0.2f,
                                    cornerRadius = 12.dp
                                )
                                Spacer(modifier = Modifier.weight(1f)) // 남은 공간을 채우기 위해 사용

                                if(descendingList.size > 3) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Spacer(modifier = Modifier.height(totalWidth * 0.04f))
                                        // 네 번째 아이콘 이미지
                                        IconImage(
                                            imageBitmap = descendingList[3].appIcon,
                                            size = totalWidth * 0.14f,
                                            cornerRadius = 12.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(descendingList.size > 4) {
            // 다섯 번째 아이콘 이미지
            IconImage(
                imageBitmap = descendingList[4].appIcon,
                size = totalWidth * 0.1f,
                cornerRadius = 12.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(y = -(totalWidth * 0.19f))
            )
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
                        expanded = false }
                )
            }
        }
    }
}

@Composable
fun finishedRecords(modifier: Modifier, aspectRatio: Float, totalWidth: Dp, index: Int, finishedList: List<FinishedRecord>, onDelete: () -> Unit){
    val squareSize = (totalWidth - 10.dp) / 2 * 0.24f
    var expanded by remember { mutableStateOf(false) }

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
                        expanded = false }
                )
            }
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier, totalWidth: Dp) {
    Box(
        modifier = modifier
            .padding(horizontal = totalWidth * 0.1f)
            .fillMaxWidth()
            .height(totalWidth * 1.2f)
            .background(Color.White, shape = RoundedCornerShape(16.dp, 0.dp, 16.dp, 16.dp)),
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