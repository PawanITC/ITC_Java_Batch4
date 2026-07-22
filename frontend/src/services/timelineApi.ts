import keycloak from "../features/auth/keycloak";
import { apiBaseUrl } from "../config/runtimeConfig";
import { FeedPost } from "../types/feed";
import { readJsonBody } from "./responseUtils";

const API_BASE = apiBaseUrl;

export type TimelineSortMode = "top" | "recent";

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

export async function getTimeline(
  sort: TimelineSortMode = "top"
): Promise<FeedPost[]> {
  const res = await fetch(`${API_BASE}/api/timeline?sort=${sort}`, {
    headers: await authHeaders(),
  });

  if (!res.ok) {
    throw new Error(`Timeline failed: ${res.status}`);
  }

  const data = await readJsonBody<any>(res, []);
  const items = Array.isArray(data) ? data : data.data ?? [];

  return items.map(toFeedPost);
}
