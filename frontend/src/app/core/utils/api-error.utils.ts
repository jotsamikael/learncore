export function getApiErrorMessage(error: unknown, fallback: string): string {
  const body = (error as { error?: Record<string, string> })?.error;
  if (!body) {
    return fallback;
  }
  return body['error'] ?? body['businessErrorDescription'] ?? body['message'] ?? fallback;
}
