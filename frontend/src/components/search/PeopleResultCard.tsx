import { Link } from "react-router-dom";
import { useState } from "react";
import { PeopleSearchResult } from "../../types/search";
import { followUser, getCurrentProfile } from "../../features/userprofile/api";

type Props = {
  person: PeopleSearchResult;
};

function initials(name: string) {
  const value = name
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();

  return value || "DU";
}

export default function PeopleResultCard({ person }: Props) {
  const [isFollowing, setIsFollowing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState("");

  const handleFollow = async () => {
    if (isFollowing || isSaving) return;

    try {
      setIsSaving(true);
      setError("");

      const currentProfile = await getCurrentProfile();

      if (currentProfile.id === person.id) {
        setError("You cannot follow yourself.");
        return;
      }

      await followUser({
        followerId: currentProfile.id,
        followingId: person.id,
      });

      setIsFollowing(true);
    } catch (followError: any) {
      const message = followError?.response?.data?.message;

      if (typeof message === "string" && message.toLowerCase().includes("already following")) {
        setIsFollowing(true);
        return;
      }

      console.error("Failed to follow user:", followError);
      setError("Follow failed");
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="flex gap-4 rounded-lg bg-white p-4 shadow transition hover:shadow-md">
      <Link
        to={`/profiles/${person.id}`}
        className="flex flex-1 gap-4 rounded-md focus:outline-none focus:ring-2 focus:ring-[#0a66c2]"
      >
        <div className="flex h-14 w-14 shrink-0 items-center justify-center overflow-hidden rounded-full bg-gray-300 font-semibold text-gray-700">
          {person.profileImageUrl ? (
            <img src={person.profileImageUrl} alt="" className="h-full w-full object-cover" />
          ) : (
            initials(person.fullName)
          )}
        </div>

        <div className="flex-1">
          <h3 className="font-semibold text-gray-900">{person.fullName}</h3>
          <p className="text-sm text-gray-600">{person.headline}</p>
          <p className="text-xs text-gray-500">{person.location}</p>
          <p className="mt-1 text-xs text-gray-400">{person.connectionDegree}</p>
        </div>
      </Link>

      <div className="flex flex-col items-end gap-1">
        <button
          type="button"
          onClick={handleFollow}
          disabled={isSaving || isFollowing}
          className={`self-start rounded-full border px-4 py-1 font-semibold ${
            isFollowing
              ? "border-gray-400 text-gray-600"
              : "border-[#0A66C2] text-[#0A66C2] hover:bg-blue-50"
          } disabled:cursor-default disabled:opacity-70`}
        >
          {isSaving ? "Following..." : isFollowing ? "Following" : "Follow"}
        </button>
        {error && <span className="text-xs text-red-600">{error}</span>}
      </div>
      
      <button className="self-start rounded-full border border-[#0A66C2] px-4 py-1 font-semibold text-[#0A66C2]">
        Connect
      </button>
    </div>
  );
}
