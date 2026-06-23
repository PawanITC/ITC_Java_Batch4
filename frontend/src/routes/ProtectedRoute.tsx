import { Navigate, Outlet } from "react-router-dom";
import { useAppSelector } from "../hooks/reduxHooks";
import FeedNavbar from "../components/feed/FeedNavbar";

export default function ProtectedRoute() {
  const { isLoggedIn, loading } = useAppSelector((state) => state.auth);

  if (loading) {
    return <div>Loading authentication...</div>;
  }

  return isLoggedIn ? (
    <>
      <FeedNavbar />
      <Outlet />
    </>
  ) : (
    <Navigate to="/login" replace />
  );
}
