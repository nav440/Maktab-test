package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.example.viewmodel.TestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: TestViewModel,
    onHomeClick: () -> Unit
) {
    val quizState by viewModel.quizState.collectAsState()
    val result = quizState.lastResult
    val questions = quizState.questions
    val selectedAnswers = quizState.selectedAnswers

    val gradeText = when (result?.grade) {
        5 -> "5 - A'lo (Mukammal)"
        4 -> "4 - Yaxshi"
        3 -> "3 - Qoniqarli"
        else -> "2 - Qoniqarsiz"
    }

    val gradeColor = when (result?.grade) {
        5 -> Color(0xFF2E7D32) // Green
        4 -> Color(0xFF1565C0) // Blue
        3 -> Color(0xFFEF6C00) // Orange
        else -> Color(0xFFC62828) // Red
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Test Natijasi va Tahlil") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            )
        }
    ) { padding ->
        if (result == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Natijalar mavjud emas.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Score Summary Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = result.testTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Grade Circle Badge
                            Surface(
                                shape = CircleShape,
                                color = gradeColor,
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${result.grade}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 36.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            Text(
                                text = gradeText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = gradeColor
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                StatBadge("To'g'ri javoblar", "${result.score} / ${result.totalQuestions}")
                                StatBadge("Foiz", "${result.percentage.toInt()}%")
                            }

                            Button(
                                onClick = onHomeClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .testTag("btn_back_home")
                            ) {
                                Icon(Icons.Default.Home, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Bosh sahifaga qaytish")
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Savollar tahlili",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                itemsIndexed(questions) { index, question ->
                    val userAns = selectedAnswers[index]
                    val isCorrect = userAns == question.correctAnswerIndex
                    val optionLabels = listOf("A", "B", "C", "D")
                    val options = listOf(question.optionA, question.optionB, question.optionC, question.optionD)

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) MaterialTheme.colorScheme.surfaceContainerLow
                            else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${index + 1}-savol",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = if (isCorrect) Icons.Default.Done else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }

                            Text(
                                text = question.questionText,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "To'g'ri javob: ${optionLabels[question.correctAnswerIndex]}) ${options[question.correctAnswerIndex]}",
                                fontSize = 13.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )

                            if (userAns != null) {
                                Text(
                                    text = "Sizning javobingiz: ${optionLabels[userAns]}) ${options[userAns]}",
                                    fontSize = 13.sp,
                                    color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            } else {
                                Text(
                                    text = "Siz javob bermadingiz",
                                    fontSize = 13.sp,
                                    color = Color(0xFFC62828)
                                )
                            }

                            Text(
                                text = "Izoh: ${question.explanation}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
