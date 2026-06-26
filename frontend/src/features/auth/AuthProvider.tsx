import { ReactNode, useEffect, useRef } from "react";
import { useDispatch } from "react-redux";
import keycloak from "./keycloak";
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
      dispatch(
        loginSuccess({
          token: keycloak.token || "",
          username: keycloak.tokenParsed?.preferred_username || "",
          roles: (keycloak.tokenParsed?.realm_access as any)?.roles || [],
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
          dispatch(
            loginSuccess({
              token: keycloak.token || "",
              username: keycloak.tokenParsed?.preferred_username || "",
              roles: (keycloak.tokenParsed?.realm_access as any)?.roles || [],
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
