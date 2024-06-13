package com.example.record

import android.graphics.Color.parseColor
import android.graphics.ColorMatrix
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.local.user.UserTokenStore
import kotlin.coroutines.coroutineContext


val backgroundColor: Color = Color(android.graphics.Color.parseColor("#EFEFEF"))
val keyColor: Color = Color(android.graphics.Color.parseColor("#FF9A62"))
val subColor: Color = Color(android.graphics.Color.parseColor("#73A66A"))
val vagueText: Color = Color(android.graphics.Color.parseColor("#777777"))


@Composable
fun RecordScreen(
    navController: NavController,
    viewModel: RecordViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
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
    val context = LocalContext.current

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp), // 상단에 여백 추가
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Goal",
                fontSize = 28.sp, // 글자 크기 키움
                fontWeight = FontWeight.Bold,
            )
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(totalWidth * 0.06f),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(totalWidth * 0.1f))
                    Row(modifier = Modifier
                        .width(totalWidth), // end 패딩 삭제
                        horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = {
                            navController.navigate("goal_create_screen") { // 대상 루트로 변경
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
                                                viewModel.deleteOngoingRecord(
                                                    Pair(
                                                        thisUiState.appName,
                                                        thisUiState.date
                                                    ),
                                                    UserTokenStore.getToken(context)!!,
                                                    UserTokenStore.getUserId(context)
                                                )
                                            }
                                        )
                                    }
                                }
                                if (uiState.finishedList.isNotEmpty()) {
                                    for (i in 0 until uiState.finishedList.size) {
                                        val thisUiState = uiState.finishedList[i]
                                        finishedRecords(
                                            modifier = Modifier,
                                            aspectRatio = 1 / 0.26f,
                                            totalWidth = totalWidth,
                                            index = i,
                                            finishedList = uiState.finishedList,
                                            onDelete = {
                                                viewModel.deleteFinishedRecord(
                                                    Pair(
                                                        thisUiState.appName,
                                                        thisUiState.date
                                                    ),
                                                    UserTokenStore.getToken(context)!!,
                                                    UserTokenStore.getUserId(context)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(10.dp)) }
            }
        }
    }
}


