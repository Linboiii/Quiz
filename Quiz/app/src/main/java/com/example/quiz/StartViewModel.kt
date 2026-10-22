package com.example.quiz

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StartUiState(
    val input: String = "",
    val showError: Boolean = false
) {
    val err: Boolean get() = showError && ((input.toIntOrNull() ?: 0) !in 1..5)
}

class StartViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StartUiState())
    val uiState: StateFlow<StartUiState> = _uiState.asStateFlow()

    fun onInputChange(value: String) {
        _uiState.update { it.copy(input = value, showError = false) }
    }

    fun getValidCount(): Int? {
        val num = _uiState.value.input.toIntOrNull() ?: 0
        return if (num in 1..5) {
            num
        } else {
            _uiState.update { it.copy(showError = true) }
            null
        }
    }
}