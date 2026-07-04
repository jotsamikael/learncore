export function decodeJwtPayload(token: string): Record<string, unknown> | null {
  try {
    const parts = token.split('.');
    if (parts.length < 2) {
      return null;
    }
    const payload = parts[1].replace(/-/g, '+').replace(/_/g, '/');
    const decoded = decodeURIComponent(
      atob(payload)
        .split('')
        .map(char => `%${(`00${char.charCodeAt(0).toString(16)}`).slice(-2)}`)
        .join('')
    );
    return JSON.parse(decoded);
  } catch {
    return null;
  }
}

export function getJwtExpirationMs(token: string): number | undefined {
  const payload = decodeJwtPayload(token);
  const exp = payload?.['exp'];
  if (typeof exp === 'number') {
    return exp * 1000;
  }
  return undefined;
}
