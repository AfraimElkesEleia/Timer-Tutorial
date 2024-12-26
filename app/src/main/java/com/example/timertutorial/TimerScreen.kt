package com.example.timertutorial

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    viewModel: CountDownViewModel,
    totalTime: Long,
    handleColor: Color,
    inactiveColor: Color,
    activeColor: Color,
    initialValue: Float = 1f,
    strokeWidth: Dp = 5.dp,
) {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    var value by remember { //Percentage value of time 0.77=>77%
        mutableFloatStateOf(initialValue)
    }
    var currentTime by remember {  //Time in millisecond we currently add
        mutableLongStateOf(totalTime)
    }
    var isTimerRunnig by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = viewModel.timeLeft, key2 = isTimerRunnig) {
        Log.d("tag", viewModel.timeLeft.toString())
        if (!viewModel.isFinished && isTimerRunnig) {
            //delay(100L)
            //currentTime -= 100L
            value =
                viewModel.timeLeft / viewModel.totalTime.toFloat() //currentTime/totalTime.toFloat()
        } else if (viewModel.isFinished) {
            value = 1f
        }
    }
    Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        Box(contentAlignment = Alignment.Center, modifier = modifier.onSizeChanged {
            size = it
        }) {
            Canvas(modifier = modifier) {
                drawArc(
                    color = inactiveColor,
                    startAngle = -215f,
                    sweepAngle = 250f,
                    useCenter = false,
                    size = Size(size.width.toFloat(), size.height.toFloat()),
                    style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = activeColor,
                    startAngle = -215f,
                    sweepAngle = if (!viewModel.isFinished) 250f * value else 250f, // Depends on percentage of timer
                    useCenter = false,
                    size = Size(size.width.toFloat(), size.height.toFloat()),
                    style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                )
                val center = Offset(size.width / 2f, size.height / 2f)
                val beta =
                    if (!viewModel.isFinished) (250f * value + 145f) * (PI / 180f).toFloat() else (250f + 145f) * (PI / 180f).toFloat()
                val r = size.width / 2f
                val a = cos(beta) * r
                val b = sin(beta) * r
                drawPoints(
                    listOf(Offset(center.x + a, center.y + b)),
                    pointMode = PointMode.Points,
                    color = handleColor,
                    strokeWidth = (strokeWidth * 3f).toPx(),
                    cap = StrokeCap.Round
                )
            }
            Text(
                text = viewModel.timerText.value,//(currentTime / 60000L).toString(),
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Button(
            onClick = {
//                if (currentTime<=0L){
//                    currentTime = totalTime
//                    isTimerRunnig = true
//                }else{
//                    isTimerRunnig = !isTimerRunnig
//
//                }
                if (!viewModel.isFinished) {
                    if (viewModel.isPlaying) {
                        viewModel.stopCountDownTimer()
                        isTimerRunnig = false
                        currentTime = viewModel.timeLeft
                    } else {
                        viewModel.startCountDownTimer()
                        isTimerRunnig = true
                    }
                } else {
                    viewModel.isFinished = false
                    isTimerRunnig = true
                    value = 1f
                    viewModel.timeLeft = totalTime
                    viewModel.startCountDownTimer()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!viewModel.isPlaying || viewModel.isFinished) Color.Green
                else Color.Red
            )
        ) {
            Text(text = if (viewModel.isPlaying && !viewModel.isFinished) "Stop" else if (!viewModel.isPlaying && !viewModel.isFinished) "Start" else "Restart")
        }
        Button(
            onClick = {
                viewModel.resetCountDownTimer()
                value = 1f
            },
            colors = ButtonDefaults.buttonColors(
                Color.Green
            )
        ) {
            Text(text = "Reset")
        }
    }
}