export interface FindLanguagesQuery {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  name?: string;
  code?: string;
}
