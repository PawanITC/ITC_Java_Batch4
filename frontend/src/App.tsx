<<<<<<< HEAD
import { useSelector } from "react-redux";
import { RootState } from "./store/store";
import LoginPage from "./pages/LoginPage";
import Dashboard from "./pages/Dashboard";

function App() {
  const { isLoggedIn, loading } = useSelector(
    (state: RootState) => state.auth
  );

  if (loading) {
    return <div>Loading authentication...</div>;
  }

  if (!isLoggedIn) {
    return <LoginPage />;
  }

  return <Dashboard />;
}

export default App;
=======
import AppRoutes from "./routes/AppRoutes";

export default function App() {
  return <AppRoutes  />;
}
>>>>>>> c4e713a82e1baa1ee93fdb436c56d7991ad51579
