import { Navigate, Outlet } from "react-router-dom";
import { useAppSelector } from "../hooks/reduxHooks";

export default function ProtectedRoute() {
  const { isLoggedIn, loading } = useAppSelector((state) => state.auth);

  if (loading) {
    return <div>Loading authentication...</div>;
  }

  return isLoggedIn ? <Outlet /> : <Navigate to="/login" replace />;
}