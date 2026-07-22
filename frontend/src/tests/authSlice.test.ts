import authReducer, {
  authLoaded,
  login,
  loginSuccess,
  logout,
  logoutSuccess,
} from "../store/authSlice";

describe("authSlice", () => {
  test("marks authentication as loaded", () => {
    const state = authReducer(undefined, authLoaded());

    expect(state.loading).toBe(false);
    expect(state.isLoggedIn).toBe(false);
  });

  test("stores token and user details on loginSuccess", () => {
    const state = authReducer(
      undefined,
      loginSuccess({
        token: "token-123",
        username: "Sam",
        roles: ["USER"],
      })
    );

    expect(state.isLoggedIn).toBe(true);
    expect(state.loading).toBe(false);
    expect(state.token).toBe("token-123");
    expect(state.accessToken).toBe("token-123");
    expect(state.username).toBe("Sam");
    expect(state.user).toEqual({ name: "Sam", roles: ["USER"] });
  });

  test("stores user object on login", () => {
    const state = authReducer(
      undefined,
      login({
        accessToken: "access-123",
        user: {
          id: "user-1",
          name: "Taylor",
          email: "taylor@example.com",
          roles: ["ADMIN"],
        },
      })
    );

    expect(state.isLoggedIn).toBe(true);
    expect(state.accessToken).toBe("access-123");
    expect(state.username).toBe("Taylor");
    expect(state.roles).toEqual(["ADMIN"]);
  });

  test("clears auth state on logout actions", () => {
    const loggedInState = authReducer(
      undefined,
      loginSuccess({
        token: "token-123",
        username: "Sam",
        roles: ["USER"],
      })
    );

    expect(authReducer(loggedInState, logout())).toMatchObject({
      isLoggedIn: false,
      loading: false,
      token: null,
      accessToken: null,
      username: null,
      roles: [],
      user: null,
    });

    expect(authReducer(loggedInState, logoutSuccess())).toMatchObject({
      isLoggedIn: false,
      loading: false,
      token: null,
      accessToken: null,
      username: null,
      roles: [],
      user: null,
    });
  });
});
