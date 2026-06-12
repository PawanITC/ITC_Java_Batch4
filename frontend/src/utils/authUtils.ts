import keycloak from "../features/auth/keycloak";

export const getUserRoles = (): string[] => {
  const realmRoles = keycloak.tokenParsed?.realm_access?.roles || [];
  return realmRoles;
};

export const getUsername = (): string | null => {
  return keycloak.tokenParsed?.preferred_username || null;
};

export const getEmail = (): string | null => {
  return keycloak.tokenParsed?.email || null;
};