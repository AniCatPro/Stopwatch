package com.example.stopwatch

import android.os.Bundle
import android.os.SystemClock
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.stopwatch.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var running = false
    var offset: Long = 0
    private val lapTimes = mutableListOf<String>()

    val OFFSET_KEY = "offset"
    val RUNNING_KEY = "running"
    val BASE_KEY = "base"

    private val stopWatchTag = "StopWatch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(stopWatchTag, "onCreate called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (savedInstanceState != null) {
            offset = savedInstanceState.getLong(OFFSET_KEY)
            running = savedInstanceState.getBoolean(RUNNING_KEY)
            if (running) {
                binding.stopwatch.base = savedInstanceState.getLong(BASE_KEY)
                binding.stopwatch.start()
            } else setBaseTime()
        }

        binding.startButton.setOnClickListener {
            if (!running) {
                setBaseTime()
                binding.stopwatch.start()
                running = true
            }
        }

        binding.pauseButton.setOnClickListener {
            if (running) {
                saveOffset()
                binding.stopwatch.stop()
                running = false
            }
        }

        binding.resetButton.setOnClickListener {
            offset = 0
            setBaseTime()
            binding.stopwatch.stop()
            running = false
            binding.lapTimes.text = ""
        }


        binding.lapButton.setOnClickListener {
            if (running) {
                val lapTime = formatTime(SystemClock.elapsedRealtime() - binding.stopwatch.base)
                lapTimes.add(0, lapTime)
                updateLapTimes()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(stopWatchTag, "onPause called")
        if (running) {
            saveOffset()
            binding.stopwatch.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(stopWatchTag, "onResume called")
        if (running) {
            setBaseTime()
            binding.stopwatch.start()
            offset = 0
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong(OFFSET_KEY, offset)
        outState.putBoolean(RUNNING_KEY, running)
        outState.putLong(BASE_KEY, binding.stopwatch.base)
        super.onSaveInstanceState(outState)
    }

    fun setBaseTime() {
        binding.stopwatch.base = SystemClock.elapsedRealtime() - offset
    }

    fun saveOffset() {
        offset = SystemClock.elapsedRealtime() - binding.stopwatch.base
    }

    private fun formatTime(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        val hours = (ms / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


    private fun updateLapTimes() {
        val lapTimesText = lapTimes.joinToString("\n") { it }
        val spannableText = SpannableString(lapTimesText)
        val lastLapIndex = 0
        val lastLap = lapTimes[lastLapIndex]
        val start = lapTimesText.indexOf(lastLap)
        val end = start + lastLap.length

        spannableText.setSpan(
            ForegroundColorSpan(getColor(android.R.color.holo_green_light)),
            start,
            end,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.lapTimes.text = spannableText
    }
}
