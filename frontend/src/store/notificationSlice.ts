import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { AppNotification } from "../types/notification";
import {
  fetchNotifications,
  markNotificationRead,
} from "../services/notificationApi";

export type NotificationState = {
  items: AppNotification[];
  loading: boolean;
  error: string | null;
};

const initialState: NotificationState = {
  items: [],
  loading: false,
  error: null,
};

export const loadNotifications = createAsyncThunk(
  "notifications/load",
  async (userId: string) => {
    return fetchNotifications(userId);
  }
);

export const readNotification = createAsyncThunk(
  "notifications/markRead",
  async (id: number) => {
    return markNotificationRead(id);
  }
);

const notificationSlice = createSlice({
  name: "notifications",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(loadNotifications.pending, (state) => {
        state.loading = true;
      })
      .addCase(loadNotifications.fulfilled, (state, action) => {
        state.loading = false;
        state.error = null;
        state.items = action.payload
          .slice()
          .sort((a, b) => b.createdAt.localeCompare(a.createdAt));
      })
      .addCase(loadNotifications.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message ?? "Failed to load notifications";
      })
      .addCase(readNotification.fulfilled, (state, action) => {
        const index = state.items.findIndex((n) => n.id === action.payload.id);
        if (index !== -1) {
          state.items[index] = action.payload;
        }
      });
  },
});

export default notificationSlice.reducer;