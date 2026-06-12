import { createSlice, PayloadAction } from "@reduxjs/toolkit";

type User = {
  id?: string;
  name: string;
  email?: string;
  roles: string[];
};

export type AuthState = {
  isLoggedIn: boolean;
  loading: boolean;
  token: string | null;
  accessToken: string | null;
  username: string | null;
  roles: string[];
  user: User | null;
};

const initialState: AuthState = {
  isLoggedIn: false,
  loading: true,
  token: null,
  accessToken: null,
  username: null,
  roles: [],
  user: null,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    loginSuccess: (
      state,
      action: PayloadAction<{
        token: string;
        username: string;
        roles: string[];
      }>
    ) => {
      state.isLoggedIn = true;
      state.loading = false;
      state.token = action.payload.token;
      state.accessToken = action.payload.token;
      state.username = action.payload.username;
      state.roles = action.payload.roles;
      state.user = {
        name: action.payload.username,
        roles: action.payload.roles,
      };
    },

    login: (
      state,
      action: PayloadAction<{
        user: User;
        accessToken: string;
      }>
    ) => {
      state.isLoggedIn = true;
      state.loading = false;
      state.user = action.payload.user;
      state.username = action.payload.user.name;
      state.roles = action.payload.user.roles;
      state.token = action.payload.accessToken;
      state.accessToken = action.payload.accessToken;
    },

    logoutSuccess: (state) => {
      state.isLoggedIn = false;
      state.loading = false;
      state.token = null;
      state.accessToken = null;
      state.username = null;
      state.roles = [];
      state.user = null;
    },

    logout: (state) => {
      state.isLoggedIn = false;
      state.loading = false;
      state.token = null;
      state.accessToken = null;
      state.username = null;
      state.roles = [];
      state.user = null;
    },

    authLoaded: (state) => {
      state.loading = false;
    },
  },
});

export const {
  loginSuccess,
  login,
  logoutSuccess,
  logout,
  authLoaded,
} = authSlice.actions;

export default authSlice.reducer;