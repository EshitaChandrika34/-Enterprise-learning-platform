package com.enterpriselearning.ai;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CareerAiService {

    public CareerAiResponse generateCareerAdvice(CareerAiRequest request) {

        String message = request.getMessage() == null
                ? ""
                : request.getMessage().trim();

        String targetRole = request.getTargetRole() == null
                ? "your target role"
                : request.getTargetRole().trim();

        List<String> skills = request.getSkills() == null
                ? new ArrayList<>()
                : request.getSkills();

        List<String> missingSkills = request.getMissingSkills() == null
                ? new ArrayList<>()
                : request.getMissingSkills();

        String text = message.toLowerCase(Locale.ROOT);

        // =========================================================
        // THANK YOU / GOODBYE
        // =========================================================

        if (containsAny(text,
                "thank you",
                "thankyou",
                "thanks",
                "thank")) {

            return new CareerAiResponse(
                    "You're welcome! 😊 Keep working consistently toward your "
                    + targetRole
                    + " goal. If you need help with skills, interview preparation, "
                    + "your resume, certifications, or job preparation, just ask me."
            );
        }

        if (containsAny(text,
                "bye",
                "goodbye",
                "see you")) {

            return new CareerAiResponse(
                    "Goodbye! Keep learning and making progress toward your "
                    + targetRole
                    + " career goal. All the best! 🚀"
            );
        }

        // =========================================================
        // GREETING
        // =========================================================

        if (containsAny(text,
                "hello",
                "hi",
                "hey",
                "good morning",
                "good afternoon",
                "good evening")) {

            return new CareerAiResponse(
                    "Hello! 👋 I'm your Career Assistant. "
                    + "I can help you with your "
                    + targetRole
                    + " career journey. "
                    + "You can ask me about skills, learning plans, interviews, "
                    + "resumes, certifications, or job preparation."
            );
        }

        // =========================================================
        // WHAT SHOULD I LEARN NEXT?
        // =========================================================

        if (containsAny(text,
                "what should i learn",
                "what should i learn next",
                "learn next",
                "what to learn",
                "which skill should i learn",
                "next skill")) {

            if (!missingSkills.isEmpty()) {

                return new CareerAiResponse(
                        "For your "
                        + targetRole
                        + " goal, I recommend focusing on these missing skills first:\n\n"
                        + formatList(missingSkills)
                        + "\n\nStart with one skill at a time. "
                        + "Build a small practical project while learning it, "
                        + "then move to the next skill."
                );
            }

            return new CareerAiResponse(
                    "For your "
                    + targetRole
                    + " goal, continue strengthening your core technical skills. "
                    + "Focus on advanced concepts, practical projects, "
                    + "problem solving, and interview preparation."
            );
        }

        // =========================================================
        // MISSING SKILLS
        // =========================================================

        if (containsAny(text,
                "missing skills",
                "skills am i missing",
                "skills i am missing",
                "what skills do i need",
                "skill gap",
                "skill gaps")) {

            if (!missingSkills.isEmpty()) {

                return new CareerAiResponse(
                        "Based on your current career profile, these are the "
                        + "skills you should focus on:\n\n"
                        + formatList(missingSkills)
                        + "\n\nImproving these skills will help increase your "
                        + "readiness for "
                        + targetRole
                        + " positions."
                );
            }

            return new CareerAiResponse(
                    "Your current profile does not show any major missing skills. "
                    + "Continue improving your existing skills through projects, "
                    + "certifications, and practical experience."
            );
        }

        // =========================================================
        // CURRENT SKILLS
        // =========================================================

        if (containsAny(text,
                "my skills",
                "current skills",
                "what skills do i have",
                "show my skills")) {

            if (!skills.isEmpty()) {

                return new CareerAiResponse(
                        "Your current skills are:\n\n"
                        + formatList(skills)
                        + "\n\nKeep strengthening these skills through "
                        + "real projects and practical experience."
                );
            }

            return new CareerAiResponse(
                    "I don't currently have your selected skills available. "
                    + "Please add your skills from the Skills page so I can "
                    + "give you more personalized career advice."
            );
        }

        // =========================================================
        // INTERVIEW PREPARATION
        // =========================================================

        if (containsAny(text,
                "interview",
                "interviews",
                "interview preparation",
                "prepare for interview",
                "how should i prepare")) {

            return new CareerAiResponse(
                    "For a "
                    + targetRole
                    + " interview, prepare in these areas:\n\n"
                    + "1. Core programming concepts\n"
                    + "2. Data Structures and Algorithms\n"
                    + "3. SQL and database concepts\n"
                    + "4. Spring Boot and REST APIs\n"
                    + "5. System Design fundamentals\n"
                    + "6. Projects from your resume\n"
                    + "7. Common HR and behavioral questions\n\n"
                    + "Also practice explaining your projects clearly in 2–3 minutes."
            );
        }

        // =========================================================
        // RESUME
        // =========================================================

        if (containsAny(text,
                "resume",
                "cv",
                "improve my resume",
                "resume improvement",
                "resume tips")) {

            return new CareerAiResponse(
                    "To improve your resume for "
                    + targetRole
                    + " roles:\n\n"
                    + "• Highlight your strongest technical skills.\n"
                    + "• Add measurable project achievements.\n"
                    + "• Mention technologies used in each project.\n"
                    + "• Keep project descriptions short and clear.\n"
                    + "• Add relevant internship or practical experience.\n"
                    + "• Include certifications that support your target role.\n"
                    + "• Keep the resume focused on the job you are applying for."
            );
        }

        // =========================================================
        // CERTIFICATIONS
        // =========================================================

        if (containsAny(text,
                "certification",
                "certifications",
                "certificate",
                "which certification",
                "certification should i take")) {

            return new CareerAiResponse(
                    "For a "
                    + targetRole
                    + " career path, choose certifications that strengthen "
                    + "your actual technical skills.\n\n"
                    + "Good areas to consider include:\n"
                    + "• Java and Spring Boot\n"
                    + "• SQL and database technologies\n"
                    + "• Cloud fundamentals\n"
                    + "• Docker and containerization\n"
                    + "• Kubernetes fundamentals\n"
                    + "• System Design\n\n"
                    + "Prioritize practical projects along with certifications."
            );
        }

        // =========================================================
        // JOB PREPARATION
        // =========================================================

        if (containsAny(text,
                "job",
                "job ready",
                "job readiness",
                "job preparation",
                "career readiness",
                "am i ready")) {

            return new CareerAiResponse(
                    "To become job-ready for "
                    + targetRole
                    + ", focus on four areas:\n\n"
                    + "1. Technical skills\n"
                    + "2. Practical projects\n"
                    + "3. Interview preparation\n"
                    + "4. Resume and job applications\n\n"
                    + (missingSkills.isEmpty()
                        ? "Continue strengthening your existing skills and build "
                          + "projects that demonstrate them."
                        : "Your current priority should be improving these skills:\n"
                          + formatList(missingSkills))
            );
        }

        // =========================================================
        // JAVA / SPRING BOOT
        // =========================================================

        if (containsAny(text,
                "java",
                "spring",
                "spring boot",
                "backend",
                "backend development")) {

            return new CareerAiResponse(
                    "For your "
                    + targetRole
                    + " journey, strengthen your backend foundation:\n\n"
                    + "• Core Java and OOP\n"
                    + "• Collections and exception handling\n"
                    + "• Java Streams and modern Java features\n"
                    + "• Spring Boot\n"
                    + "• REST APIs\n"
                    + "• JPA and Hibernate\n"
                    + "• SQL and database design\n"
                    + "• Authentication and authorization\n"
                    + "• Docker and deployment\n"
                    + "• System Design\n\n"
                    + "Build projects that combine several of these skills."
            );
        }

        // =========================================================
        // SQL / DATABASE
        // =========================================================

        if (containsAny(text,
                "sql",
                "database",
                "mysql",
                "postgresql",
                "database design")) {

            return new CareerAiResponse(
                    "For backend development, strengthen your database knowledge:\n\n"
                    + "• SELECT, INSERT, UPDATE and DELETE\n"
                    + "• Joins\n"
                    + "• GROUP BY and aggregate functions\n"
                    + "• Subqueries\n"
                    + "• Indexes\n"
                    + "• Normalization\n"
                    + "• Transactions\n"
                    + "• Query optimization\n"
                    + "• Database design\n\n"
                    + "Practice by designing databases for real applications."
            );
        }

        // =========================================================
        // GENERAL CAREER QUESTION
        // =========================================================

        if (containsAny(text,
                "career",
                "career advice",
                "career plan",
                "career path",
                "roadmap",
                "future")) {

            return new CareerAiResponse(
                    "Your target role is "
                    + targetRole
                    + ". A good career plan is:\n\n"
                    + "1. Identify missing skills.\n"
                    + "2. Learn one skill at a time.\n"
                    + "3. Build practical projects.\n"
                    + "4. Complete relevant learning or certifications.\n"
                    + "5. Practice technical interviews.\n"
                    + "6. Improve your resume.\n"
                    + "7. Apply for suitable internal and external opportunities.\n\n"
                    + "Consistent progress is more important than trying to learn "
                    + "everything at once."
            );
        }

        // =========================================================
        // DEFAULT RESPONSE
        // =========================================================

        return new CareerAiResponse(
                "I can help you with your "
                + targetRole
                + " career journey.\n\n"
                + "Try asking me:\n"
                + "• What should I learn next?\n"
                + "• What skills am I missing?\n"
                + "• How should I prepare for interviews?\n"
                + "• How can I improve my resume?\n"
                + "• Which certifications should I take?\n"
                + "• How can I become job-ready?"
        );
    }

    // =============================================================
    // CHECK KEYWORDS
    // =============================================================

    private boolean containsAny(
            String text,
            String... keywords) {

        for (String keyword : keywords) {

            if (text.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }

        return false;
    }

    // =============================================================
    // FORMAT LIST
    // =============================================================

    private String formatList(List<String> values) {

        StringBuilder result = new StringBuilder();

        for (String value : values) {

            if (value == null || value.trim().isEmpty()) {
                continue;
            }

            result.append("• ")
                    .append(value.trim())
                    .append("\n");
        }

        return result.toString().trim();
    }
}