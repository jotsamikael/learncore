export interface FindStudentsQuery {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  firstname?: string;
  lastname?: string;
  email?: string;
  username?: string;
  tenantUuid?: string;
  level?: number;
  xp?: number;
  streakDays?: number;
}
