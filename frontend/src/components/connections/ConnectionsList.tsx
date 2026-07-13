import { Link } from "react-router-dom";
import { RefreshCw, Search, UserCheck, Users } from "lucide-react";
import { useCallback, useEffect, useState } from "react";
import {
  getCurrentProfile,
  getFollowers,
  getFollowing,
  Profile,
} from "../../features/userprofile/api";
import "./connections.css";

type TabType = "following" | "followers";

function fullName(profile: Profile) {
  return [profile.firstName, profile.lastName].filter(Boolean).join(" ") || "LinkedIn User";
}

function initials(profile: Profile) {
  const value = fullName(profile)
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();

  return value || "U";
}

export default function ConnectionsList() {
  const [tab, setTab] = useState<TabType>("following");
  const [currentProfile, setCurrentProfile] = useState<Profile | null>(null);
  const [people, setPeople] = useState<Profile[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const loadNetwork = useCallback(async () => {
    try {
      setLoading(true);
      setMessage("");

      const profile = currentProfile ?? (await getCurrentProfile());
      setCurrentProfile(profile);

      const data =
        tab === "following"
          ? await getFollowing(profile.id)
          : await getFollowers(profile.id);

      setPeople(data);
    } catch (error) {
      console.error("Failed to load network list:", error);
      setPeople([]);
      setMessage("Unable to load your network right now.");
    } finally {
      setLoading(false);
    }
  }, [currentProfile, tab]);

  useEffect(() => {
    void loadNetwork();
  }, [loadNetwork]);

  return (
    <div className="connections-page">
      <aside className="connections-sidebar">
        <div className="connections-panel">
          <h2>Manage my network</h2>
          <button
            className={tab === "following" ? "network-menu-item active" : "network-menu-item"}
            onClick={() => setTab("following")}
            type="button"
          >
            <UserCheck size={20} />
            <span>Following</span>
          </button>
          <button
            className={tab === "followers" ? "network-menu-item active" : "network-menu-item"}
            onClick={() => setTab("followers")}
            type="button"
          >
            <Users size={20} />
            <span>Followers</span>
          </button>
        </div>
      </aside>

      <section className="connections-main">
        <div className="connections-header">
          <div>
            <h1>{tab === "following" ? "People you follow" : "People following you"}</h1>
            <p>
              {tab === "following"
                ? "Profiles you followed from search and profile pages appear here."
                : "Profiles that follow your updates appear here."}
            </p>
          </div>
          <button
            className="icon-action"
            type="button"
            onClick={() => void loadNetwork()}
            disabled={loading}
            title="Refresh"
          >
            <RefreshCw size={18} />
          </button>
        </div>

        {message && <p className="connection-alert">{message}</p>}

        {loading ? (
          <div className="connections-empty">Loading your network...</div>
        ) : people.length === 0 ? (
          <div className="connections-empty">
            <Search size={28} />
            <p>
              {tab === "following"
                ? "You are not following anyone yet."
                : "No followers yet."}
            </p>
            {tab === "following" && (
              <Link className="connection-btn primary" to="/search?type=people">
                Find people
              </Link>
            )}
          </div>
        ) : (
          <div className="connections-list">
            {people.map((person) => (
              <Link
                className="connection-card"
                key={person.id}
                to={`/profiles/${person.id}`}
              >
                <div className="avatar">
                  {person.profilePictureUrl ? (
                    <img src={person.profilePictureUrl} alt="" />
                  ) : (
                    initials(person)
                  )}
                </div>

                <div className="connection-info">
                  <h3>{fullName(person)}</h3>
                  <p>{person.headline || person.currentPosition || "LinkedIn Member"}</p>
                  {(person.city || person.country) && (
                    <p>{[person.city, person.country].filter(Boolean).join(", ")}</p>
                  )}
                </div>

                <span className="connection-view">View profile</span>
              </Link>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
