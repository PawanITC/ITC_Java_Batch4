import keycloak from "./keycloak";

type RealmAccess = {
  roles?: string[];
};

type KeycloakUserToken = {
  sub?: string;
  email?: string;
  given_name?: string;
  family_name?: string;
  name?: string;
  preferred_username?: string;
  picture?: string;
  realm_access?: RealmAccess;
};

export type KeycloakUser = {
  id?: string;
  firstName: string;
  lastName: string;
  displayName: string;
  email?: string;
  username: string;
  roles: string[];
  profilePictureUrl?: string;
};

const fallbackNameParts = (value: string) => {
  const parts = value.trim().split(/\s+/).filter(Boolean);

  return {
    firstName: parts[0] || "LinkedIn",
    lastName: parts.slice(1).join(" ") || "Member",
  };
};

export function getKeycloakUser(): KeycloakUser | null {
  const token = keycloak.tokenParsed as KeycloakUserToken | undefined;

  if (!token) {
    return null;
  }

  const username = token.preferred_username || token.email || token.sub || "";
  const nameFallback = token.name || username;
  const names = fallbackNameParts(nameFallback);
  const firstName = token.given_name || names.firstName;
  const lastName = token.family_name || names.lastName;

  return {
    id: token.sub,
    firstName,
    lastName,
    displayName: `${firstName} ${lastName}`.trim(),
    email: token.email,
    username,
    roles: token.realm_access?.roles || [],
    profilePictureUrl: token.picture,
  };
}
