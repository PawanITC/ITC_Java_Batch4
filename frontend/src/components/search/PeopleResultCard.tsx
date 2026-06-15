import { PeopleSearchResult } from "../../types/search";

type Props = {
  person: PeopleSearchResult;
};

export default function PeopleResultCard({ person }: Props) {
  return (
    <div className="bg-white rounded-lg shadow p-4 flex gap-4">
      <div className="w-14 h-14 rounded-full bg-gray-300 flex items-center justify-center">
        👤
      </div>

      <div className="flex-1">
        <h3 className="font-semibold text-gray-900">{person.fullName}</h3>
        <p className="text-sm text-gray-600">{person.headline}</p>
        <p className="text-xs text-gray-500">{person.location}</p>
        <p className="text-xs text-gray-400 mt-1">{person.connectionDegree}</p>
      </div>

      <button className="border border-[#0A66C2] text-[#0A66C2] px-4 py-1 rounded-full font-semibold">
        Connect
      </button>
    </div>
  );
}