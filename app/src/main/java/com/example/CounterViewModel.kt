package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CounterUiState(
    val count: Long = 0L,
    val step: Int = 1,
    val totalClicks: Long = 0L,
    val maxCount: Long = 0L,
    val hapticEnabled: Boolean = true,
    val soundEnabled: Boolean = true
)

class CounterViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("counter_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        CounterUiState(
            count = prefs.getLong("count", 0L),
            step = prefs.getInt("step", 1),
            totalClicks = prefs.getLong("total_clicks", 0L),
            maxCount = prefs.getLong("max_count", 0L),
            hapticEnabled = prefs.getBoolean("haptic_enabled", true),
            soundEnabled = prefs.getBoolean("sound_enabled", true)
        )
    )
    val uiState: StateFlow<CounterUiState> = _uiState.asStateFlow()

    fun increment(customStep: Int? = null) {
        val addAmount = customStep ?: _uiState.value.step
        _uiState.update { currentState ->
            val newCount = currentState.count + addAmount
            val newTotal = currentState.totalClicks + addAmount
            val newMax = maxOf(currentState.maxCount, newCount)

            prefs.edit()
                .putLong("count", newCount)
                .putLong("total_clicks", newTotal)
                .putLong("max_count", newMax)
                .apply()

            currentState.copy(
                count = newCount,
                totalClicks = newTotal,
                maxCount = newMax
            )
        }
    }

    fun decrement() {
        _uiState.update { currentState ->
            val newCount = maxOf(0L, currentState.count - currentState.step)
            prefs.edit().putLong("count", newCount).apply()
            currentState.copy(count = newCount)
        }
    }

    fun reset() {
        _uiState.update { currentState ->
            prefs.edit().putLong("count", 0L).apply()
            currentState.copy(count = 0L)
        }
    }

    fun setStep(newStep: Int) {
        _uiState.update { currentState ->
            prefs.edit().putInt("step", newStep).apply()
            currentState.copy(step = newStep)
        }
    }

    fun toggleHaptic() {
        _uiState.update { currentState ->
            val newValue = !currentState.hapticEnabled
            prefs.edit().putBoolean("haptic_enabled", newValue).apply()
            currentState.copy(hapticEnabled = newValue)
        }
    }

    fun toggleSound() {
        _uiState.update { currentState ->
            val newValue = !currentState.soundEnabled
            prefs.edit().putBoolean("sound_enabled", newValue).apply()
            currentState.copy(soundEnabled = newValue)
        }
    }
}
