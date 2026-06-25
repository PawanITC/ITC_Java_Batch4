import keycloak from "../features/auth/keycloak";
import { apiBaseUrl } from "../config/runtimeConfig";
import { CreatePostResponse } from "../types/feed";

const API_BASE = apiBaseUrl;

async function authHeaders() {
  await keycloak.updateToken(30);

  return {
    Authorization: `Bearer ${keycloak.token}`,
    "Content-Type": "application/json",
  };
}

function toPost(item: any): CreatePostResponse {
  return {
    id: Number(item.id ?? item.postId),
    postId: Number(item.postId ?? item.id),
    authorId: item.authorId ?? "",
    authorName: item.authorName ?? "LinkedIn Member",
    authorHeadline: item.authorHeadline ?? "Professional",
    authorAvatarUrl: item.authorAvatarUrl ?? undefined,
    content: item.content ?? "",
    likesCount: item.likesCount ?? 0,
    commentsCount: item.commentsCount ?? 0,
    createdAt: item.createdAt ?? new Date().toISOString(),
  };
}

export async function createPost(content: string): Promise<CreatePostResponse> {
  const res = await fetch(`${API_BASE}/api/posts`, {
    method: "POST",
    headers: await authHeaders(),
    body: JSON.stringify({ content }),
  });

  if (!res.ok) throw new Error(`Create post failed: ${res.status}`);

  const data = await res.json();
  return toPost(data.data ?? data);
}

export async function likePost(postId: number) {
  const res = await fetch(`${API_BASE}/api/posts/${postId}/like`, {
    method: "POST",
    headers: await authHeaders(),
  });

  if (!res.ok) throw new Error(`Like failed: ${res.status}`);

  const data = await res.json();
  return toPost(data.data ?? data);
}

export async function unlikePost(postId: number) {
  const res = await fetch(`${API_BASE}/api/posts/${postId}/like`, {
    method: "DELETE",
    headers: await authHeaders(),
  });

  if (!res.ok) throw new Error(`Unlike failed: ${res.status}`);

  const data = await res.json();
  return toPost(data.data ?? data);
}

export async function deletePost(postId: number) {
  const res = await fetch(`${API_BASE}/api/posts/${postId}`, {
    method: "DELETE",
    headers: await authHeaders(),
  });

  if (!res.ok) throw new Error(`Delete post failed: ${res.status}`);

  return true;
}

export async function addComment(postId: number, content: string) {
  const res = await fetch(`${API_BASE}/api/posts/${postId}/comments`, {
    method: "POST",
    headers: await authHeaders(),
    body: JSON.stringify({ content }),
  });

  if (!res.ok) throw new Error(`Add comment failed: ${res.status}`);

  const data = await res.json();
  return toPost(data.data ?? data);
}
