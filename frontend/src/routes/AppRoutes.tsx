import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import UserProfile from "../pages/UserProfile";
import ProtectedRoute from "./ProtectedRoute";
import FeedTimelinePage from "../pages/FeedTimelinePage";
import SearchDiscoveryPage from "../pages/SearchDiscoveryPage";
import JobPosting from "../pages/JobPosting";
import NotificationsPage from "../pages/NotificationsPage";

export default function AppRoutes() {


  return (
    <BrowserRouter>
      <Routes>

        <Route path="/login" element={<LoginPage />} />
        <Route path="/jobposting" element={<JobPosting/>} />

        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<FeedTimelinePage />} />
          <Route path="/search" element={<SearchDiscoveryPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="/profile" element={<UserProfile />} />
          <Route path="/user-profile" element={<Navigate to="/profile" replace />} />
        </Route>
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}