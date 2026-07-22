import Keycloak from "keycloak-js";
import {
  keycloakClientId,
  keycloakRealm,
  keycloakUrl,
} from "../../config/runtimeConfig";

const keycloak = new Keycloak({
  url: keycloakUrl,
  realm: keycloakRealm,
  clientId: keycloakClientId,
});


export default keycloak;
