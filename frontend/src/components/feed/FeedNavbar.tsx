import { Link, NavLink, useNavigate } from "react-router-dom";
import {
  Bell,
  BriefcaseBusiness,
  Grid3X3,
  Home,
  LogOut,
  MessageSquare,
  Search,
  Users,
} from "lucide-react";
import keycloak from "../../features/auth/keycloak";

export default function FeedNavbar() {
  const navigate = useNavigate();

  const navItems = [
    { label: "Home", to: "/", icon: Home },
    { label: "My Network", to: "/search", icon: Users },
    { label: "Jobs", to: "/search?type=jobs", icon: BriefcaseBusiness },
    { label: "Messaging", to: "/", icon: MessageSquare },
    { label: "Notifications", to: "/", icon: Bell },
  ];

  return (
    <>
      <nav className="sticky top-0 z-50 border-b border-[#d0d7de] bg-white shadow-[0_1px_2px_rgba(0,0,0,0.08)]">
        <div className="mx-auto flex h-[52px] max-w-[1128px] items-center justify-between gap-4 px-3 sm:px-4">
          <div className="flex min-w-0 flex-1 items-center gap-2">
            <Link
              to="/"
              aria-label="LinkedIn home"
              className="flex h-[34px] w-[34px] shrink-0 items-center justify-center rounded bg-[#0a66c2] text-lg font-bold leading-none text-white"
            >
              in
            </Link>

            <label className="hidden h-[34px] w-full max-w-[280px] items-center gap-2 rounded bg-[#edf3f8] px-3 text-sm text-gray-500 sm:flex">
              <Search size={17} />
              <input
                placeholder="Search"
                onFocus={() => navigate("/search")}
                className="min-w-0 flex-1 bg-transparent text-sm text-gray-900 outline-none placeholder:text-gray-500"
              />
            </label>
          </div>

          <div className="hidden h-full items-center md:flex">
            {navItems.map(({ label, to, icon: Icon }) => (
              <NavLink
                key={label}
                to={to}
                className={({ isActive }) =>
                  `flex h-full min-w-[64px] flex-col items-center justify-center gap-0.5 border-b-2 px-2 text-xs transition hover:bg-[#f3f6f8] hover:text-[#0a66c2] ${
                    isActive
                      ? "border-[#0a66c2] text-[#0a66c2]"
                      : "border-transparent text-gray-600"
                  }`
                }
              >
                <Icon size={20} strokeWidth={1.8} />
                <span className="leading-none">{label}</span>
              </NavLink>
            ))}

            <Link
              to="/profile"
              className="flex h-full min-w-[58px] flex-col items-center justify-center gap-0.5 border-b-2 border-transparent px-2 text-xs text-gray-600 hover:bg-[#f3f6f8] hover:text-[#0a66c2]"
            >
              <span className="flex h-6 w-6 items-center justify-center rounded-full bg-[#0a66c2] text-[10px] font-semibold text-white">
                ST
              </span>
              <span className="leading-none">Me</span>
            </Link>

            <button
              className="flex h-full min-w-[78px] flex-col items-center justify-center gap-0.5 border-l border-[#dfdeda] px-2 text-xs text-gray-600 hover:bg-[#f3f6f8] hover:text-[#0a66c2]"
              title="For Business"
            >
              <Grid3X3 size={19} strokeWidth={1.8} />
              <span className="leading-none">For Business</span>
            </button>

            <button
              onClick={() =>
                keycloak.logout({
                  redirectUri: `${window.location.origin}/login`,
                })
              }
              className="flex h-full min-w-[50px] items-center justify-center border-l border-[#dfdeda] px-2 text-gray-600 hover:text-red-600"
              title="Logout"
            >
              <LogOut size={20} strokeWidth={1.8} />
            </button>
          </div>

          <button
            onClick={() => navigate("/search")}
            className="flex h-10 w-10 items-center justify-center rounded-full text-gray-700 hover:bg-gray-100 sm:hidden"
            title="Search"
          >
            <Search size={22} />
          </button>
        </div>
      </nav>

      <nav className="fixed bottom-0 left-0 right-0 z-50 grid h-14 grid-cols-4 border-t border-[#dfdeda] bg-white md:hidden">
        {navItems.slice(0, 4).map(({ label, to, icon: Icon }) => (
          <NavLink
            key={label}
            to={to}
            className={({ isActive }) =>
              `flex flex-col items-center justify-center gap-0.5 text-[11px] ${
                isActive ? "text-[#0a66c2]" : "text-gray-600"
              }`
            }
          >
            <Icon size={21} strokeWidth={1.8} />
            <span>{label.replace("My ", "")}</span>
          </NavLink>
        ))}
      </nav>
    </>
  );
}
