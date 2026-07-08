export const PUBLIC_AUTH_URL_PATHS = [
  '/auth/login',
  '/auth/staff/login',
  '/auth/superadmin/login',
  '/auth/register',
  '/auth/refresh',
  '/auth/forgot-password',
  '/auth/reset-password',
  '/auth/activate-account',
  '/auth/resend-activation',
] as const;

export function isPublicAuthUrl(url: string): boolean {
  return PUBLIC_AUTH_URL_PATHS.some(path => url.includes(path));
}
