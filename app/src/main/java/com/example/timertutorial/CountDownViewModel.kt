package com.example.timertutorial

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timertutorial.TimeFormat.timeFormat
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CountDownViewModel : ViewModel() {
    private var countDownTimer: CountDownTimer? = null
    private val userInputMinute = TimeUnit.MINUTES.toMillis(1)
    private val userInputSeconds = TimeUnit.SECONDS.toMillis(0)
     val totalTime = userInputSeconds + userInputMinute
    var timeLeft by mutableLongStateOf(totalTime)
    var timerText = mutableStateOf(timeLeft.timeFormat())
    val countDownInterval = 1000L //every one second
    var isPlaying by mutableStateOf(false)
    var isFinished by mutableStateOf(false)
    fun startCountDownTimer() = viewModelScope.launch {
        isPlaying = true
        countDownTimer = object : CountDownTimer(timeLeft, countDownInterval) {
            override fun onTick(currentTimeLeft: Long) {
                timerText.value = currentTimeLeft.timeFormat()
                timeLeft= currentTimeLeft
            }
            override fun onFinish() {
                Log.d("tag","Done")
                timerText.value = totalTime.timeFormat()
                isPlaying= false
                isFinished = true
            }
        }.start()
    }
    fun stopCountDownTimer() = viewModelScope.launch {
        isPlaying = false
        countDownTimer?.cancel()
    }
    fun resetCountDownTimer() = viewModelScope.launch {
        isPlaying = false
        countDownTimer?.cancel()
        timerText.value = totalTime.timeFormat()
        timeLeft = totalTime
        isFinished = false
    }
}
