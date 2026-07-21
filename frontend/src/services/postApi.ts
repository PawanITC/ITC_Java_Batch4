import keycloak from "../features/auth/keycloak";
import { apiBaseUrl } from "../config/runtimeConfig";
import { CreatePostResponse, FeedComment, MediaUploadResponse } from "../types/feed";

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
    mediaUrl: item.mediaUrl ?? undefined,
    mediaObjectKey: item.mediaObjectKey ?? undefined,
    mediaType: item.mediaType ?? undefined,
    likesCount: item.likesCount ?? 0,
    commentsCount: item.commentsCount ?? 0,
    createdAt: item.createdAt ?? new Date().toISOString(),
  };
}

function toComment(item: any): FeedComment {
  return {
    id: Number(item.id),
    postId: Number(item.postId),
    authorId: item.authorId ?? "",
    authorName: item.authorName ?? "LinkedIn Member",
    content: item.content ?? "",
    createdAt: item.createdAt ?? new Date().toISOString(),
  };
}

export async function createPost(
  content: string,
  media?: { mediaObjectKey?: string; objectKey?: string; mediaType: "IMAGE" | "VIDEO" }
): Promise<CreatePostResponse> {
  const res = await fetch(`${API_BASE}/api/posts`, {
    method: "POST",
    headers: await authHeaders(),
    body: JSON.stringify({
      content,
      mediaObjectKey: media?.mediaObjectKey ?? media?.objectKey,
      mediaType: media?.mediaType,
    }),
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

export async function uploadPostMedia(file: File): Promise<MediaUploadResponse> {
  await keycloak.updateToken(30);

  const formData = new FormData();
  formData.append("file", file);

  const res = await fetch(`${API_BASE}/api/posts/media`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${keycloak.token}`,
    },
    body: formData,
  });

  if (!res.ok) throw new Error(`Media upload failed: ${res.status}`);

  const data = await res.json();
  return {
    ...data,
    mediaObjectKey: data.mediaObjectKey ?? data.objectKey,
  };
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

export async function getComments(postId: number): Promise<FeedComment[]> {
  const res = await fetch(`${API_BASE}/api/posts/${postId}/comments`, {
    headers: await authHeaders(),
  });

  if (!res.ok) throw new Error(`Get comments failed: ${res.status}`);

  const data = await res.json();
  const items = Array.isArray(data) ? data : data.data ?? [];
  return items.map(toComment);
}
