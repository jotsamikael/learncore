export interface UserSession {
  accessToken: string;
  refreshToken: string;
  accessTokenExpiresAt?: number;
  userUuid: string;
  tenantUuid: string | null;
  tenantSlug: string | null;
  roles: string[];
  permissions: string[];
}
