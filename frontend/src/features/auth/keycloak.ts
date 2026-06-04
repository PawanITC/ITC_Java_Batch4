import Keycloak from "keycloak-js";

const keycloak = new Keycloak({
  url: "http://localhost:8080",
  realm: "linkedin-app",
  clientId: "linkedin-frontend",
});


export default keycloak;