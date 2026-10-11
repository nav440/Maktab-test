package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.example.viewmodel.TestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherScreen(
    viewModel: TestViewModel,
    onBack: () -> Unit
) {
    var testId by remember { mutableStateOf("math_g8_q1_BSB") }
    var questionText by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctIndex by remember { mutableStateOf(1) }
    var explanation by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("O'qituvchi rejimi: Savol qo'shish") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_teacher_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Test bazasiga yangi savol qo'shish",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = testId,
                onValueChange = { testId = it },
                label = { Text("Test ID (masalan: math_g8_q1_BSB)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                label = { Text("Savol matni") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = optionA,
                onValueChange = { optionA = it },
                label = { Text("A varianti") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = optionB,
                onValueChange = { optionB = it },
                label = { Text("B varianti") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = optionC,
                onValueChange = { optionC = it },
                label = { Text("C varianti") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = optionD,
                onValueChange = { optionD = it },
                label = { Text("D varianti") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = explanation,
                onValueChange = { explanation = it },
                label = { Text("To'g'ri javob izohi") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (questionText.isNotBlank()) {
                        viewModel.addCustomQuestion(
                            testId = testId,
                            questionText = questionText,
                            optA = optionA.ifBlank { "A varianti" },
                            optB = optionB.ifBlank { "B varianti" },
                            optC = optionC.ifBlank { "C varianti" },
                            optD = optionD.ifBlank { "D varianti" },
                            correctIdx = correctIndex,
                            explanation = explanation.ifBlank { "To'g'ri javob" }
                        )
                        successMsg = true
                        questionText = ""
                        optionA = ""
                        optionB = ""
                        optionC = ""
                        optionD = ""
                        explanation = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_save_question")
            ) {
                Icon(Icons.Default.Done, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Savolni saqlash")
            }

            if (successMsg) {
                Text(
                    text = "Savol bazaga muvaffaqiyatli qo'shildi!",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
