package com.dodibo.learncore.elearningcore.question.enums;

public enum GradingStrategy {
    /**
     * Exact or almost exact textual answer.
     */
    EXACT_MATCH,

    /**
     * Look for expected keywords / key concepts.
     */
    KEYWORD_MATCH,

    /**
     * Evaluate against a set of criteria.
     */
    RUBRIC,

    /**
     * NLP / embeddings based similarity.
     */
    SEMANTIC_SIMILARITY,

    /**
     * Future GenAI based grading. e.g Qwen3 1.7B
     */
    AI
}
