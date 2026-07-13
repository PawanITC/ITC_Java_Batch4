import { Link } from "react-router-dom";
import { PeopleSearchResult } from "../../types/search";

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

      <button className="self-start rounded-full border border-[#0A66C2] px-4 py-1 font-semibold text-[#0A66C2]">
        Follow
      </button>
      
      <button className="self-start rounded-full border border-[#0A66C2] px-4 py-1 font-semibold text-[#0A66C2]">
        Connect
      </button>
    </div>
  );
}
