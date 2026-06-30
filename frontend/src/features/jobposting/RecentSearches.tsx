import { DUMMY_SEARCHES } from "./TopJobPicks";

export function RecentSearches() {
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-4 mb-4">
      <div className="flex justify-between items-center mb-3">
        <h2 className="text-lg font-semibold text-gray-900">Recent job searches</h2>
        <button className="text-sm font-semibold text-gray-600 hover:text-gray-900">Clear</button>
      </div>

      {DUMMY_SEARCHES.map((search) => (
        <div key={search.id} className="flex items-start gap-3 py-2">
          <div className="text-gray-500 mt-0.5 text-lg">🕒</div>
          <div>
            <h4 className="font-semibold text-[15px] text-gray-900 hover:underline cursor-pointer leading-snug">
              {search.role}
            </h4>
            <p className="text-xs text-gray-500 mt-0.5">
              {search.location} • {search.distance}
            </p>
          </div>
        </div>
      ))}
    </div>
  );
}