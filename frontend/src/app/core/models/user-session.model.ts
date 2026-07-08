export interface UserSession {
  accessToken: string;
  refreshToken: string;
  accessTokenExpiresAt?: number;
  firstname: string;
  lastname: string;
  email: string;
  userUuid: string;
  tenantUuid: string | null;
  tenantSlug: string | null;
  roles: string[];
  permissions: string[];
}
