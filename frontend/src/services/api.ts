import keycloak from "../features/auth/keycloak";

export async function authFetch(url: string, options: RequestInit = {}) {
  await keycloak.updateToken(30);

  return fetch(url, {
    ...options,
    headers: {
      ...(options.headers || {}),
      Authorization: `Bearer ${keycloak.token}`,
      "Content-Type": "application/json",
    },
  });
}