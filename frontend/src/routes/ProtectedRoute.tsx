import { Navigate, Outlet } from "react-router-dom";
import { useAppSelector } from "../hooks/reduxHooks";

export default function ProtectedRoute() {
  const user = useAppSelector((state) => state.auth.user);

  console.log(user,"kjshkjhd")

  return user ? <Outlet /> : <Navigate to="/login" />;
}