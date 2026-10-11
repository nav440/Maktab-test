package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.example.model.SubjectEntity
import com.example.viewmodel.TestViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TestViewModel,
    onSubjectClick: (String, String) -> Unit,
    onStatisticsClick: () -> Unit,
    onTeacherClick: () -> Unit
) {
    val subjects by viewModel.subjects.collectAsState(initial = emptyList())
    val selectedGrade by viewModel.selectedGrade.collectAsState()
    val selectedQuarter by viewModel.selectedQuarter.collectAsState()

    var gradeExpanded by remember { mutableStateOf(false) }
    val grades = listOf(5, 6, 7, 8, 9, 10, 11)
    val quarters = listOf(1, 2, 3, 4)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Maktab Test Tizimi", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("BSB va CHSB onlayn baholash", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = onStatisticsClick, modifier = Modifier.testTag("btn_stats")) {
                        Icon(Icons.Default.BarChart, contentDescription = "Statistika")
                    }
                    IconButton(onClick = onTeacherClick, modifier = Modifier.testTag("btn_teacher")) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Savol qo'shish")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Grade & Quarter Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Grade Dropdown
                ExposedDropdownMenuBox(
                    expanded = gradeExpanded,
                    onExpandedChange = { gradeExpanded = !gradeExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "$selectedGrade - sinf",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sinf") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("dropdown_grade")
                    )
                    ExposedDropdownMenu(
                        expanded = gradeExpanded,
                        onDismissRequest = { gradeExpanded = false }
                    ) {
                        grades.forEach { grade ->
                            DropdownMenuItem(
                                text = { Text("$grade - sinf") },
                                onClick = {
                                    viewModel.setSelectedGrade(grade)
                                    gradeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Quarter Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedQuarter - 1,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                quarters.forEach { quarter ->
                    Tab(
                        selected = selectedQuarter == quarter,
                        onClick = { viewModel.setSelectedQuarter(quarter) },
                        text = { Text("$quarter - Chorak", fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("tab_quarter_$quarter")
                    )
                }
            }

            // Info Banner for BSB & CHSB
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(36.dp)
                    )
                    Column {
                        Text(
                            text = "$selectedQuarter-Chorak BSB va CHSB",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Har bir chorakda 1 ta BSB (Bo'limli) va 1 ta CHSB (Choraklik) imtihonini topshiring.",
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Text(
                text = "Barcha Fanlar ($selectedGrade-sinf)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Subjects Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(subjects) { subject ->
                    SubjectCard(subject = subject) {
                        onSubjectClick(subject.id, subject.name)
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectCard(subject: SubjectEntity, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .testTag("subject_card_${subject.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when (subject.iconName) {
                                "calculate" -> Icons.Default.Calculate
                                "bolt" -> Icons.Default.FlashOn
                                "language" -> Icons.Default.Language
                                "history" -> Icons.Default.History
                                "science" -> Icons.Default.Science
                                "eco" -> Icons.Default.Eco
                                "public" -> Icons.Default.Public
                                "computer" -> Icons.Default.Computer
                                "auto_stories" -> Icons.Default.AutoStories
                                else -> Icons.Default.MenuBook
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(
                        subject.category,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = subject.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
