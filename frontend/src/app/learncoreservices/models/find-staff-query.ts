export interface FindStaffQuery {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  firstname?: string;
  lastname?: string;
  email?: string;
  positionName?: string;
}
