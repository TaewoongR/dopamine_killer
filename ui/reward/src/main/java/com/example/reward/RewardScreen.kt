package com.example.reward

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
// import R class for accessing drawable resources
import com.example.reward.R

val backgroundColor: Color = Color(android.graphics.Color.parseColor("#EFEFEF"))

@Composable
fun RewardScreen(
    navController: NavController,
    viewModel: RewardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.loadRewardData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp) // 상단에 여백 추가
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center // 중앙 정렬
            ) {
                Text(
                    text = "Badges",
                    fontSize = 28.sp, // 글자 크기 키움
                    fontWeight = FontWeight.Bold,
                )
            }
            rewardContent(uiState, viewModel)
        }
    }

    BackHandler {
        navController.navigate("overview_route") {
            popUpTo(0) { inclusive = true }
        }
    }
}




@Composable
fun rewardContent(uiState: List<RewardUiState>, viewModel: RewardViewModel) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f
    val individualWidth = ((totalWidth) - 10.dp) / 2

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .offset(y = totalWidth * 0.1f)
                .width(totalWidth)
                .background(Color.White, RoundedCornerShape(16.dp))
                .fillMaxHeight()
        ) {
            CustomTabBar(totalWidth = totalWidth, individualWidth, uiState, viewModel)
        }
    }
}


@Composable
fun CustomTabBar(totalWidth: Dp, individualWidth: Dp, uiState: List<RewardUiState>, viewModel: RewardViewModel) {
    val tabs = listOf("취득한 배지", "미취득한 배지")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 커스텀 탭바
        Row(modifier = Modifier
            .offset(y = totalWidth * 0.08f)
            .background(Color.LightGray, RoundedCornerShape(12.dp))
            .height(totalWidth * 0.12f)
            .width(totalWidth * 0.8f)
            .padding(totalWidth * 0.01f)
            .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically) {
            tabs.forEachIndexed { index, title ->
                // 각 탭 정의
                TabItem(totalWidth = totalWidth, title = title, selected = index == selectedTabIndex) {
                    selectedTabIndex = index
                }
            }
        }
        // 선택된 탭의 내용 표시
        when (selectedTabIndex) {
            0 -> { EarnedBadgesTab(totalWidth, individualWidth, uiState, viewModel) }
            1 -> { UnearnedBadgesTab(totalWidth, individualWidth) }
        }
    }
}

@Composable
fun TabItem(totalWidth: Dp, title: String, selected: Boolean, onSelect: () -> Unit) {
    Column(modifier = Modifier
        .clickable(onClick = onSelect),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier
            .width(totalWidth * 0.39f)
            .fillMaxHeight()
            .background(
                color = if (selected) Color.White else Color.LightGray,
                RoundedCornerShape(12.dp)
            ),
            contentAlignment = Alignment.Center){
            Text(text = title, color = if (selected) Color.Black else Color.White, fontSize = 15.sp)
        }
    }
}

@Composable
fun EarnedBadgesTab(totalWidth: Dp, individualWidth: Dp, uiState: List<RewardUiState>, viewModel: RewardViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .offset(y = totalWidth * 0.04f)
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(individualWidth * 0.08f),
        verticalArrangement = Arrangement.spacedBy(individualWidth * 0.1f),
        horizontalArrangement = Arrangement.spacedBy(individualWidth * 0.08f)
    ) {
        item {
            Spacer(modifier = Modifier.height(individualWidth * 0.04f))
        }
        item {
            Spacer(modifier = Modifier.height(individualWidth * 0.04f))
        }

        items(uiState.size){
            EarnedBadges(
                imageUrl = uiState[it].imageUrl,
                bname = uiState[it].name,
                bdescription = uiState[it].description,
                individualWidth = individualWidth
            )
        }

        item {
            Spacer(modifier = Modifier.height(individualWidth * 0.8f))
        }
        item {
            Spacer(modifier = Modifier.height(individualWidth * 0.8f))
        }
    }
}

@Composable
fun UnearnedBadgesTab(totalWidth: Dp, individualWidth: Dp) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .offset(y = totalWidth * 0.04f)
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = individualWidth * 0.1f, vertical = individualWidth * 0.08f), // 좌우 패딩 추가
        verticalArrangement = Arrangement.spacedBy(individualWidth * 0.1f),
        horizontalArrangement = Arrangement.spacedBy(individualWidth * 0.1f) // 좌우 간격 조정
    ) {
        item {
            Spacer(modifier = Modifier.height(individualWidth * 0.04f))
        }
        item {
            Spacer(modifier = Modifier.height(individualWidth * 0.04f))
        }

        items(7) {
            UnearnedBadges(
                imageResId = com.example.local.R.drawable.lockedbadge, // 수정된 부분
                bname = "???",
                bdescription = "모든 배지에 도전해보세요!",
                individualWidth = individualWidth
            )
        }

        item {
            Spacer(modifier = Modifier.height(individualWidth * 0.8f))
        }
        item {
            Spacer(modifier = Modifier.height(individualWidth * 0.8f))
        }
    }
}



@Composable
fun EarnedBadges(imageUrl: String, bname: String, bdescription: String, individualWidth: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(individualWidth * 1.4f)
            .border(2.dp, Color.LightGray, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(individualWidth * 0.1f))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = bname,
                modifier = Modifier
                    .size(individualWidth * 0.6f)
                    .clip(RoundedCornerShape(99999.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(individualWidth * 0.1f))
            Text(text = bname, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(individualWidth * 0.06f))
            Text(text = bdescription, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}


@Composable
fun UnearnedBadges(imageResId: Int, bname: String, bdescription: String, individualWidth: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(individualWidth * 1.4f)
            .border(2.dp, Color.LightGray, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(individualWidth * 0.1f))
            Image(
                painter = painterResource(id = imageResId), // 리소스 ID를 사용하여 이미지 설정
                contentDescription = bname,
                modifier = Modifier
                    .size(individualWidth * 0.6f)
                    .clip(RoundedCornerShape(99999.dp)),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
            )
            Spacer(modifier = Modifier.height(individualWidth * 0.1f))
            Text(text = bname, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(individualWidth * 0.06f))
            Text(text = bdescription, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}
