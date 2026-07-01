import { useState, useEffect } from "react";

interface RecentSearchesProps {
  jobs: any[];
}

export function RecentSearches({ jobs }: RecentSearchesProps) {
  // 1. Initialize local state with the first 3 jobs
  const [searches, setSearches] = useState<any[]>([]);

  // 2. Sync the local state once the parent component finishes loading the API jobs array
  useEffect(() => {
    if (jobs && jobs.length > 0) {
      setSearches(jobs.slice(0, 3));
    }
  }, [jobs]);

  // If there are no searches left to show (or after clearing), hide the component or show an empty state
  if (searches.length === 0) {
    return (
      <div className="bg-white rounded-xl border border-gray-200 p-4 mb-4 text-center text-sm text-gray-500">
        No recent searches available.
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-4 mb-4">
      <div className="flex justify-between items-center mb-3">
        <h2 className="text-lg font-semibold text-gray-900">Recent job searches</h2>
        {/* 3. Attach the click handler to empty the array */}
        <button 
          onClick={() => setSearches([])}
          className="text-sm font-semibold text-gray-600 hover:text-red-600 transition-colors"
        >
          Clear
        </button>
      </div>

      <div className="divide-y divide-gray-100">
        {searches.map((job) => (
          <div key={job.id} className="flex items-start gap-3 py-2.5 first:pt-0 last:pb-0">
            <div className="text-gray-400 mt-0.5 text-sm">🕒</div>
            <div>
              <h4 className="font-semibold text-[15px] text-gray-900 hover:text-blue-600 hover:underline cursor-pointer leading-snug">
                {job.title}
              </h4>
              <p className="text-xs text-gray-500 mt-0.5">
                {job.location} • Actively Hiring
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}