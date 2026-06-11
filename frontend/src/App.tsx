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
