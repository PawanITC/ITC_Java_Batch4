import { authFetch } from "./api";
import { apiBaseUrl } from "../config/runtimeConfig";
import { AppNotification } from "../types/notification";

export async function fetchNotifications(
  userId: string
): Promise<AppNotification[]> {
  const res = await authFetch(
    `${apiBaseUrl}/api/notifications?userId=${encodeURIComponent(userId)}`
  );
  if (!res.ok) throw new Error(`Failed to load notifications: ${res.status}`);
  return res.json();
}

export async function markNotificationRead(
  id: number
): Promise<AppNotification> {
  const res = await authFetch(`${apiBaseUrl}/api/notifications/${id}/read`, {
    method: "PUT",
  });
  if (!res.ok) throw new Error(`Failed to mark as read: ${res.status}`);
  return res.json();
}