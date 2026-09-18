package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StudyPromptViewModel
import com.example.ui.components.AppDropdown
import com.example.ui.components.MultiSelectChipGroup
import com.example.ui.components.SingleSelectSegmentedGroup
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryPurple

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: StudyPromptViewModel,
    modifier: Modifier = Modifier
) {
    val subject by viewModel.subject.collectAsState()
    val topic by viewModel.topic.collectAsState()
    val academicLevel by viewModel.academicLevel.collectAsState()
    val studyGoal by viewModel.studyGoal.collectAsState()
    val currentKnowledge by viewModel.currentKnowledge.collectAsState()
    val availableTime by viewModel.availableTime.collectAsState()
    val selectedStyles by viewModel.selectedStyles.collectAsState()
    val examOrUniversity by viewModel.examOrUniversity.collectAsState()
    val additionalInstructions by viewModel.additionalInstructions.collectAsState()
    val language by viewModel.language.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val validationError by viewModel.validationError.collectAsState()

    val academicLevels = listOf("School", "Intermediate", "Diploma", "B.Tech", "Degree", "Competitive Exam", "Other")
    val studyGoals = listOf(
        "Learn from basics",
        "Understand a difficult topic",
        "Prepare for an exam",
        "Revise quickly",
        "Solve problems",
        "Make notes",
        "Practice questions",
        "Prepare for viva",
        "Create a study plan"
    )
    val knowledgeLevels = listOf("Beginner", "Basic knowledge", "Intermediate", "Advanced")
    val timeOptions = listOf("15 minutes", "30 minutes", "1 hour", "2 hours", "3+ hours")
    val learningStyles = listOf(
        "Simple explanation",
        "Step-by-step",
        "Examples",
        "Visual explanation",
        "Analogies",
        "Practice questions",
        "Exam-focused",
        "Memory tricks"
    )
    val languages = listOf("English", "Telugu", "Hindi")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // App Header & Branding
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(PrimaryIndigo, SecondaryPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🤖",
                            fontSize = 20.sp
                        )
                    }
                    Text(
                        text = "StudyPrompt AI",
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 26.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "“Turn your study goals into powerful AI prompts.”",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Main Generator Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_prompt_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title inside card
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Create Your Study Prompt",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Validation Error Banner
                    AnimatedVisibility(
                        visible = validationError != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = validationError ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Field 1: Subject
                    Column {
                        Text(
                            text = "Subject *",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = subject,
                            onValueChange = {
                                viewModel.subject.value = it
                                if (validationError != null) viewModel.clearValidationError()
                            },
                            placeholder = { Text("e.g. Engineering Mathematics, Biology, History") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("subject_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryIndigo,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }

                    // Field 2: Topic
                    Column {
                        Text(
                            text = "Topic *",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = topic,
                            onValueChange = {
                                viewModel.topic.value = it
                                if (validationError != null) viewModel.clearValidationError()
                            },
                            placeholder = { Text("e.g. Differential Equations, Photosynthesis, World War II") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("topic_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryIndigo,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }

                    // Field 3: Academic Level
                    AppDropdown(
                        label = "Academic Level",
                        selectedOption = academicLevel,
                        options = academicLevels,
                        onOptionSelected = { viewModel.academicLevel.value = it },
                        testTag = "academic_level_dropdown"
                    )

                    // Field 4: Study Goal
                    AppDropdown(
                        label = "Study Goal",
                        selectedOption = studyGoal,
                        options = studyGoals,
                        onOptionSelected = { viewModel.studyGoal.value = it },
                        testTag = "study_goal_dropdown"
                    )

                    // Field 5: Current Knowledge
                    AppDropdown(
                        label = "Current Knowledge",
                        selectedOption = currentKnowledge,
                        options = knowledgeLevels,
                        onOptionSelected = { viewModel.currentKnowledge.value = it },
                        testTag = "current_knowledge_dropdown"
                    )

                    // Field 6: Available Study Time
                    Column {
                        Text(
                            text = "Available Study Time",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        SingleSelectSegmentedGroup(
                            options = timeOptions,
                            selectedOption = availableTime,
                            onSelect = { viewModel.availableTime.value = it }
                        )
                    }

                    // Field 7: Learning Style (Multi-select)
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Learning Style",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${selectedStyles.size} selected",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        MultiSelectChipGroup(
                            options = learningStyles,
                            selectedOptions = selectedStyles,
                            onToggle = { viewModel.toggleLearningStyle(it) }
                        )
                    }

                    // Language Selector
                    Column {
                        Text(
                            text = "Prompt Output Language",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        SingleSelectSegmentedGroup(
                            options = languages,
                            selectedOption = language,
                            onSelect = { viewModel.language.value = it }
                        )
                        if (language == "Telugu") {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "💡 AI will explain in simple Telugu with key technical terms in English.",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimaryIndigo
                            )
                        } else if (language == "Hindi") {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "💡 AI will explain in natural conversational Hindi with technical terms in English.",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimaryIndigo
                            )
                        }
                    }

                    // Field 8: Exam/University (Optional)
                    Column {
                        Text(
                            text = "Exam / University (Optional)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = examOrUniversity,
                            onValueChange = { viewModel.examOrUniversity.value = it },
                            placeholder = { Text("e.g. GATE 2025, JNTU Mid-term, CBSE Board, UPSC") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("exam_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryIndigo,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }

                    // Field 9: Additional Instructions (Optional)
                    Column {
                        Text(
                            text = "Additional Instructions (Optional)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = additionalInstructions,
                            onValueChange = { viewModel.additionalInstructions.value = it },
                            placeholder = { Text("“Explain using simple English and focus on important exam questions.”") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(95.dp)
                                .testTag("additional_instructions_input"),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryIndigo,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Large Primary Generate Button
                    Button(
                        onClick = { viewModel.generatePrompt() },
                        enabled = !isGenerating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("generate_prompt_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryIndigo
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Synthesizing Prompt...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "✨ Generate Study Prompt",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Quick Start Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_start_section"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Quick Start",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(text = "🚀", fontSize = 20.sp)
                }
                Text(
                    text = "Tap a preset to instantly configure goal, pace, and learning preferences:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 9 Quick Templates
                val templates = listOf(
                    QuickTemplateItem("📚 Learn a Topic", Icons.Default.Book, "Learn from foundational concepts"),
                    QuickTemplateItem("📝 Exam Preparation", Icons.Default.EditNote, "High-yield formulas & patterns"),
                    QuickTemplateItem("🧠 Understand Difficult Concepts", Icons.Default.Psychology, "Analogies & simple models"),
                    QuickTemplateItem("⚡ Quick Revision", Icons.Default.Bolt, "Rapid recap & memory hooks"),
                    QuickTemplateItem("➗ Problem Solving", Icons.Default.Calculate, "Step-by-step algorithmic breakdowns"),
                    QuickTemplateItem("📖 Make Smart Notes", Icons.Default.School, "Clear bullet-point revision sheet"),
                    QuickTemplateItem("🎤 Viva Preparation", Icons.Default.Mic, "Oral exam simulation & rationale"),
                    QuickTemplateItem("📅 Study Plan", Icons.Default.CalendarToday, "Roadmap with time-blocked goals"),
                    QuickTemplateItem("🔥 Practice Questions", Icons.Default.Whatshot, "Curated tiered problem sets")
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    templates.forEach { template ->
                        val cleanName = template.title.replace(Regex("^[\\p{So}\\p{Sk}\\p{Sm}\\p{Sc}\\p{Punct}\\s]+"), "").trim()
                        Surface(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.applyTemplate(cleanName) }
                                .testTag("template_${cleanName.lowercase().replace(" ", "_")}"),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                            shadowElevation = 1.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = template.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = template.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class QuickTemplateItem(
    val title: String,
    val icon: ImageVector,
    val subtitle: String
)
