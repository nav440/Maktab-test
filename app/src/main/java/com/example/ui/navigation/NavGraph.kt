package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.viewmodel.TestViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(viewModel: TestViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onSubjectClick = { subjectId, subjectName ->
                    val encodedName = URLEncoder.encode(subjectName, StandardCharsets.UTF_8.toString())
                    navController.navigate("tests/$subjectId/$encodedName")
                },
                onStatisticsClick = {
                    navController.navigate("statistics")
                },
                onTeacherClick = {
                    navController.navigate("teacher")
                }
            )
        }

        composable(
            route = "tests/{subjectId}/{subjectName}",
            arguments = listOf(
                navArgument("subjectId") { type = NavType.StringType },
                navArgument("subjectName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            val encodedName = backStackEntry.arguments?.getString("subjectName") ?: ""
            val subjectName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8.toString())

            SubjectTestsScreen(
                subjectId = subjectId,
                subjectName = subjectName,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onStartTest = { test ->
                    viewModel.startTest(test)
                    val encSubName = URLEncoder.encode(subjectName, StandardCharsets.UTF_8.toString())
                    navController.navigate("quiz/$encSubName")
                }
            )
        }

        composable(
            route = "quiz/{subjectName}",
            arguments = listOf(
                navArgument("subjectName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encSubName = backStackEntry.arguments?.getString("subjectName") ?: ""
            val subjectName = URLDecoder.decode(encSubName, StandardCharsets.UTF_8.toString())

            QuizScreen(
                subjectName = subjectName,
                viewModel = viewModel,
                onFinishTest = {
                    navController.navigate("result") {
                        popUpTo("home")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("result") {
            ResultScreen(
                viewModel = viewModel,
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("statistics") {
            StatisticsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("teacher") {
            TeacherScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
