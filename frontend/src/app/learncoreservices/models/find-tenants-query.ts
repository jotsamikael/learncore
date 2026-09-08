export interface FindTenantsQuery {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  name?: string;
  phone?: string;
  examFocus?: string;
  country?: string;
  description?: string;
}
