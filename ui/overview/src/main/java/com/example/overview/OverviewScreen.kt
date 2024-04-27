package com.example.overview

import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val backgroundColor: Color = Color(android.graphics.Color.parseColor("#EFEFEF"))

@ExperimentalMaterial3Api
@Composable
fun OverviewScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
            RoundedSquare()
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