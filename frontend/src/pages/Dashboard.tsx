import { useSelector } from "react-redux";
import { RootState } from "../store/store";
import keycloak from "../features/auth/keycloak";
import { useDispatch } from "react-redux";
import { logoutSuccess } from "../store/authSlice";
import { logoutRedirectUri } from "../config/runtimeConfig";

export default function Dashboard() {
  const dispatch = useDispatch();
  const { username, roles } = useSelector((state: RootState) => state.auth);

  const logout = () => {
    dispatch(logoutSuccess());
    keycloak.logout({
      redirectUri: logoutRedirectUri,
    });
  };

  return (
    <div className="p-8">
      <h1>Welcome {username}</h1>
      <p>Roles: {roles.join(", ")}</p>
      <button onClick={logout}>Logout</button>
    </div>
  );
}