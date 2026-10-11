package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.model.QuestionEntity
import com.example.model.SubjectEntity
import com.example.model.TestEntity
import com.example.model.TestResultEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizState(
    val test: TestEntity? = null,
    val questions: List<QuestionEntity> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(), // questionIndex -> selectedOptionIndex (0-3)
    val isFinished: Boolean = false,
    val lastResult: TestResultEntity? = null
)

class TestViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).testDao()

    val subjects: Flow<List<SubjectEntity>> = dao.getAllSubjects()
    val testResults: Flow<List<TestResultEntity>> = dao.getAllResults()

    private val _selectedGrade = MutableStateFlow(8) // default 8th grade
    val selectedGrade: StateFlow<Int> = _selectedGrade.asStateFlow()

    private val _selectedQuarter = MutableStateFlow(1) // default 1st quarter
    val selectedQuarter: StateFlow<Int> = _selectedQuarter.asStateFlow()

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    fun setSelectedGrade(grade: Int) {
        _selectedGrade.value = grade
    }

    fun setSelectedQuarter(quarter: Int) {
        _selectedQuarter.value = quarter
    }

    fun getTestsForSubject(subjectId: String, grade: Int, quarter: Int): Flow<List<TestEntity>> {
        return dao.getTestsForSubjectAndQuarter(subjectId, grade, quarter)
    }

    fun startTest(test: TestEntity) {
        viewModelScope.launch {
            val questions = dao.getQuestionsForTest(test.id)
            _quizState.value = QuizState(
                test = test,
                questions = questions,
                currentIndex = 0,
                selectedAnswers = emptyMap(),
                isFinished = false,
                lastResult = null
            )
        }
    }

    fun selectAnswer(questionIndex: Int, optionIndex: Int) {
        _quizState.update { state ->
            val newAnswers = state.selectedAnswers.toMutableMap()
            newAnswers[questionIndex] = optionIndex
            state.copy(selectedAnswers = newAnswers)
        }
    }

    fun nextQuestion() {
        _quizState.update { state ->
            if (state.currentIndex < state.questions.size - 1) {
                state.copy(currentIndex = state.currentIndex + 1)
            } else state
        }
    }

    fun previousQuestion() {
        _quizState.update { state ->
            if (state.currentIndex > 0) {
                state.copy(currentIndex = state.currentIndex - 1)
            } else state
        }
    }

    fun submitTest(subjectName: String) {
        val state = _quizState.value
        val test = state.test ?: return
        val questions = state.questions
        val answers = state.selectedAnswers

        var correctCount = 0
        questions.forEachIndexed { index, question ->
            val selectedOption = answers[index]
            if (selectedOption == question.correctAnswerIndex) {
                correctCount++
            }
        }

        val total = questions.size
        val percentage = if (total > 0) (correctCount.toFloat() / total) * 100f else 0f
        
        // Calculate grade (2 to 5) according to school grading system
        // 86-100% -> 5 (A'lo)
        // 70-85% -> 4 (Yaxshi)
        // 55-69% -> 3 (Qoniqarli)
        // <55% -> 2 (Qoniqarsiz)
        val grade = when {
            percentage >= 86f -> 5
            percentage >= 70f -> 4
            percentage >= 55f -> 3
            else -> 2
        }

        val resultEntity = TestResultEntity(
            testId = test.id,
            testTitle = test.title,
            subjectName = subjectName,
            quarter = test.quarter,
            type = test.type,
            classGrade = test.classGrade,
            score = correctCount,
            totalQuestions = total,
            percentage = percentage,
            grade = grade,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            dao.insertResult(resultEntity)
            _quizState.update {
                it.copy(isFinished = true, lastResult = resultEntity)
            }
        }
    }

    fun resetQuiz() {
        _quizState.value = QuizState()
    }

    fun addCustomQuestion(testId: String, questionText: String, optA: String, optB: String, optC: String, optD: String, correctIdx: Int, explanation: String) {
        viewModelScope.launch {
            val qId = "custom_${System.currentTimeMillis()}"
            val q = QuestionEntity(
                id = qId,
                testId = testId,
                questionText = questionText,
                optionA = optA,
                optionB = optB,
                optionC = optC,
                optionD = optD,
                correctAnswerIndex = correctIdx,
                explanation = explanation
            )
            dao.insertQuestions(listOf(q))
        }
    }
}
