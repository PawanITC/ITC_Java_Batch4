import { ReactNode, useEffect, useRef } from "react";
import { useDispatch } from "react-redux";
import keycloak from "./keycloak";
import {
  loginSuccess,
  authLoaded,
  logoutSuccess,
} from "../../store/authSlice";

type Props = {
  children: ReactNode;
};

export default function AuthProvider({ children }: Props) {
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
      .then((authenticated) => {
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
      .catch(() => {
        dispatch(authLoaded());
      });

    keycloak.onTokenExpired = () => {
      keycloak
        .updateToken(30)
        .then((refreshed) => {
          if (refreshed) {
            dispatch(
              loginSuccess({
                token: keycloak.token || "",
                username: keycloak.tokenParsed?.preferred_username || "",
                roles: (keycloak.tokenParsed?.realm_access as any)?.roles || [],
              })
            );
          }
        })
        .catch(() => {
          dispatch(logoutSuccess());
          keycloak.logout();
        });
    };
  }, [dispatch]);

  return <>{children}</>;
}