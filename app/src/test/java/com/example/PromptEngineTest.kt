package com.example

import com.example.ai.PromptEngine
import com.example.ai.PromptImprover
import com.example.ai.PromptRequest
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptEngineTest {

    @Test
    fun testPromptGenerationStructure() {
        val request = PromptRequest(
            subject = "Engineering Mathematics",
            topic = "Differential Equations",
            academicLevel = "B.Tech",
            studyGoal = "Prepare for an exam",
            currentKnowledge = "Basic knowledge",
            availableTime = "1 hour",
            learningStyles = listOf("Step-by-step", "Examples", "Exam-focused"),
            examOrUniversity = "JNTU Final",
            additionalInstructions = "Focus on 1st order linear equations",
            language = "English"
        )

        val prompt = PromptEngine.generatePrompt(request)

        assertTrue(prompt.contains("ROLE:"))
        assertTrue(prompt.contains("CONTEXT:"))
        assertTrue(prompt.contains("OBJECTIVE:"))
        assertTrue(prompt.contains("LEARNING REQUIREMENTS"))
        assertTrue(prompt.contains("EXAMPLES & DEMONSTRATION"))
        assertTrue(prompt.contains("PRACTICE & PROBLEM SOLVING"))
        assertTrue(prompt.contains("EXAM FOCUS:"))
        assertTrue(prompt.contains("TIME LIMIT & PACING:"))
        assertTrue(prompt.contains("FINAL CHECK:"))
        assertTrue(prompt.contains("Differential Equations"))
        assertTrue(prompt.contains("Engineering Mathematics"))
        assertTrue(prompt.contains("B.Tech"))
    }

    @Test
    fun testTeluguLanguagePromptInstruction() {
        val request = PromptRequest(
            subject = "Physics",
            topic = "Semiconductors",
            academicLevel = "B.Tech",
            studyGoal = "Learn from basics",
            currentKnowledge = "Beginner",
            availableTime = "30 minutes",
            learningStyles = listOf("Simple explanation", "Examples"),
            language = "Telugu"
        )

        val prompt = PromptEngine.generatePrompt(request)
        assertTrue(prompt.contains("Telugu"))
        assertTrue(prompt.contains("English"))
    }

    @Test
    fun testPromptImproverAnalysis() {
        val vaguePrompt = "teach me machine learning"
        val analysis = PromptImprover.analyzeAndImprove(vaguePrompt)

        assertTrue(analysis.issuesFound.isNotEmpty())
        assertTrue(analysis.improvedPrompt.contains("ROLE:"))
        assertTrue(analysis.improvedPrompt.contains("CONTEXT & TARGET:"))
        assertTrue(analysis.improvedPrompt.contains("ACTIVE PRACTICE & SELF-ASSESSMENT:"))
    }
}
