import keycloak from "../features/auth/keycloak";

const API_BASE = "http://localhost:8085/api/feed";

async function authHeaders() {
  await keycloak.updateToken(30);

  return {
    Authorization: `Bearer ${keycloak.token}`,
    "Content-Type": "application/json",
  };
}

export async function getFeed() {
  const res = await fetch(API_BASE, {
    headers: await authHeaders(),
  });

  if (!res.ok) throw new Error(`Feed failed: ${res.status}`);

  const data = await res.json();
  return data.data ?? [];
}

export async function createPost(content: string) {
  const res = await fetch(`${API_BASE}/posts`, {
    method: "POST",
    headers: await authHeaders(),
    body: JSON.stringify({ content }),
  });

  if (!res.ok) throw new Error(`Create post failed: ${res.status}`);

  const data = await res.json();
  return data.data;
}

export async function likePost(postId: number) {
  const res = await fetch(`${API_BASE}/posts/${postId}/like`, {
    method: "POST",
    headers: await authHeaders(),
  });

  if (!res.ok) throw new Error(`Like failed: ${res.status}`);

  const data = await res.json();
  return data.data;
}

export async function unlikePost(postId: number) {
  const res = await fetch(`${API_BASE}/posts/${postId}/like`, {
    method: "DELETE",
    headers: await authHeaders(),
  });

  if (!res.ok) throw new Error(`Unlike failed: ${res.status}`);

  const data = await res.json();
  return data.data;
}

export async function deletePost(postId: number) {
  const res = await fetch(`${API_BASE}/posts/${postId}`, {
    method: "DELETE",
    headers: await authHeaders(),
  });

  if (!res.ok) throw new Error(`Delete post failed: ${res.status}`);

  const data = await res.json();
  return data.data;
}

export async function getComments(postId: number) {
  const res = await fetch(`${API_BASE}/posts/${postId}/comments`, {
    headers: await authHeaders(),
  });

  if (!res.ok) throw new Error(`Get comments failed: ${res.status}`);

  const data = await res.json();
  return data.data ?? [];
}

export async function addComment(postId: number, content: string) {
  const res = await fetch(`${API_BASE}/posts/${postId}/comments`, {
    method: "POST",
    headers: await authHeaders(),
    body: JSON.stringify({ content }),
  });

  if (!res.ok) throw new Error(`Add comment failed: ${res.status}`);

  const data = await res.json();
  return data.data;
}

export async function deleteComment(commentId: number) {
  const res = await fetch(`${API_BASE}/comments/${commentId}`, {
    method: "DELETE",
    headers: await authHeaders(),
  });

  if (!res.ok) throw new Error(`Delete comment failed: ${res.status}`);

  const data = await res.json();
  return data.data;
}