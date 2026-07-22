export {};

describe("runtimeConfig", () => {
  const originalEnv = process.env;

  beforeEach(() => {
    jest.resetModules();
    process.env = { ...originalEnv };
    delete process.env.REACT_APP_KEYCLOAK_URL;
  });

  afterAll(() => {
    process.env = originalEnv;
  });

  test("falls back to the current deployment origin when Keycloak points to localhost", () => {
    Object.defineProperty(window, "location", {
      configurable: true,
      value: {
        origin: "https://example.com",
        hostname: "example.com",
      },
    });

    process.env.REACT_APP_KEYCLOAK_URL = "http://localhost:8080";

    const { keycloakUrl } = require("./runtimeConfig");

    expect(keycloakUrl).toBe("https://example.com");
  });
});
