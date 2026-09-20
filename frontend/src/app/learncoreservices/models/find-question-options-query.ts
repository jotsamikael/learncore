export interface FindQuestionOptionsQuery {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  questionUuid?: string;
}
