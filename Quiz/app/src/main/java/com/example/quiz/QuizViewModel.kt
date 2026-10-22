package com.example.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class QuizUiState(
    val index: Int = 0,
    val selected: String? = null,
    val isDone: Boolean = false,
    val questions: List<Question> = emptyList()
) {
    val currentQuestion: Question? get() = questions.getOrNull(index)
}

class QuizViewModel(
    private val repo: QuizRepository = QuizRepository
) : ViewModel() {

    private val _quizState = MutableStateFlow(QuizUiState())

    val uiState: StateFlow<QuizUiState> = combine(_quizState, repo.questions) { state, list ->
        state.copy(questions = list)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = QuizUiState(questions = repo.questions.value)
    )

    fun onSelect(opt: String) {
        _quizState.update { it.copy(selected = opt) }
    }

    fun onNext() {
        val state = _quizState.value
        val questionsList = repo.questions.value
        val q = questionsList.getOrNull(state.index) ?: return
        val ans = state.selected ?: return

        repo.addResult(state.index + 1, q, ans)

        if (state.index + 1 < questionsList.size) {
            _quizState.update {
                it.copy(
                    index = it.index + 1,
                    selected = null
                )
            }
        } else {
            _quizState.update { it.copy(isDone = true) }
        }
    }
}