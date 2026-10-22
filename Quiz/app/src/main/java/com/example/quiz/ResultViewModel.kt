package com.example.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ResultUiState(
    val list: List<QuizResult> = emptyList()
) {
    val total: Int get() = list.size
    val score: Int get() = list.count { it.isCorrect }
    val wrongList: List<QuizResult> get() = list.filter { !it.isCorrect }
}

class ResultViewModel(
    repo: QuizRepository = QuizRepository
) : ViewModel() {

    val uiState: StateFlow<ResultUiState> = repo.results
        .map { ResultUiState(list = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ResultUiState(list = repo.results.value)
        )
}