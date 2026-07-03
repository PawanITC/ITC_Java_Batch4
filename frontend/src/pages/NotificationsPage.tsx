import { useEffect } from "react";
import { Bell } from "lucide-react";
import { useAppDispatch, useAppSelector } from "../hooks/reduxHooks";
import {
  loadNotifications,
  readNotification,
} from "../store/notificationSlice";
import { getUsername } from "../utils/authUtils";
import { AppNotification } from "../types/notification";

export default function NotificationsPage() {
  const dispatch = useAppDispatch();
  const { items, loading, error } = useAppSelector(
    (state) => state.notifications
  );

  useEffect(() => {
    const userId = getUsername();
    if (userId) {
      dispatch(loadNotifications(userId));
    }
  }, [dispatch]);

  const handleTap = (notification: AppNotification) => {
    if (!notification.read) {
      dispatch(readNotification(notification.id));
    }
  };

  return (
    <main className="mx-auto max-w-[680px] px-3 py-6">
      <h1 className="mb-4 text-xl font-semibold text-gray-900">
        Notifications
      </h1>

      {loading && items.length === 0 && (
        <p className="text-sm text-gray-500">Loading notifications…</p>
      )}

      {error && (
        <p className="rounded bg-red-50 p-3 text-sm text-red-700">{error}</p>
      )}

      {!loading && !error && items.length === 0 && (
        <div className="flex flex-col items-center gap-2 rounded-lg border border-[#d0d7de] bg-white p-10 text-gray-500">
          <Bell size={28} />
          <p className="text-sm">You're all caught up</p>
        </div>
      )}

      {items.length > 0 && (
        <ul className="overflow-hidden rounded-lg border border-[#d0d7de] bg-white">
          {items.map((n) => (
            <li key={n.id}>
              <button
                onClick={() => handleTap(n)}
                className={`flex w-full items-start gap-3 border-b border-[#e8e8e8] px-4 py-3 text-left hover:bg-[#f3f6f8] ${
                  n.read ? "bg-white" : "bg-[#e9f0f8]"
                }`}
              >
                <span
                  className={`mt-1.5 h-2 w-2 shrink-0 rounded-full ${
                    n.read ? "bg-transparent" : "bg-[#0a66c2]"
                  }`}
                />
                <span className="flex-1">
                  <span className="block text-sm text-gray-900">
                    {n.content}
                  </span>
                  <span className="mt-0.5 block text-xs text-gray-500">
                    {n.type} · {new Date(n.createdAt).toLocaleString()}
                  </span>
                </span>
              </button>
            </li>
          ))}
        </ul>
      )}
    </main>
  );
}