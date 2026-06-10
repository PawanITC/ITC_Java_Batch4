import { configureStore } from "@reduxjs/toolkit";
<<<<<<< HEAD
import authReducer from "./authSlice";
=======
import authReducer from "../features/auth/authSlice";
>>>>>>> c4e713a82e1baa1ee93fdb436c56d7991ad51579

export const store = configureStore({
  reducer: {
    auth: authReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;