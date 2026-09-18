package com.example.ai

data class PromptRequest(
    val subject: String,
    val topic: String,
    val academicLevel: String,
    val studyGoal: String,
    val currentKnowledge: String,
    val availableTime: String,
    val learningStyles: List<String>,
    val examOrUniversity: String = "",
    val additionalInstructions: String = "",
    val language: String = "English"
)

object PromptEngine {

    fun generatePrompt(request: PromptRequest): String {
        val subject = request.subject.trim()
        val topic = request.topic.trim()
        val academicLevel = request.academicLevel.trim()
        val studyGoal = request.studyGoal.trim()
        val currentKnowledge = request.currentKnowledge.trim()
        val availableTime = request.availableTime.trim()
        val learningStyles = request.learningStyles
        val exam = request.examOrUniversity.trim()
        val additional = request.additionalInstructions.trim()
        val language = request.language.trim()

        // 1. Determine Role
        val roleDesc = when (academicLevel) {
            "School" -> "an expert, patient secondary school educator and mentor specializing in $subject"
            "Intermediate" -> "an experienced pre-university tutor and academic mentor in $subject"
            "Diploma" -> "a practical technical instructor and applied $subject specialist"
            "B.Tech" -> "a distinguished engineering professor and industry-experienced tutor in $subject"
            "Degree" -> "a senior university lecturer and subject matter authority in $subject"
            "Competitive Exam" -> "a top-tier competitive exam mentor, rank-booster coach, and $subject expert"
            else -> "an expert academic instructor, author, and patient tutor in $subject"
        }

        // 2. Learning Requirements based on Goal
        val goalStrategy = when (studyGoal) {
            "Learn from basics" -> 
                "Start with foundational intuition and real-world relevance before introducing formal definitions, formulas, and advanced concepts. Build knowledge incrementally without skipping intermediate steps."
            "Understand a difficult topic" -> 
                "Break down the hardest nuances into intuitive chunks. Pinpoint why students typically get stuck, resolve common misconceptions, and use multiple complementary perspectives."
            "Prepare for an exam" -> 
                "Prioritize syllabus-critical topics, derivations, frequently tested principles, scoring patterns, and high-yield problem archetypes. Highlight common student pitfalls and exam presentation tips."
            "Revise quickly" -> 
                "Deliver a high-density, crystal-clear revision summary: core principles, master formula sheet/rules, summary bullet points, and quick sanity checks."
            "Solve problems" -> 
                "Walk through problem-solving algorithms systematically. Show how to analyze the problem statement, choose the right formula/method, avoid calculation traps, and verify answers."
            "Make notes" -> 
                "Format the response as clean, structured study notes with executive summaries, bullet points, formula tables, diagrams/ASCII flowcharts, and key takeaway boxes."
            "Practice questions" -> 
                "Provide a curated progression of practice problems categorized by difficulty (Easy, Medium, Challenging), each accompanied by step-by-step solutions and reasoning."
            "Prepare for viva" -> 
                "Simulate a university viva examination: ask typical examiner questions, explain conceptual rationale, provide crisp model answers, and detail the 'why' behind key mechanisms."
            "Create a study plan" -> 
                "Structure a structured time-blocked roadmap prioritizing prerequisites, core milestones, active recall checkpoints, and revision cycles."
            else -> 
                "Deliver a comprehensive, well-structured academic breakdown tailored to the objective."
        }

        // 3. Explanation Style from selections
        val styleClauses = mutableListOf<String>()
        if (learningStyles.contains("Simple explanation")) {
            styleClauses.add("Use clear, jargon-free explanations with simple terminology wherever possible")
        }
        if (learningStyles.contains("Step-by-step")) {
            styleClauses.add("Break down all concepts, mathematical derivations, and workflows step-by-step")
        }
        if (learningStyles.contains("Examples")) {
            styleClauses.add("Provide concrete, relevant real-world and textbook examples for each major concept")
        }
        if (learningStyles.contains("Visual explanation")) {
            styleClauses.add("Include ASCII mental diagrams, conceptual flowcharts, or spatial analogies to aid visual intuition")
        }
        if (learningStyles.contains("Analogies")) {
            styleClauses.add("Use relatable everyday analogies to anchor abstract principles")
        }
        if (learningStyles.contains("Practice questions")) {
            styleClauses.add("Embed interactive check-for-understanding questions directly within the content")
        }
        if (learningStyles.contains("Exam-focused")) {
            styleClauses.add("Emphasize scoring keywords, expected mark distributions, and examiner expectation criteria")
        }
        if (learningStyles.contains("Memory tricks")) {
            styleClauses.add("Include memorable mnemonics, acronyms, or cognitive hooks to effortlessly memorize key formulas and sequences")
        }
        if (styleClauses.isEmpty()) {
            styleClauses.add("Provide clear, structured, and pedagogical explanations")
        }

        // 4. Language adaptation
        val languageInstruction = when (language) {
            "Telugu" -> "LANGUAGE REQUIREMENT: Explain concepts in simple, natural Telugu with essential technical terminology, mathematical terms, and equations written in English (Bilingual Telugu + English mix ideal for $academicLevel students)."
            "Hindi" -> "LANGUAGE REQUIREMENT: Explain concepts in conversational Hindi/Hinglish with technical keywords, terms, and formulas kept in standard English (Student-friendly Hindi + English mix suitable for $academicLevel students)."
            else -> "LANGUAGE REQUIREMENT: Respond in clear, precise, and articulate English."
        }

        // 5. Time adaptation
        val timeStrategy = when (availableTime) {
            "15 minutes" -> "I only have 15 minutes available. Provide an ultra-concise executive summary, the top 3 critical concepts, essential formulas, and a 3-question rapid quiz."
            "30 minutes" -> "I have 30 minutes available. Deliver a targeted breakdown focusing on the core 80/20 essentials, two worked examples, and a 5-question review."
            "1 hour" -> "I have 1 hour available. Balance depth with efficiency: foundational intuition, formal principles, 2-3 solved examples, and a 5-10 question assessment."
            "2 hours" -> "I have 2 hours available. Go in-depth: comprehensive theory, complete derivations, multiple edge cases, worked problems, and an extensive practice set."
            "3+ hours" -> "I have 3+ hours available. Treat this as an exhaustive deep-dive masterclass: cover prerequisites, complete theoretical foundations, advanced applications, multiple problem tiers, and a comprehensive final test."
            else -> "I have $availableTime available for this study session. Pace and prioritize the content accordingly."
        }

        // 6. Exam / University clause
        val examClause = if (exam.isNotBlank()) {
            "\nEXAM/INSTITUTION TARGET:\n- Tailor content and question patterns specifically for: $exam."
        } else ""

        // 7. Additional notes clause
        val additionalClause = if (additional.isNotBlank()) {
            "\nSPECIAL STUDENT INSTRUCTIONS:\n- $additional"
        } else ""

        // Build the structured prompt
        return buildString {
            appendLine("ROLE:")
            appendLine("Act as $roleDesc.")
            appendLine()
            appendLine("CONTEXT:")
            appendLine("- Subject: $subject")
            appendLine("- Topic: $topic")
            appendLine("- Academic Level: $academicLevel")
            appendLine("- Current Knowledge: $currentKnowledge")
            appendLine()
            appendLine("OBJECTIVE:")
            appendLine("My primary goal is: $studyGoal on \"$topic\". $goalStrategy")
            appendLine()
            appendLine("LEARNING REQUIREMENTS & METHODOLOGY:")
            styleClauses.forEach { clause ->
                appendLine("- $clause.")
            }
            appendLine()
            appendLine("EXAMPLES & DEMONSTRATION:")
            appendLine("- Provide clear, fully worked illustrative examples demonstrating the concepts in action.")
            appendLine("- Highlight typical misconceptions, edge cases, and common student mistakes.")
            appendLine()
            appendLine("PRACTICE & PROBLEM SOLVING:")
            appendLine("- Include targeted practice problems suitable for $academicLevel level.")
            appendLine("- Provide hints followed by complete step-by-step solution keys.")
            if (studyGoal == "Prepare for an exam" || learningStyles.contains("Exam-focused") || exam.isNotBlank()) {
                appendLine()
                appendLine("EXAM FOCUS:")
                appendLine("- Emphasize important concepts, frequently tested areas, formulas, problem-solving methods, and likely question patterns without claiming certainty about future exam questions.")
            }
            appendLine()
            appendLine("TIME LIMIT & PACING:")
            appendLine("- $timeStrategy")
            appendLine()
            appendLine(languageInstruction)
            if (examClause.isNotBlank()) {
                append(examClause)
                appendLine()
            }
            if (additionalClause.isNotBlank()) {
                append(additionalClause)
                appendLine()
            }
            appendLine()
            appendLine("FINAL CHECK:")
            appendLine("Conclude with a short, interactive quiz or self-assessment checklist so I can verify my mastery before moving forward.")
        }.trim()
    }
}
