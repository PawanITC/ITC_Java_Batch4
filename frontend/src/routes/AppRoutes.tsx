import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import Profile from "../pages/Profile";
import UserProfile from "../pages/UserProfile";
import ProtectedRoute from "./ProtectedRoute";
import FeedTimelinePage from "../pages/FeedTimelinePage";
import SearchDiscoveryPage from "../pages/SearchDiscoveryPage";

export default function AppRoutes() {


  return (
    <BrowserRouter>
      <Routes>
        
        <Route path="/login" element={<LoginPage />} />

        {/* Public Routes */}
        <Route path="/user-profile" element={<UserProfile/>} />

        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<FeedTimelinePage />} />
          <Route path="/search" element={<SearchDiscoveryPage />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/user-profile" element={<UserProfile />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}