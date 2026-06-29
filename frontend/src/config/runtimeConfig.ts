const defaultAppOrigin =
  typeof window !== "undefined" && window.location?.origin
    ? window.location.origin
    : "http://localhost:3000";

const isLocalHost =
  typeof window !== "undefined" &&
  window.location?.hostname !== undefined &&
  ["localhost", "127.0.0.1", "0.0.0.0"].includes(window.location.hostname);

const isLocalUrl = (value: string) =>
  /^https?:\/\/(localhost|127\.0\.0\.1|0\.0\.0\.0)(:\d+)?(\/.*)?$/i.test(value);

const resolveRuntimeValue = (
  key: string,
  fallback: string,
  shouldUseCurrentOriginForLocalFallback = false
) => {
  const rawValue = process.env[key];

  if (typeof rawValue === "string" && rawValue.trim()) {
    const normalizedValue = rawValue.trim();

    if (
      key === "REACT_APP_KEYCLOAK_URL" &&
      isLocalUrl(normalizedValue) &&
      !isLocalHost &&
      shouldUseCurrentOriginForLocalFallback
    ) {
      return defaultAppOrigin;
    }

    return normalizedValue;
  }

  return fallback;
};

export const apiBaseUrl =
  process.env.REACT_APP_API_BASE_URL ?? "http://localhost:8085";

export const keycloakUrl = resolveRuntimeValue(
  "REACT_APP_KEYCLOAK_URL",
  isLocalHost ? "http://a9099f2319da740c3b1c3e37813205aa-704837357.eu-west-2.elb.amazonaws.com" : defaultAppOrigin,
  true
);

export const keycloakRealm =
  process.env.REACT_APP_KEYCLOAK_REALM ?? "linkedin-app";

export const keycloakClientId =
  process.env.REACT_APP_KEYCLOAK_CLIENT_ID ?? "linkedin-frontend";

export const logoutRedirectUri = resolveRuntimeValue(
  "REACT_APP_LOGOUT_REDIRECT_URI",
  `${defaultAppOrigin}/login`
);
