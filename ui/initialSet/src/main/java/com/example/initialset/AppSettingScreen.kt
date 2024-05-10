package com.example.initialset

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.initialSet.R

val backgroundColor: Color = Color(android.graphics.Color.parseColor("#EFEFEF"))
val keyColor: Color = Color(android.graphics.Color.parseColor("#FF9A62"))


@Composable
fun appSelection() {

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f
    val squareSize = (totalWidth - 10.dp) / 2 * 0.24f
    val ScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize(),
        contentAlignment = Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(totalWidth * 0.24f))
            Text(text = "분석할 앱을 선택하세요.",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = totalWidth * 0.16f))
            Box(
                modifier = Modifier
                    .width(totalWidth)
                    .aspectRatio(1f / 1.5f)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(ScrollState)
                ) {
                    Spacer(modifier = Modifier.height(totalWidth * 0.06f))
                    for (i in 0 until 30) {
                        IndividualApp(totalWidth)
                    }
                }
            }
        }
        Box(modifier = Modifier.offset(y = totalWidth * 1f)){
            setButton(totalWidth = totalWidth)
        }
    }
}

@Composable
fun IndividualApp(totalWidth: Dp) {
    val squareSize = (totalWidth - 10.dp) / 2 * 0.24f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = totalWidth * 0.08f, end = totalWidth * 0.08f,
                top = totalWidth * 0.03f, bottom = totalWidth * 0.03f
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconImage(
            modifier = Modifier.padding(
                end = totalWidth * 0.05f
            ),
            imageResId = R.drawable.iconex,
            size = squareSize * 0.8f,
            cornerRadius = 8.dp
        )

        Text(text = "App Name 1", style = TextStyle(fontSize = 14.sp))

        Spacer(modifier = Modifier.weight(1f))
        ToggleButton(totalWidth)

    }
}

@Composable
fun ToggleButton(totalWidth: Dp) {
    var isButtonEnabled by remember { mutableStateOf(false) }
    val circlePosition by animateFloatAsState(targetValue = if (isButtonEnabled) 1f else 0f,
        label = "FloatAnimation"
    )

    Box(
        modifier = Modifier
            .width(totalWidth * 0.13f)
            .height(totalWidth * 0.07f)
            .background(Color.LightGray, RoundedCornerShape(999.dp))
            .clickable { isButtonEnabled = !isButtonEnabled },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(totalWidth * 0.05f)
                .align(Alignment.CenterStart)
                .graphicsLayer {
                    // 동그라미의 수평 이동
                    val circleMargin = totalWidth * 0.01f
                    val motionRange =
                        totalWidth * 0.14f - totalWidth * 0.06f - circleMargin * 2
                    translationX =
                        circleMargin.toPx() + circlePosition * motionRange.toPx()
                }
                .background(Color.White, CircleShape)
        )
    }
}

@Composable
fun setButton(totalWidth: Dp){
    Box(
        modifier = Modifier
            .width(totalWidth)
            .aspectRatio(1f / 0.1875f)
            .background(keyColor, shape = RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "완료", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White))
    }
}

@Composable
fun IconImage(
    modifier: Modifier = Modifier,
    imageResId: Int,
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
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}


@Preview
@Composable
fun DefaultPreview5(){
    appSelection()
}