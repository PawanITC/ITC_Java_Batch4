import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "../pages/Home";

<<<<<<< HEAD
import Login from "../pages/LoginPage";
=======
import Login from "../pages/Login";
>>>>>>> c4e713a82e1baa1ee93fdb436c56d7991ad51579
import Profile from "../pages/Profile";
import ProtectedRoute from "./ProtectedRoute";

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>

        {/* Public Routes */}
        <Route path="/login" element={<Login />} />

        {/* Protected Routes */}
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<Home />} />
          <Route path="/profile" element={<Profile />} />
        </Route>

      </Routes>
    </BrowserRouter>
  );
}