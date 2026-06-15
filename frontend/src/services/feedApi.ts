import keycloak from "../features/auth/keycloak";

const API_BASE = "http://localhost:8085/api/feed";

async function getAuthHeaders() {
  console.log("Authenticated:", keycloak.authenticated);
  console.log("Token:", keycloak.token);
  if (!keycloak.authenticated) {
    throw new Error("User is not authenticated");
  }

  await keycloak.updateToken(30);

  return {
    Authorization: `Bearer ${keycloak.token}`,
    "Content-Type": "application/json",
  };
}

export async function getFeed() {
  const res = await fetch(API_BASE, {
    headers: await getAuthHeaders(),
  });

  if (!res.ok) {
    throw new Error(`Failed to fetch feed: ${res.status}`);
  }

  const data = await res.json();
  return data.data ?? [];
}

export async function createPost(content: string) {
  const res = await fetch(`${API_BASE}/posts`, {
    method: "POST",
    headers: await getAuthHeaders(),
    body: JSON.stringify({ content }),
  });

  if (!res.ok) {
    throw new Error(`Failed to create post: ${res.status}`);
  }

  const data = await res.json();
  return data.data;
}

export async function likePost(postId: number) {
  const res = await fetch(`${API_BASE}/posts/${postId}/like`, {
    method: "POST",
    headers: await getAuthHeaders(),
  });

  if (!res.ok) {
    throw new Error(`Failed to like post: ${res.status}`);
  }

  const data = await res.json();
  return data.data;
}