package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val category: String
)

@Entity(tableName = "tests")
data class TestEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val title: String,
    val quarter: Int, // 1, 2, 3, 4
    val type: String, // "BSB" or "CHSB"
    val classGrade: Int, // 5 to 11
    val durationMinutes: Int,
    val description: String
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val testId: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswerIndex: Int, // 0, 1, 2, 3
    val explanation: String
)

@Entity(tableName = "test_results")
data class TestResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testId: String,
    val testTitle: String,
    val subjectName: String,
    val quarter: Int,
    val type: String,
    val classGrade: Int,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Float,
    val grade: Int, // 2, 3, 4, 5
    val timestamp: Long
)
