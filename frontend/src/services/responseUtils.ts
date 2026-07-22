export async function readJsonBody<T>(
  response: Response,
  fallback: T
): Promise<T> {
  if (response.status === 204) {
    return fallback;
  }

  const rawBody = await response.text();
  if (!rawBody.trim()) {
    return fallback;
  }

  return JSON.parse(rawBody) as T;
}
