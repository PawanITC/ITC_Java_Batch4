import keycloak from "../features/auth/keycloak";
import { FeedPost } from "../types/feed";

const API_BASE =
  process.env.REACT_APP_API_BASE_URL ?? "http://localhost:8085";

async function authHeaders() {
  await keycloak.updateToken(30);

  return {
    Authorization: `Bearer ${keycloak.token}`,
    "Content-Type": "application/json",
  };
}

function toFeedPost(item: any): FeedPost {
  return {
    id: Number(item.id ?? item.postId),
    postId: Number(item.postId ?? item.id),
    authorId: item.authorId ?? "",
    authorName: item.authorName ?? "LinkedIn Member",
    authorHeadline: item.authorHeadline ?? "Professional",
    content: item.content ?? "",
    likesCount: item.likesCount ?? 0,
    commentsCount: item.commentsCount ?? 0,
    createdAt: item.createdAt ?? new Date().toISOString(),
  };
}

export async function getTimeline(): Promise<FeedPost[]> {
  const res = await fetch(`${API_BASE}/api/timeline`, {
    headers: await authHeaders(),
  });

  if (!res.ok) {
    throw new Error(`Timeline failed: ${res.status}`);
  }

  const data = await res.json();
  const items = Array.isArray(data) ? data : data.data ?? [];

  return items.map(toFeedPost);
}
