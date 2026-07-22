import { render, screen } from "@testing-library/react";
import App from "../App";

jest.mock("../routes/AppRoutes", () => () => <div>App routes loaded</div>);

test("renders the app routes", () => {
  render(<App />);
  expect(screen.getByText(/app routes loaded/i)).toBeInTheDocument();
});
