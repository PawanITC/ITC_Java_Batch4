import { fireEvent, render, screen } from "@testing-library/react";
import keycloak from "../features/auth/keycloak";
import LoginPage from "../pages/LoginPage";

jest.mock("../features/auth/keycloak", () => ({
  login: jest.fn(),
  register: jest.fn(),
}));

describe("LoginPage", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renders the login page content and actions", () => {
    render(<LoginPage />);

    expect(
      screen.getByRole("heading", {
        name: /welcome to your professional community/i,
      })
    ).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /join now/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /^sign in$/i })).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /sign in with keycloak/i })
    ).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /create account/i })
    ).toBeInTheDocument();
  });

  test("starts Keycloak login from both sign-in buttons", () => {
    render(<LoginPage />);

    fireEvent.click(screen.getByRole("button", { name: /^sign in$/i }));
    fireEvent.click(screen.getByRole("button", { name: /sign in with keycloak/i }));

    expect(keycloak.login).toHaveBeenCalledTimes(2);
    expect(keycloak.login).toHaveBeenCalledWith({
      redirectUri: `${window.location.origin}/`,
    });
  });

  test("starts Keycloak registration from both create-account actions", () => {
    render(<LoginPage />);

    fireEvent.click(screen.getByRole("button", { name: /join now/i }));
    fireEvent.click(screen.getByRole("button", { name: /create account/i }));

    expect(keycloak.register).toHaveBeenCalledTimes(2);
    expect(keycloak.register).toHaveBeenCalledWith({
      redirectUri: `${window.location.origin}/`,
    });
  });
});
