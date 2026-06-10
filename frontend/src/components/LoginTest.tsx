import { useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "../hooks/reduxHooks";
<<<<<<< HEAD
import { login, logout } from "../store/authSlice";
=======
import { login, logout } from "../features/auth/authSlice";
>>>>>>> c4e713a82e1baa1ee93fdb436c56d7991ad51579

export default function LoginTest() {
  const dispatch = useAppDispatch();
  const auth = useAppSelector((state) => state.auth);
  const navigate = useNavigate(); 

  const handleLogin = () => {
<<<<<<< HEAD
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
=======
    dispatch(login({ name: "Hasnain", email: "test@mail.com" }));
>>>>>>> c4e713a82e1baa1ee93fdb436c56d7991ad51579

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