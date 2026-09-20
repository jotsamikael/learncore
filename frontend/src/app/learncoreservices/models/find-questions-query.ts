export type QuestionDifficultyLevel = 'EASY' | 'MEDIUM' | 'HARD' | 'EXPERT';
export type QuestionTypeFilter = 'MCQ' | 'TRUE_OR_FALSE' | 'STRUCTURAL' | 'ESSAY';

export interface FindQuestionsQuery {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  questionText?: string;
  difficultyLevel?: QuestionDifficultyLevel;
  questionType?: QuestionTypeFilter;
  categoryUuid?: string;
}
