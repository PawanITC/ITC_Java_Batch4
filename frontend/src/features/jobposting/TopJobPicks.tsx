
export const DUMMY_JOBS = [
  {
    id: 1,
    title: 'Full Stack Engineer',
    company: 'Computappoint',
    location: 'London Area, United Kingdom',
    type: 'Remote',
    salary: '60K GBP/yr - 70K GBP/yr',
    logo: 'https://via.placeholder.com/48/0a66c2/fff?text=CA',
    status: 'Actively reviewing applicants',
    promoted: true,
    easyApply: true,
  },
  {
    id: 2,
    title: 'Full Stack Engineer',
    company: 'NearTech Search',
    location: 'London Area, United Kingdom',
    type: 'On-site',
    salary: '50K GBP/yr - 55K GBP/yr',
    logo: 'https://via.placeholder.com/48/563d7c/fff?text=NT',
    status: 'Actively reviewing applicants',
    promoted: true,
    easyApply: true,
  },
  {
    id: 3,
    title: 'Back End Developer',
    company: 'develop',
    location: 'London Area, United Kingdom',
    type: 'Hybrid',
    salary: '75K GBP/yr - 90K GBP/yr',
    logo: 'https://via.placeholder.com/48/000/fff?text=dev',
    status: 'Actively reviewing applicants',
    promoted: true,
    easyApply: true,
  },
];

export const DUMMY_SEARCHES = [
  {
    id: 1,
    role: 'java developer',
    location: 'Horsham, England, United Kingdom',
    distance: '80 kilometers',
  },
  {
    id: 2,
    role: 'React developer',
    location: 'Crawley, England, United Kingdom',
    distance: '80 kilometers',
  },
  {
    id: 3,
    role: 'Frontend developer',
    location: 'London, England, United Kingdom',
    distance: '80 kilometers',
  },
];

export function TopJobPicks({ onShowAll, onSelectJob }:any) {
  return (
    <div className="bg-white rounded-xl border border-gray-200 overflow-hidden mb-4">
      <div className="p-4 pb-2">
        <h2 className="text-xl font-semibold text-gray-900">Top job picks for you</h2>
        <p className="text-xs text-gray-500 mt-0.5">Based on your profile, preferences, and activity like applies, searches, and saves</p>
      </div>

      <div className="divide-y divide-gray-200">
        {DUMMY_JOBS.map((job) => (
          <div key={job.id} className="p-4 flex gap-3 relative hover:bg-gray-50 transition-colors">
          
            <button className="absolute top-4 right-4 text-gray-500 hover:text-gray-700 text-lg font-bold">
              &times;
            </button>

            <img src={job.logo} alt={job.company} className="w-12 h-12 rounded object-cover flex-shrink-0" />

            <div className="flex-1 pr-6">
              {/* Click triggers action on parent panel layout */}
              <h3 
                onClick={() => onSelectJob && onSelectJob(job.id)}
                className="font-semibold text-[15px] text-blue-600 hover:underline cursor-pointer inline-block"
              >
                {job.title}
              </h3>
              <p className="text-sm text-gray-800 mt-0.5">
                {job.company} • {job.location} ({job.type}) • <span className="text-gray-600 font-medium">{job.salary}</span>
              </p>
              
              {job.status && (
                <div className="flex items-center gap-1.5 mt-2 text-xs font-medium text-green-700">
                  <span className="w-4 h-4 rounded-full border border-green-700 flex items-center justify-center text-[10px]">✓</span>
                  {job.status}
                </div>
              )}

              <div className="flex items-center gap-2 mt-2 text-xs text-gray-500">
                {job.promoted && <span>Promoted</span>}
                {job.easyApply && (
                  <span className="flex items-center gap-1 font-semibold text-blue-700">
                    <span className="text-[10px] px-1 bg-blue-700 text-white rounded-sm">in</span> Easy Apply
                  </span>
                )}
              </div>
            </div>
          </div>
        ))}
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