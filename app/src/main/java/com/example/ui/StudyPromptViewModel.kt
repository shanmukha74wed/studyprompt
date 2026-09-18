package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiClient
import com.example.ai.ImprovementAnalysis
import com.example.ai.PromptEngine
import com.example.ai.PromptImprover
import com.example.ai.PromptRequest
import com.example.data.AppDatabase
import com.example.data.PreferencesManager
import com.example.data.PromptRepository
import com.example.data.SavedPromptEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudyPromptViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PromptRepository
    private val preferencesManager: PreferencesManager

    init {
        val db = AppDatabase.getInstance(application)
        repository = PromptRepository(db.promptDao())
        preferencesManager = PreferencesManager(application)
    }

    // Navigation Tab
    private val _currentTab = MutableStateFlow("home")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    // UI Feedback Event
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    fun showToast(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(message)
        }
    }

    // Form State
    val subject = MutableStateFlow("")
    val topic = MutableStateFlow("")
    val academicLevel = MutableStateFlow(preferencesManager.defaultAcademicLevel)
    val studyGoal = MutableStateFlow("Learn from basics")
    val currentKnowledge = MutableStateFlow("Beginner")
    val availableTime = MutableStateFlow("1 hour")
    val selectedStyles = MutableStateFlow(setOf(preferencesManager.defaultLearningStyle, "Examples"))
    val examOrUniversity = MutableStateFlow("")
    val additionalInstructions = MutableStateFlow("")
    val language = MutableStateFlow(preferencesManager.defaultLanguage)

    // Form Validation Error
    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    fun clearValidationError() {
        _validationError.value = null
    }

    // Generation State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generatedPrompt = MutableStateFlow<String?>(null)
    val generatedPrompt: StateFlow<String?> = _generatedPrompt.asStateFlow()

    private val _lastRequest = MutableStateFlow<PromptRequest?>(null)
    val lastRequest: StateFlow<PromptRequest?> = _lastRequest.asStateFlow()

    private val _showGeneratedSheet = MutableStateFlow(false)
    val showGeneratedSheet: StateFlow<Boolean> = _showGeneratedSheet.asStateFlow()

    private val _currentSavedPromptId = MutableStateFlow<Long?>(null)
    val currentSavedPromptId: StateFlow<Long?> = _currentSavedPromptId.asStateFlow()

    private val _isCurrentSaved = MutableStateFlow(false)
    val isCurrentSaved: StateFlow<Boolean> = _isCurrentSaved.asStateFlow()

    fun dismissGeneratedSheet() {
        _showGeneratedSheet.value = false
    }

    fun openGeneratedSheet(promptText: String, savedId: Long? = null, isSaved: Boolean = false) {
        _generatedPrompt.value = promptText
        _currentSavedPromptId.value = savedId
        _isCurrentSaved.value = isSaved
        _showGeneratedSheet.value = true
    }

    fun toggleLearningStyle(style: String) {
        val current = selectedStyles.value.toMutableSet()
        if (current.contains(style)) {
            if (current.size > 1) { // Keep at least one
                current.remove(style)
            }
        } else {
            current.add(style)
        }
        selectedStyles.value = current
    }

    // Quick Templates
    fun applyTemplate(templateName: String) {
        when (templateName) {
            "Learn a Topic" -> {
                studyGoal.value = "Learn from basics"
                currentKnowledge.value = "Beginner"
                availableTime.value = "1 hour"
                selectedStyles.value = setOf("Simple explanation", "Step-by-step", "Examples")
            }
            "Exam Preparation" -> {
                studyGoal.value = "Prepare for an exam"
                currentKnowledge.value = "Intermediate"
                availableTime.value = "2 hours"
                selectedStyles.value = setOf("Exam-focused", "Practice questions", "Step-by-step")
            }
            "Understand Difficult Concepts" -> {
                studyGoal.value = "Understand a difficult topic"
                currentKnowledge.value = "Basic knowledge"
                availableTime.value = "1 hour"
                selectedStyles.value = setOf("Analogies", "Visual explanation", "Simple explanation")
            }
            "Quick Revision" -> {
                studyGoal.value = "Revise quickly"
                currentKnowledge.value = "Intermediate"
                availableTime.value = "30 minutes"
                selectedStyles.value = setOf("Memory tricks", "Step-by-step", "Exam-focused")
            }
            "Problem Solving" -> {
                studyGoal.value = "Solve problems"
                currentKnowledge.value = "Basic knowledge"
                availableTime.value = "1 hour"
                selectedStyles.value = setOf("Step-by-step", "Practice questions", "Examples")
            }
            "Make Smart Notes" -> {
                studyGoal.value = "Make notes"
                currentKnowledge.value = "Intermediate"
                availableTime.value = "45 minutes"
                selectedStyles.value = setOf("Step-by-step", "Visual explanation", "Memory tricks")
            }
            "Viva Preparation" -> {
                studyGoal.value = "Prepare for viva"
                currentKnowledge.value = "Intermediate"
                availableTime.value = "30 minutes"
                selectedStyles.value = setOf("Simple explanation", "Examples", "Practice questions")
            }
            "Study Plan" -> {
                studyGoal.value = "Create a study plan"
                currentKnowledge.value = "Beginner"
                availableTime.value = "3+ hours"
                selectedStyles.value = setOf("Step-by-step", "Exam-focused")
            }
            "Practice Questions" -> {
                studyGoal.value = "Practice questions"
                currentKnowledge.value = "Intermediate"
                availableTime.value = "1 hour"
                selectedStyles.value = setOf("Practice questions", "Exam-focused", "Examples")
            }
        }
        showToast("Loaded \"$templateName\" template! 🎯")
    }

    // Generate Prompt
    fun generatePrompt() {
        val s = subject.value.trim()
        val t = topic.value.trim()

        if (s.isBlank()) {
            _validationError.value = "Please enter a subject first."
            return
        }
        if (t.isBlank()) {
            _validationError.value = "Please enter a topic to study."
            return
        }

        _validationError.value = null
        _isGenerating.value = true

        val request = PromptRequest(
            subject = s,
            topic = t,
            academicLevel = academicLevel.value,
            studyGoal = studyGoal.value,
            currentKnowledge = currentKnowledge.value,
            availableTime = availableTime.value,
            learningStyles = selectedStyles.value.toList(),
            examOrUniversity = examOrUniversity.value,
            additionalInstructions = additionalInstructions.value,
            language = language.value
        )
        _lastRequest.value = request

        viewModelScope.launch {
            try {
                // Short, smooth loading animation per spec
                delay(600)

                // High-quality pedagogical structured prompt
                val structuredPrompt = PromptEngine.generatePrompt(request)

                // If Gemini is configured, we can optionally enhance it, otherwise our prompt engine produces the complete prompt
                _generatedPrompt.value = structuredPrompt
                _currentSavedPromptId.value = null
                _isCurrentSaved.value = false
                _showGeneratedSheet.value = true
            } catch (e: Exception) {
                showToast("Something went wrong while generating your prompt. Please try again.")
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun regeneratePrompt() {
        val req = _lastRequest.value
        if (req != null) {
            _isGenerating.value = true
            viewModelScope.launch {
                delay(500)
                val newPrompt = PromptEngine.generatePrompt(req)
                _generatedPrompt.value = newPrompt
                _isGenerating.value = false
                showToast("Prompt regenerated! 🔄")
            }
        } else {
            generatePrompt()
        }
    }

    fun updateGeneratedPromptText(newText: String) {
        _generatedPrompt.value = newText
        val id = _currentSavedPromptId.value
        if (id != null) {
            viewModelScope.launch {
                val existing = repository.getPromptById(id)
                if (existing != null) {
                    repository.updatePrompt(existing.copy(generatedPrompt = newText, updatedAt = System.currentTimeMillis()))
                }
            }
        }
    }

    fun saveCurrentPrompt() {
        val promptText = _generatedPrompt.value ?: return
        val req = _lastRequest.value ?: PromptRequest(
            subject = subject.value.ifBlank { "Study Topic" },
            topic = topic.value.ifBlank { "Key Concept" },
            academicLevel = academicLevel.value,
            studyGoal = studyGoal.value,
            currentKnowledge = currentKnowledge.value,
            availableTime = availableTime.value,
            learningStyles = selectedStyles.value.toList(),
            examOrUniversity = examOrUniversity.value,
            additionalInstructions = additionalInstructions.value,
            language = language.value
        )

        viewModelScope.launch {
            val entity = SavedPromptEntity(
                subject = req.subject,
                topic = req.topic,
                academicLevel = req.academicLevel,
                studyGoal = req.studyGoal,
                currentKnowledge = req.currentKnowledge,
                availableTime = req.availableTime,
                learningStyles = req.learningStyles.joinToString(", "),
                examOrUniversity = req.examOrUniversity,
                additionalInstructions = req.additionalInstructions,
                language = req.language,
                generatedPrompt = promptText,
                isFavorite = false
            )
            val newId = repository.savePrompt(entity)
            _currentSavedPromptId.value = newId
            _isCurrentSaved.value = true
            showToast("Saved to My Prompts! ⭐")
        }
    }

    fun copyPromptToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Study Prompt", text)
        clipboard.setPrimaryClip(clip)
        showToast("Prompt copied! 🚀")
    }

    fun sharePrompt(context: Context, text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share AI Study Prompt")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    // Room Data Observing
    val allSavedPrompts: StateFlow<List<SavedPromptEntity>> = repository.allPrompts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSavedPrompts: StateFlow<List<SavedPromptEntity>> = repository.favoritePrompts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter and Search for My Prompts
    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow("All") // "All", "Favorites", "Recent"

    fun toggleFavorite(id: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, !currentStatus)
            if (!currentStatus) {
                showToast("Added to favorites! ⭐")
            } else {
                showToast("Removed from favorites")
            }
        }
    }

    fun deletePrompt(id: Long) {
        viewModelScope.launch {
            repository.deletePrompt(id)
            showToast("Prompt deleted")
        }
    }

    // Improve My Prompt
    val improveInputText = MutableStateFlow("")
    private val _isAnalyzingImprovement = MutableStateFlow(false)
    val isAnalyzingImprovement: StateFlow<Boolean> = _isAnalyzingImprovement.asStateFlow()

    private val _improvementResult = MutableStateFlow<ImprovementAnalysis?>(null)
    val improvementResult: StateFlow<ImprovementAnalysis?> = _improvementResult.asStateFlow()

    fun analyzeAndImprovePrompt() {
        val raw = improveInputText.value.trim()
        if (raw.isBlank()) {
            showToast("Please enter or paste a prompt first.")
            return
        }

        _isAnalyzingImprovement.value = true
        viewModelScope.launch {
            delay(500)
            val result = PromptImprover.analyzeAndImprove(raw)
            _improvementResult.value = result
            _isAnalyzingImprovement.value = false
            showToast("Prompt analyzed & improved! ✨")
        }
    }

    // Settings
    val themeMode = MutableStateFlow(preferencesManager.themeMode)
    val defaultAcademicLevelPref = MutableStateFlow(preferencesManager.defaultAcademicLevel)
    val defaultLearningStylePref = MutableStateFlow(preferencesManager.defaultLearningStyle)
    val defaultLanguagePref = MutableStateFlow(preferencesManager.defaultLanguage)

    fun setTheme(mode: String) {
        themeMode.value = mode
        preferencesManager.themeMode = mode
    }

    fun setDefaultAcademicLevel(level: String) {
        defaultAcademicLevelPref.value = level
        preferencesManager.defaultAcademicLevel = level
        academicLevel.value = level
        showToast("Default level updated to $level")
    }

    fun setDefaultLearningStyle(style: String) {
        defaultLearningStylePref.value = style
        preferencesManager.defaultLearningStyle = style
        showToast("Default style updated to $style")
    }

    fun setDefaultLanguage(lang: String) {
        defaultLanguagePref.value = lang
        preferencesManager.defaultLanguage = lang
        language.value = lang
        showToast("Default language updated to $lang")
    }

    fun clearAllSavedPrompts() {
        viewModelScope.launch {
            repository.clearAllPrompts()
            showToast("All saved prompts cleared")
        }
    }

    fun exportPromptsText(): String {
        val prompts = allSavedPrompts.value
        if (prompts.isEmpty()) return "No saved prompts."
        return buildString {
            appendLine("=== STUDYPROMPT AI - EXPORTED PROMPTS ===")
            appendLine("Total Prompts: ${prompts.size}")
            appendLine()
            prompts.forEachIndexed { index, p ->
                appendLine("--------------------------------------------------")
                appendLine("PROMPT #${index + 1}: ${p.subject} - ${p.topic}")
                appendLine("Level: ${p.academicLevel} | Goal: ${p.studyGoal} | Time: ${p.availableTime}")
                appendLine("Language: ${p.language}")
                appendLine()
                appendLine(p.generatedPrompt)
                appendLine()
            }
        }
    }
}
