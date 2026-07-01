interface TopJobPicksProps {
  jobs: any[];
  onShowAll: () => void;
  onSelectJob: (id: string) => void;
}

export function TopJobPicks({ jobs, onShowAll, onSelectJob }: TopJobPicksProps) {
  // Take just the top 3 jobs to display on dashboard preview pane
  const previewJobs = jobs.slice(0, 3);

  return (
    <div className="bg-white rounded-xl border border-gray-200 overflow-hidden mb-4">
      <div className="p-4 pb-2">
        <h2 className="text-xl font-semibold text-gray-900">Top job picks for you</h2>
        <p className="text-xs text-gray-500 mt-0.5">Based on your live profile and activity matches</p>
      </div>

      <div className="divide-y divide-gray-200">
        {previewJobs.map((job) => (
          <div key={job.id} className="p-4 flex gap-3 relative hover:bg-gray-50 transition-colors">
            <div className="w-12 h-12 rounded bg-blue-600 text-white flex items-center justify-center font-bold text-sm uppercase tracking-wider select-none flex-shrink-0">
          {job.title ? job.title.trim().slice(0, 2) : "JB"}
        </div>

            <div className="flex-1 pr-6">
              <h3 
                onClick={() => onSelectJob && onSelectJob(job.id)}
                className="font-semibold text-[15px] text-blue-600 hover:underline cursor-pointer inline-block"
              >
                {job.title}
              </h3>
              <p className="text-sm text-gray-800 mt-0.5">
                {job.location} •{" "}
                <span className="text-gray-600 font-medium">
                  ${job.salaryMin.toLocaleString()} - ${job.salaryMax.toLocaleString()}
                </span>
              </p>
            </div>
          </div>
        ))}
        
        {jobs.length === 0 && (
          <div className="p-6 text-center text-sm text-gray-500">No active job listings found right now.</div>
        )}
      </div>

      <button 
        onClick={onShowAll}
        className="w-full py-3 border-t border-gray-200 font-semibold text-sm text-gray-600 hover:bg-gray-50 flex items-center justify-center gap-1 transition-colors"
      >
        Show all <span>→</span>
      </button>
    </div>
  );
}