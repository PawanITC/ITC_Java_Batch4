import { useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "../hooks/reduxHooks";
import { login, logout } from "../store/authSlice";

export default function LoginTest() {
  const dispatch = useAppDispatch();
  const auth = useAppSelector((state) => state.auth);
  const navigate = useNavigate(); 

  const handleLogin = () => {
    dispatch(
  login({
    user: {
      id: "test-user-1",
      name: "Hasnain",
      email: "test@mail.com",
      roles: ["USER"],
    },
    accessToken: "test-token",
  })
);

    // 👇 redirect after login
    navigate("/profile");
  };

  return (
    <div className="p-4">
      <p>User: {auth.user?.name || "No user"}</p>

      <button
        className="bg-green-500 text-white px-4 py-2 mr-2"
        onClick={handleLogin}
      >
        Login
      </button>

      <button
        className="bg-red-500 text-white px-4 py-2"
        onClick={() => dispatch(logout())}
      >
        Logout
      </button>
    </div>
  );
}