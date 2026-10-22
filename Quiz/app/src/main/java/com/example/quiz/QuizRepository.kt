package com.example.quiz

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object QuizRepository {
    private val data = listOf(
        Question(1, "What is 12 + 15?", listOf("25", "27", "28", "30"), "27"),
        Question(2, "What is 8 x 7?", listOf("48", "54", "56", "64"), "56"),
        Question(3, "What is 100 - 43?", listOf("57", "63", "53", "47"), "57"),
        Question(4, "What is 144 / 12?", listOf("10", "11", "12", "14"), "12"),
        Question(5, "What is 5 + 3 x 4?", listOf("32", "17", "20", "23"), "17")
    )

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _results = MutableStateFlow<List<QuizResult>>(emptyList())
    val results: StateFlow<List<QuizResult>> = _results.asStateFlow()

    fun startQuiz(count: Int) {
        _questions.value = data.shuffled().take(count.coerceAtMost(data.size))
        _results.value = emptyList()
    }

    fun addResult(num: Int, q: Question, ans: String) {
        val item = QuizResult(
            num = num,
            text = q.text,
            userAns = ans,
            correctAns = q.answer,
            isCorrect = ans == q.answer
        )
        _results.update { it + item }
    }
}