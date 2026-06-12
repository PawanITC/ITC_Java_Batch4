import keycloak from "../features/auth/keycloak";

const API_BASE = "http://localhost:8085/api/feed";

export async function getFeed() {
  const res = await fetch(API_BASE, {
    headers: {
      Authorization: `Bearer ${keycloak.token}`,
    },
  });

  if (!res.ok) {
    throw new Error("Failed to fetch feed");
  }

  const data = await res.json();
  return data.data;
}

export async function createPost(content: string) {
  const res = await fetch(`${API_BASE}/posts`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${keycloak.token}`,
    },
    body: JSON.stringify({ content }),
  });

  if (!res.ok) {
    throw new Error("Failed to create post");
  }

  const data = await res.json();
  return data.data;
}

export async function likePost(postId: number) {
  const res = await fetch(`${API_BASE}/posts/${postId}/like`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${keycloak.token}`,
    },
  });

  if (!res.ok) {
    throw new Error("Failed to like post");
  }

  const data = await res.json();
  return data.data;
}