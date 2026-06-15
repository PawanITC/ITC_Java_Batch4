import { DiscoverySuggestion } from "../../types/search";

export default function DiscoverySuggestions({
  suggestions,
}: {
  suggestions: DiscoverySuggestion[];
}) {
  return (
    <div className="bg-white rounded-lg shadow p-4">
      <h3 className="font-semibold mb-3">People you may know</h3>

      <div className="space-y-4">
        {suggestions.map((person) => (
          <div key={person.id} className="border-b pb-3">
            <h4 className="font-medium">{person.fullName}</h4>
            <p className="text-sm text-gray-600">{person.headline}</p>
            <p className="text-xs text-gray-400">{person.reason}</p>
            <button className="mt-2 border border-[#0A66C2] text-[#0A66C2] px-4 py-1 rounded-full text-sm font-semibold">
              Connect
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}