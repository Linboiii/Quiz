package com.example.quiz

import kotlinx.serialization.Serializable

data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val answer: String
)

data class QuizResult(
    val num: Int,
    val text: String,
    val userAns: String,
    val correctAns: String,
    val isCorrect: Boolean
)

@Serializable object StartRoute
@Serializable object QuizRoute
@Serializable object ResultRoute