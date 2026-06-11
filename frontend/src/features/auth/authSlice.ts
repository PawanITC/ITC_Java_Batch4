import { createSlice } from "@reduxjs/toolkit";
import { AuthState } from "../../types/auth.types";


const initialState: AuthState = {
  user: null,
  isLoggedIn: false,
  accessToken: null,
  loading: false,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    login: (state, action) => {
      state.user = action.payload;
      state.isLoggedIn = true;
    },
    logout: (state) => {
      state.user = null;
      state.isLoggedIn = false;
    },
  },
});

export const { login, logout } = authSlice.actions;
export default authSlice.reducer;