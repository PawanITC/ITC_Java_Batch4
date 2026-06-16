import { Link } from "react-router-dom";
import keycloak from "../../features/auth/keycloak";

export default function SearchNavbar() {
  const logout = () => {
    keycloak.logout({
      redirectUri: `${window.location.origin}/login`,
    });
  };

  return (
    <nav className="bg-white border-b sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-6 py-2 flex justify-between items-center">
        <div className="flex items-center gap-5">
          <Link to="/" className="text-3xl font-bold text-[#0A66C2]">
            Linked
            <span className="bg-[#0A66C2] text-white px-1 rounded">in</span>
          </Link>

          <div className="hidden md:block">
            <input
              type="text"
              placeholder="Search"
              className="bg-[#eef3f8] px-4 py-2 rounded-md w-72 outline-none text-sm"
            />
          </div>
        </div>

        <div className="flex items-center gap-6 text-sm text-gray-600">
          <Link to="/" className="hover:text-black">
            🏠 Home
          </Link>

          <Link to="/search" className="hover:text-black">
            🔎 Search
          </Link>

          <Link to="/profile" className="hover:text-black">
            👤 Profile
          </Link>

          <button
            onClick={logout}
            className="text-red-500 font-semibold hover:text-red-700"
          >
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
}