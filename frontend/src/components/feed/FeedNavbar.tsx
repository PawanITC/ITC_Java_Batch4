import keycloak from "../../features/auth/keycloak";

export default function FeedNavbar() {
  return (
    <nav className="bg-white border-b sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-6 py-2 flex justify-between items-center">
        <div className="flex items-center gap-4">
          <div className="text-3xl font-bold text-[#0A66C2]">
            Linked<span className="bg-[#0A66C2] text-white px-1 rounded">in</span>
          </div>

          <input
            placeholder="Search"
            className="bg-[#eef3f8] px-4 py-2 rounded-md w-64 outline-none text-sm"
          />
        </div>

        <div className="flex items-center gap-6 text-sm text-gray-600">
          <span>🏠 Home</span>
          <span>👥 Network</span>
          <span>💼 Jobs</span>
          <span>💬 Messaging</span>
          <button
            onClick={() =>
              keycloak.logout({
                redirectUri: `${window.location.origin}/login`,
              })
            }
            className="text-red-500 font-semibold"
          >
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
}