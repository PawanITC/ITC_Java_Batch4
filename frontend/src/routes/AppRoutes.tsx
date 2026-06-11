import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "../pages/Home";

import Login from "../pages/LoginPage";
import Profile from "../pages/Profile";
import ProtectedRoute from "./ProtectedRoute";
import UserProfile from "../pages/UserProfile";

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>

        {/* Public Routes */}
        <Route path="/login" element={<Login />}/>
        <Route path="/user-profile" element={<UserProfile/>} />

        {/* Protected Routes */}
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<Home />} />
          <Route path="/profile" element={<Profile />} />
        </Route>

      </Routes>
    </BrowserRouter>
  );
}