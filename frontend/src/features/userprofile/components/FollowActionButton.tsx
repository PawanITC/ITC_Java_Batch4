import { useEffect, useState } from "react";
import {
  followUser,
  getCurrentProfile,
  getFollowing,
  type Profile,
  unfollowUser,
} from "../api";

type FollowActionButtonProps = {
  targetProfileId: string;
  className?: string;
};

export default function FollowActionButton({
  targetProfileId,
  className = "",
}: FollowActionButtonProps) {
  const [currentProfile, setCurrentProfile] = useState<Profile | null>(null);
  const [isFollowing, setIsFollowing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function loadFollowState() {
      try {
        setLoading(true);
        setError("");

        const profile = await getCurrentProfile();
        if (cancelled) return;

        setCurrentProfile(profile);

        if (profile.id === targetProfileId) {
          setIsFollowing(false);
          return;
        }

        const following = await getFollowing(profile.id);
        if (!cancelled) {
          setIsFollowing(following.some((person) => person.id === targetProfileId));
        }
      } catch (loadError) {
        if (!cancelled) {
          console.error("Failed to load follow state:", loadError);
          setError("Unavailable");
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    loadFollowState();

    return () => {
      cancelled = true;
    };
  }, [targetProfileId]);

  const handleToggleFollow = async () => {
    if (!currentProfile || currentProfile.id === targetProfileId || saving) return;

    try {
      setSaving(true);
      setError("");

      const payload = {
        followerId: currentProfile.id,
        followingId: targetProfileId,
      };

      if (isFollowing) {
        await unfollowUser(payload);
        setIsFollowing(false);
      } else {
        await followUser(payload);
        setIsFollowing(true);
      }
    } catch (saveError: any) {
      const message = saveError?.response?.data?.message || "";
      const normalizedMessage = String(message).toLowerCase();

      if (normalizedMessage.includes("already following")) {
        setIsFollowing(true);
        return;
      }

      if (normalizedMessage.includes("not found") && isFollowing) {
        setIsFollowing(false);
        return;
      }

      console.error("Failed to update follow state:", saveError);
      setError("Try again");
    } finally {
      setSaving(false);
    }
  };

  if (currentProfile?.id === targetProfileId) {
    return null;
  }

  const baseClass =
    "rounded-full border px-4 py-1.5 text-sm font-semibold transition disabled:cursor-default disabled:opacity-70";
  const stateClass = isFollowing
    ? "border-gray-500 text-gray-700 hover:bg-gray-100"
    : "border-[#0A66C2] text-[#0A66C2] hover:bg-blue-50";

  return (
    <div className="flex flex-col items-start gap-1">
      <button
        type="button"
        onClick={handleToggleFollow}
        disabled={loading || saving || Boolean(error && !currentProfile)}
        className={`${baseClass} ${stateClass} ${className}`}
      >
        {loading ? "Loading..." : saving ? "Saving..." : isFollowing ? "Following" : "Follow"}
      </button>
      {error && <span className="text-xs text-red-600">{error}</span>}
    </div>
  );
}
