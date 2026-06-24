import { fireEvent, render, screen } from "@testing-library/react";
import keycloak from "../features/auth/keycloak";
import FeedNavbar from "../components/feed/FeedNavbar";

const mockNavigate = jest.fn();

jest.mock(
  "react-router-dom",
  () => {
    return {
      Link: ({ children, to, ...props }: any) => (
        <a href={to} {...props}>
          {children}
        </a>
      ),
      NavLink: ({ children, to, className, ...props }: any) => (
        <a
          href={to}
          className={
            typeof className === "function" ? className({ isActive: false }) : className
          }
          {...props}
        >
          {children}
        </a>
      ),
      useNavigate: () => mockNavigate,
    };
  },
  { virtual: true }
);

jest.mock("../features/auth/keycloak", () => ({
  logout: jest.fn(),
}));

function renderNavbar() {
  return render(<FeedNavbar />);
}

describe("FeedNavbar", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renders the main navigation links", () => {
    renderNavbar();

    expect(screen.getByLabelText(/linkedin home/i)).toBeInTheDocument();
    expect(screen.getAllByText(/home/i)[0]).toBeInTheDocument();
    expect(screen.getAllByText(/network/i)[0]).toBeInTheDocument();
    expect(screen.getAllByText(/jobs/i)[0]).toBeInTheDocument();
    expect(screen.getAllByText(/messaging/i)[0]).toBeInTheDocument();
    expect(screen.getByText(/^me$/i)).toBeInTheDocument();
  });

  test("navigates to search when the search input receives focus", () => {
    renderNavbar();

    fireEvent.focus(screen.getByPlaceholderText(/search/i));

    expect(mockNavigate).toHaveBeenCalledWith("/search");
  });

  test("logs out through Keycloak with the login redirect", () => {
    renderNavbar();

    fireEvent.click(screen.getByTitle(/logout/i));

    expect(keycloak.logout).toHaveBeenCalledWith({
      redirectUri: `${window.location.origin}/login`,
    });
  });
});
