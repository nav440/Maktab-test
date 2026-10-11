package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.QuestionEntity
import com.example.model.SubjectEntity
import com.example.model.TestEntity
import com.example.model.TestResultEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [SubjectEntity::class, TestEntity::class, QuestionEntity::class, TestResultEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun testDao(): TestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "maktab_test_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.testDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: TestDao) {
            val subjects = listOf(
                SubjectEntity("math", "Matematika", "calculate", "Aniq fanlar"),
                SubjectEntity("uzbek", "Ona tili", "menu_book", "Gumanitar"),
                SubjectEntity("physics", "Fizika", "bolt", "Aniq fanlar"),
                SubjectEntity("english", "Ingliz tili", "language", "Xorijiy tillar"),
                SubjectEntity("history", "O'zbekiston tarixi", "history", "Gumanitar"),
                SubjectEntity("chemistry", "Kimyo", "science", "Tabiiy fanlar"),
                SubjectEntity("biology", "Biologiya", "eco", "Tabiiy fanlar"),
                SubjectEntity("geography", "Geografiya", "public", "Tabiiy fanlar"),
                SubjectEntity("informatics", "Informatika", "computer", "Aniq fanlar"),
                SubjectEntity("literature", "Adabiyot", "auto_stories", "Gumanitar")
            )
            dao.insertSubjects(subjects)

            val grades = listOf(5, 6, 7, 8, 9, 10, 11)
            val quarters = listOf(1, 2, 3, 4)

            val tests = mutableListOf<TestEntity>()
            val questions = mutableListOf<QuestionEntity>()

            var qCounter = 1

            for (grade in grades) {
                for (subject in subjects) {
                    for (q in quarters) {
                        // 1 BSB and 1 CHSB per quarter
                        val types = listOf("BSB", "CHSB")
                        for (type in types) {
                            val testId = "${subject.id}_g${grade}_q${q}_$type"
                            val title = "$q-Chorak $type: ${subject.name} ($grade-sinf)"
                            val duration = if (type == "BSB") 20 else 40
                            val desc = "$grade-sinf o'quvchilari uchun ${subject.name} fanidan $q-choraklik $type (Baholash uchun sinov)."

                            tests.add(
                                TestEntity(
                                    id = testId,
                                    subjectId = subject.id,
                                    title = title,
                                    quarter = q,
                                    type = type,
                                    classGrade = grade,
                                    durationMinutes = duration,
                                    description = desc
                                )
                            )

                            // Add 5 sample questions for BSB, 10 for CHSB
                            val numQuestions = if (type == "BSB") 5 else 10
                            for (i in 1..numQuestions) {
                                val questionId = "q_${testId}_$i"
                                questions.add(
                                    QuestionEntity(
                                        id = questionId,
                                        testId = testId,
                                        questionText = "$grade-sinf ${subject.name} ($q-chorak $type) - $i-savol: ${getSampleQuestionText(subject.id, i)}",
                                        optionA = "A varianti javobi",
                                        optionB = "B varianti javobi (To'g'ri)",
                                        optionC = "C varianti javobi",
                                        optionD = "D varianti javobi",
                                        correctAnswerIndex = 1, // B is correct
                                        explanation = "To'g'ri javob B varianti, chunki bu mavzu bo'yicha ilmiy asos shuni ko'rsatadi."
                                    )
                                )
                                qCounter++
                            }
                        }
                    }
                }
            }

            dao.insertTests(tests)
            dao.insertQuestions(questions)
        }

        private fun getSampleQuestionText(subjectId: String, index: Int): String {
            return when (subjectId) {
                "math" -> "Tenglamani yeching va x ni toping ($index-misol)."
                "uzbek" -> "Quyidagi so'zlardan qaysi biri imlo qoidalariga mos keladi?"
                "physics" -> "Mexanik harakat va tezlik qonuniyatlariga doir masala ($index)."
                "english" -> "Choose the correct grammar form to complete the sentence ($index)."
                "history" -> "Qadimgi davr tarixiy voqealari va sanalari haqida savol ($index)."
                "chemistry" -> "Kimyoviy elementlar va reaksiyalar tenglamasi ($index)."
                "biology" -> "Tirik organizmlar tuzilishi va ekologiya asoslari ($index)."
                "geography" -> "Geografik obyektlar, relyef va iqlim xususiyatlari ($index)."
                "informatics" -> "Algoritmlash va dasturlash asoslari, mantiqiy amallar ($index)."
                else -> "Adabiy asar tahlili va muallif ijodiga oid savol ($index)."
            }
        }
    }
}
