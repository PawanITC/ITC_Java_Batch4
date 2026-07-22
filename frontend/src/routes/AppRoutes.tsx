import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import UserProfile from "../pages/UserProfile";
import ProtectedRoute from "./ProtectedRoute";
import FeedTimelinePage from "../pages/FeedTimelinePage";
import SearchDiscoveryPage from "../pages/SearchDiscoveryPage";
import NotificationsPage from "../pages/NotificationsPage";
import PremiumPage from "../pages/PremiumPage";
import ConnectionsPage from "../pages/ConnectionsPage";
import PostDetailPage from "../pages/PostDetailPage";

export default function AppRoutes() {


  return (
    <BrowserRouter>
      <Routes>

        <Route path="/login" element={<LoginPage />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<FeedTimelinePage />} />
          <Route path="/search" element={<SearchDiscoveryPage />} />
          <Route path="/posts/:postId" element={<PostDetailPage />} />
          <Route path="/profiles/:profileId" element={<UserProfile />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="/profile" element={<UserProfile />} />
          <Route path="/user-profile" element={<Navigate to="/profile" replace />} />
          <Route path="/premium" element={<PremiumPage />} />
          <Route path="/connections" element={<ConnectionsPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