@Composable
fun ongoingRecords(modifier: Modifier, aspectRatio: Float, totalWidth: Dp, index: Int, ongoingList: List<OngoingRecord>, onDelete: () -> Unit) {
    val squareSize = (totalWidth - 10.dp) / 2 * 0.24f
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(totalWidth)
            .background(Color.White, RoundedCornerShape(16.dp)) // 백그라운드 추가
            .aspectRatio(aspectRatio)
    ) {
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
            Column {
                Spacer(modifier = Modifier.height(totalWidth * 0.04f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 최대 20개까지, int 값만큼 째깐둥이를 그림
                    val howLong = ongoingList[index].howLong
                    Row(
                        modifier = Modifier
                            .width(totalWidth * 0.635f)
                            .border(1.dp, vagueText.copy(0.1f), shape = RoundedCornerShape(4.dp))
                            .background(vagueText.copy(0.2f), shape = RoundedCornerShape(4.dp))
                    ) {
                        Box(modifier.padding(start = totalWidth * 0.0035f))
                        for (k in 1..if (howLong == 0) 1 else if (howLong % 18 == 0) 18 else if (howLong > 90) 18 else howLong % 18) {
                            Box(
                                modifier = Modifier
                                    .padding(end = totalWidth * 0.0035f) // 패딩 수정
                                    .padding(start = totalWidth * 0.0035f)
                                    .padding(top = totalWidth * 0.003f)
                                    .padding(bottom = totalWidth * 0.003f)
                                    .size(totalWidth * 0.028f, totalWidth * 0.05f)
                                    .background(
                                        color = when (howLong) {
                                            0 -> Color.Transparent
                                            in 1..18 -> Color(parseColor("#A5D6A7"))
                                            in 19..36 -> Color(parseColor("#99CD82"))
                                            in 37..54 -> Color(parseColor("#8BC34A"))
                                            in 55..72 -> Color(parseColor("#6FBF40"))
                                            in 73..90 -> Color(parseColor("#5FAE46"))
                                            else -> Color(parseColor("#4CAF50"))
                                        },
                                        shape = RoundedCornerShape(3.dp)
                                    )
                            )
                        }
                        Box(modifier.padding(end = totalWidth * 0.0035f))
                    }
                    Text(text = "${ongoingList[index].howLong}일", Modifier.padding(start = 2.dp), fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(totalWidth * 0.03f))
                val dailyUsage = (ongoingList[index].todayUsage / 60)
                val goalUsage = (ongoingList[index].goalTime / 60)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { dailyUsage.toFloat() / goalUsage.toFloat() },
                        modifier = Modifier
                            .width(totalWidth * 0.686f)
                            .height(totalWidth * 0.03f)
                            .clip(RoundedCornerShape(4.dp)),
                        color = keyColor,
                        trackColor = vagueText.copy(0.2f),
                        strokeCap = StrokeCap.Round,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(start = 1.dp),
                        text = dailyUsage.toString() + "분",
                        style = TextStyle(Color.Gray, fontSize = 10.sp)
                    )
                    Text(
                        modifier = Modifier.padding(end = 27.dp),
                        text = goalUsage.toString() + "분",
                        style = TextStyle(Color.Gray, fontSize = 10.sp)
                    )
                }
            }
        }
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(bottom = 60.dp, end = 20.dp)
                .width(15.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "메뉴",
                tint = Color.Gray
            )
            if (expanded) {
                Popup(
                    alignment = Alignment.Center,
                    offset = IntOffset(x = 85, y = 25),
                    properties = PopupProperties(focusable = true),
                    onDismissRequest = { expanded = false }
                ) {
                    Box(
                        modifier = Modifier
                            .border(
                                width = 0.2.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            ) // 테두리 색 추가
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .width(totalWidth * 0.14f)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "삭제",
                                style = TextStyle(Color.Black, fontSize = 10.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showDialog = true
                                        expanded = false
                                    }
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = ongoingList[index].date.run { "${this.substring(0, 4)}/${this.substring(4, 6)}/${this.substring(6, 8)}" },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = totalWidth * 0.04f, top = totalWidth * 0.03f),
            style = TextStyle(Color.Gray, fontSize = 9.sp)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "알림", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
            },
            text = {
                Text("정말로 삭제하시겠습니까?", style = TextStyle(fontSize = 16.sp))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(keyColor)
                ) {
                    Text("삭제", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(keyColor)
                ) {
                    Text("취소", color = Color.White)
                }
            }
        )
    }
}
@Composable
fun finishedRecords(modifier: Modifier, aspectRatio: Float, totalWidth: Dp, index: Int, finishedList: List<FinishedRecord>, onDelete: () -> Unit) {
    val squareSize = (totalWidth - 10.dp) / 2 * 0.24f
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(totalWidth)
            .background(Color.White, RoundedCornerShape(16.dp)) // 백그라운드 추가
            .aspectRatio(aspectRatio)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = totalWidth * 0.02f),
            verticalAlignment = Alignment.CenterVertically // 앱 아이콘, 째깐둥이 수직 중앙 정렬
        ) {
            FinishedIconImage(
                modifier = Modifier.padding(start = totalWidth * 0.05f, end = totalWidth * 0.05f),
                imageBitmap = finishedList[index].appIcon,
                size = squareSize,
                cornerRadius = 8.dp,
                colorFilter = ColorFilter.colorMatrix(androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(0f) })
            )

            Column {
                val finishedPercentage: Float = finishedList[index].howLong / 90f
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { finishedPercentage },
                        modifier = Modifier
                            .width(totalWidth * 0.635f)
                            .height(totalWidth * 0.06f)
                            .clip(RoundedCornerShape(5.dp)),
                        color = vagueText.copy(0.3f),
                        trackColor = vagueText.copy(0.2f),
                        strokeCap = StrokeCap.Butt,
                    )
                    Text(text = "${finishedList[index].howLong}일", Modifier.padding(start = 2.dp), fontSize = 13.sp)
                }
                Row(
                    modifier = Modifier.width(totalWidth * 0.635f), // Ensure the Row fills the width of its parent
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val theEl = finishedList[index]
                    Text(
                        modifier = Modifier.padding(start = 1.dp),
                        text = "${(finishedPercentage * 100).toInt()}%",
                        style = TextStyle(Color.Gray, fontSize = 10.sp)
                    )
                    Text(
                        modifier = Modifier,
                        text = "${theEl.goalTime / 60}분/90일",
                        style = TextStyle(Color.Gray, fontSize = 10.sp)
                    )
                }
            }
        }
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(bottom = 60.dp, end = 20.dp)
                .width(15.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "메뉴",
                tint = Color.Gray
            )
            if (expanded) {
                Popup(
                    alignment = Alignment.Center,
                    offset = IntOffset(x = 85, y = 25),
                    properties = PopupProperties(focusable = true),
                    onDismissRequest = { expanded = false }
                ) {
                    Box(
                        modifier = Modifier
                            .border(
                                width = 0.2.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            ) // 테두리 색 추가
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .width(totalWidth * 0.14f)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "삭제",
                                style = TextStyle(Color.Black, fontSize = 10.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showDialog = true
                                        expanded = false
                                    }
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = finishedList[index].date.run { "${this.substring(0, 4)}/${this.substring(4, 6)}/${this.substring(6, 8)}" },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = totalWidth * 0.04f, top = totalWidth * 0.03f),
            style = TextStyle(Color.Gray, fontSize = 9.sp)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "알림", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
            },
            text = {
                Text("정말로 삭제하시겠습니까?", style = TextStyle(fontSize = 16.sp))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(keyColor)
                ) {
                    Text("삭제", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(keyColor)
                ) {
                    Text("취소", color = Color.White)
                }
            }
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

@Composable
fun FinishedIconImage(
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    size: Dp,
    cornerRadius: Dp,
    colorFilter: ColorFilter?
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
            contentScale = ContentScale.Crop,
            colorFilter = colorFilter
        )
    }
}