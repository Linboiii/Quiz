package com.example.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = StartRoute,
        modifier = modifier
    ) {
        composable<StartRoute> {
            StartScreen(
                onStart = { count ->
                    QuizRepository.startQuiz(count)
                    navController.navigate(QuizRoute)
                }
            )
        }
        composable<QuizRoute> {
            QuizScreen(
                onFinish = {
                    navController.navigate(ResultRoute) {
                        popUpTo<StartRoute>()
                    }
                }
            )
        }
        composable<ResultRoute> {
            ResultScreen(
                onRestart = {
                    navController.navigate(StartRoute) {
                        popUpTo<StartRoute> { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun StartScreen(
    onStart: (Int) -> Unit,
    viewModel: StartViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Mini Quiz", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("How many questions do you want? (1-5)", color = Color.Gray)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.input,
            onValueChange = viewModel::onInputChange,
            label = { Text("Number of questions") },
            isError = uiState.err,
            supportingText = { if (uiState.err) Text("Maximum is 5 questions") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val num = viewModel.getValidCount()
                if (num != null) onStart(num)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C5680)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Quiz")
        }
    }
}

@Composable
fun QuizScreen(
    onFinish: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isDone) {
        if (uiState.isDone) onFinish()
    }

    val q = uiState.currentQuestion ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Question ${uiState.index + 1}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(q.text, fontSize = 16.sp, color = Color.Gray)

        Spacer(Modifier.height(24.dp))

        q.options.forEach { opt ->
            val selected = uiState.selected == opt
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (selected) Color(0xFF4C5680) else Color(0xFFE8EBFA),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { viewModel.onSelect(opt) }
            ) {
                Text(
                    text = opt,
                    color = if (selected) Color.White else Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = viewModel::onNext,
            enabled = uiState.selected != null,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C5680)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next")
        }
    }
}

@Composable
fun ResultScreen(
    onRestart: () -> Unit,
    viewModel: ResultViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        Text("Quiz Finished!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Your Score: ${uiState.score} / ${uiState.total}", fontSize = 16.sp)

        Spacer(Modifier.height(24.dp))

        if (uiState.wrongList.isEmpty()) {
            Text("Perfect! No wrong answers.", color = Color.Gray, modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.wrongList) { item ->
                    Column {
                        Text("Q${item.num}: ${item.text}", fontWeight = FontWeight.Bold)
                        Text("Your Answer: ${item.userAns}", color = Color.Red, fontSize = 13.sp)
                        Text("Correct Answer: ${item.correctAns}", color = Color.Gray, fontSize = 13.sp)
                        HorizontalDivider(Modifier.padding(top = 8.dp))
                    }
                }
            }
        }

        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C5680)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Restart Quiz")
        }
    }
}