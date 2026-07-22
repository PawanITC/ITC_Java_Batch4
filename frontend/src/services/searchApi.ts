import keycloak from "../features/auth/keycloak";
import { SearchType } from "../types/search";
import { apiBaseUrl } from "../config/runtimeConfig";
import { readJsonBody } from "./responseUtils";

const API_BASE = `${apiBaseUrl}/api`;

async function authHeaders() {
  if (!keycloak.authenticated || !keycloak.token) {
    throw new Error("User is not authenticated. Token is missing.");
  }

  await keycloak.updateToken(30);

  return {
    Authorization: `Bearer ${keycloak.token}`,
  };
}

export async function searchByType(type: SearchType, query: string) {
  const response = await fetch(
    `${API_BASE}/search/${type}?q=${encodeURIComponent(query)}`,
    {
      headers: await authHeaders(),
    }
  );

  if (!response.ok) {
    throw new Error(`Search failed: ${response.status}`);
  }

  const data = await readJsonBody<any>(response, { data: [] });
  return data.data ?? [];
}

export async function getDiscoverySuggestions() {
  const response = await fetch(`${API_BASE}/discovery/suggestions`, {
    headers: await authHeaders(),
  });

  if (!response.ok) {
    throw new Error(`Suggestions failed: ${response.status}`);
  }

  const data = await readJsonBody<any>(response, { data: [] });
  return data.data ?? [];
}

export async function getTrendingTopics() {
  const response = await fetch(`${API_BASE}/discovery/trending/topics`, {
    headers: await authHeaders(),
  });

  if (!response.ok) {
    throw new Error(`Trending failed: ${response.status}`);
  }

  const data = await readJsonBody<any>(response, { data: [] });
  return data.data ?? [];
}
