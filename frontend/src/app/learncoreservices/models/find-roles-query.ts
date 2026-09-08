export interface FindRolesQuery {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  name?: string;
  description?: string;
  roleLevel?: string;
}
