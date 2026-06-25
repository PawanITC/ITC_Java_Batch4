const defaultAppOrigin =
  typeof window !== "undefined" ? window.location.origin : "http://localhost:3000";

export const apiBaseUrl =
  process.env.REACT_APP_API_BASE_URL ?? "http://localhost:8085";

export const keycloakUrl =
  process.env.REACT_APP_KEYCLOAK_URL ?? "http://localhost:8080";

export const keycloakRealm =
  process.env.REACT_APP_KEYCLOAK_REALM ?? "linkedin-app";

export const keycloakClientId =
  process.env.REACT_APP_KEYCLOAK_CLIENT_ID ?? "linkedin-frontend";

export const logoutRedirectUri =
  process.env.REACT_APP_LOGOUT_REDIRECT_URI ?? `${defaultAppOrigin}/login`;
