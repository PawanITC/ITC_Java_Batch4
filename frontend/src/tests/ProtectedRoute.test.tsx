import { render, screen } from "@testing-library/react";
import { useAppSelector } from "../hooks/reduxHooks";
import ProtectedRoute from "../routes/ProtectedRoute";

jest.mock("../hooks/reduxHooks", () => ({
  useAppSelector: jest.fn(),
}));

jest.mock("../components/feed/FeedNavbar", () => () => <nav>Feed navigation</nav>);

jest.mock(
  "react-router-dom",
  () => ({
    Navigate: ({ to }: { to: string }) => <main>Redirect to {to}</main>,
    Outlet: () => <main>Private feed</main>,
  }),
  { virtual: true }
);

const mockUseAppSelector = useAppSelector as jest.Mock;

function renderProtectedRoute() {
  return render(<ProtectedRoute />);
}

describe("ProtectedRoute", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("shows loading state while authentication is loading", () => {
    mockUseAppSelector.mockReturnValue({ isLoggedIn: false, loading: true });

    renderProtectedRoute();

    expect(screen.getByText(/loading authentication/i)).toBeInTheDocument();
    expect(screen.queryByText(/private feed/i)).not.toBeInTheDocument();
  });

  test("renders protected content when logged in", () => {
    mockUseAppSelector.mockReturnValue({ isLoggedIn: true, loading: false });

    renderProtectedRoute();

    expect(screen.getByText(/feed navigation/i)).toBeInTheDocument();
    expect(screen.getByText(/private feed/i)).toBeInTheDocument();
  });

  test("redirects to login when not logged in", () => {
    mockUseAppSelector.mockReturnValue({ isLoggedIn: false, loading: false });

    renderProtectedRoute();

    expect(screen.getByText(/redirect to \/login/i)).toBeInTheDocument();
    expect(screen.queryByText(/private feed/i)).not.toBeInTheDocument();
  });
});
