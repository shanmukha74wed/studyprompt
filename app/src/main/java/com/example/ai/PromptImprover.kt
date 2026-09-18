package com.example.ai

data class ImprovementAnalysis(
    val originalPrompt: String,
    val issuesFound: List<String>,
    val improvedPrompt: String
)

object PromptImprover {

    fun analyzeAndImprove(rawPrompt: String): ImprovementAnalysis {
        val prompt = rawPrompt.trim()
        val issues = mutableListOf<String>()

        val lower = prompt.lowercase()

        // 1. Check context
        val hasContext = lower.contains("subject") || lower.contains("grade") || 
                         lower.contains("level") || lower.contains("b.tech") || 
                         lower.contains("school") || lower.contains("college") ||
                         lower.contains("class") || lower.contains("semester")
        if (!hasContext) {
            issues.add("Missing Context: Does not specify your academic level, course, or prerequisites.")
        }

        // 2. Check objective clarity
        val isShort = prompt.split("\\s+".toRegex()).size < 10
        val hasObjective = lower.contains("goal") || lower.contains("understand") || 
                           lower.contains("prepare") || lower.contains("exam") ||
                           lower.contains("solve") || lower.contains("derive")
        if (isShort || !hasObjective) {
            issues.add("Unclear Objective: The goal is too vague (e.g. just asking to 'teach' rather than specifying mastery outcomes).")
        }

        // 3. Difficulty level
        val hasDifficulty = lower.contains("beginner") || lower.contains("intermediate") || 
                            lower.contains("advanced") || lower.contains("basics") ||
                            lower.contains("fundamentals")
        if (!hasDifficulty) {
            issues.add("Lack of Difficulty Level: Doesn't state your starting knowledge or expected depth.")
        }

        // 4. Output format & pedagogy
        val hasFormat = lower.contains("step by step") || lower.contains("bullet") || 
                        lower.contains("analogy") || lower.contains("example") ||
                        lower.contains("table") || lower.contains("simple")
        if (!hasFormat) {
            issues.add("Missing Output Format: Does not specify explanation structure (analogies, derivations, examples).")
        }

        // 5. Practice & assessment
        val hasPractice = lower.contains("practice") || lower.contains("quiz") || 
                          lower.contains("test") || lower.contains("questions") ||
                          lower.contains("problems")
        if (!hasPractice) {
            issues.add("Missing Practice Requirements: Lacks self-assessment questions, test exercises, or active recall.")
        }

        // If very few issues found, add a polish note
        if (issues.isEmpty()) {
            issues.add("Formatting Polish: Can be structured with explicit role-prompting and active recall constraints.")
        }

        // Synthesize an improved, elite prompt
        val improved = buildString {
            appendLine("ROLE:")
            appendLine("Act as an elite academic tutor, senior professor, and master educator.")
            appendLine()
            appendLine("CONTEXT & TARGET:")
            appendLine("- Original query: \"$prompt\"")
            appendLine("- Target: College/University undergraduate level (adapting dynamically if simpler concepts apply)")
            appendLine("- Starting Point: Assume basic familiarity with foundational prerequisites, but explain the core mechanisms cleanly.")
            appendLine()
            appendLine("OBJECTIVE:")
            appendLine("Guide me to thorough conceptual mastery of the topic addressed in my prompt, moving from intuitive mental models to rigorous principles.")
            appendLine()
            appendLine("EXPLANATION STRUCTURE:")
            appendLine("1. High-level intuition & relatable real-world analogy.")
            appendLine("2. Formal theoretical definition, key formulas/axioms, and core principles.")
            appendLine("3. Step-by-step breakdown of how and why it works.")
            appendLine("4. Common student misconceptions, edge cases, and pitfalls to avoid.")
            appendLine("5. 2 fully worked illustrative examples with step-by-step reasoning.")
            appendLine()
            appendLine("ACTIVE PRACTICE & SELF-ASSESSMENT:")
            appendLine("- Provide 3 progressively challenging practice questions (Basic, Intermediate, Exam-level).")
            appendLine("- Provide hints followed by complete solution walkthroughs.")
            appendLine("- Conclude with a 5-question quick self-test quiz to verify my retention.")
        }.trim()

        return ImprovementAnalysis(
            originalPrompt = prompt,
            issuesFound = issues,
            improvedPrompt = improved
        )
    }
}
