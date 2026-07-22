import { ReactNode, useEffect, useRef } from "react";
import { useDispatch } from "react-redux";
import keycloak from "./keycloak";
import { getKeycloakUser } from "./keycloakUser";
import {
  loginSuccess,
  authLoaded,
  logoutSuccess,
} from "../../store/authSlice";
import { logoutRedirectUri } from "../../config/runtimeConfig";

export default function AuthProvider({ children }: { children: ReactNode }) {
  const dispatch = useDispatch();
  const initialized = useRef(false);

  useEffect(() => {
    if (initialized.current) return;
    initialized.current = true;

    keycloak
  .init({
    onLoad: "check-sso",
    pkceMethod: "S256",
  })
  .then((authenticated: boolean) => {
    if (authenticated) {
      const keycloakUser = getKeycloakUser();

      dispatch(
        loginSuccess({
          token: keycloak.token || "",
          username: keycloakUser?.username || "",
          email: keycloakUser?.email,
          name: keycloakUser?.displayName,
          roles: keycloakUser?.roles || [],
        })
      );
    } else {
      dispatch(authLoaded());
    }
  })
  .catch((error: unknown) => {
    console.error("Keycloak initialization failed", error);
    dispatch(logoutSuccess());
    dispatch(authLoaded());
  });

    keycloak.onTokenExpired = () => {
      keycloak
        .updateToken(30)
        .then(() => {
          const keycloakUser = getKeycloakUser();

          dispatch(
            loginSuccess({
              token: keycloak.token || "",
              username: keycloakUser?.username || "",
              email: keycloakUser?.email,
              name: keycloakUser?.displayName,
              roles: keycloakUser?.roles || [],
            })
          );
        })
        .catch(() => {
          dispatch(logoutSuccess());
          keycloak.logout({ redirectUri: logoutRedirectUri });
        });
    };
  }, [dispatch]);

  return <>{children}</>;
}
