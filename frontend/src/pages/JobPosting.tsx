import { useState, useEffect, useCallback } from "react";
import { JobFooterLinks } from "../features/jobposting/JobFooterLinks";
import { JobNavigationMenu } from "../features/jobposting/JobNavigationMenu";
import { PremiumBanner } from "../features/jobposting/PremiumBanner";
import { RecentSearches } from "../features/jobposting/RecentSearches";
import { TopJobPicks } from "../features/jobposting/TopJobPicks";
import { fetchJobsList } from "../features/jobposting/jobPostingApi";

export interface Requirement {
  id: string;
  isMandatory: boolean;
  requirement: string;
}

export interface BackendJob {
  id: string;
  title: string;
  companyId: string;
  description: string;
  location: string;
  salaryMin: number;
  salaryMax: number;
  status: string;
  benefits: string[];
  requirements: Requirement[];
  createdAt: string;
  updatedAt: string;
}

export interface ApiResponse {
  content: BackendJob[];
  totalElements: number;
  totalPages: number;
}

// A quick helper to format numbers to currency strings
const formatSalary = (min: number, max: number) => {
  return `$${min.toLocaleString()} - $${max.toLocaleString()} /yr`;
};

export default function JobPosting() {
  const [viewMode, setViewMode] = useState("dashboard"); 
  const [jobs, setJobs] = useState<BackendJob[]>([]);
  const [selectedJobId, setSelectedJobId] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Wrapped inside a reusable useCallback hook to be safely passed into child elements
const loadBackendJobsData = useCallback(async () => {
  try {
    setError(null);
    // Dynamic client call abstracting network URL strings
    const data = await fetchJobsList(); 
    const jobContent = data.content || [];
    setJobs(jobContent);

    if (jobContent.length > 0 && !selectedJobId) {
      setSelectedJobId(jobContent[0].id);
    }
  } catch (err: any) {
    setError(err.message || "Something went wrong fetching jobs.");
  } finally {
    setLoading(false);
  }
}, [selectedJobId]);

  // Initial fetch on component mount
  useEffect(() => {
    loadBackendJobsData();
  }, [loadBackendJobsData]);

  const currentSelectedJob =
    jobs.find((job) => job.id === selectedJobId) || jobs[0];

  if (loading) {
    return (
      <div className="min-h-screen bg-[#f4f2ee] flex items-center justify-center font-medium">
        Loading jobs...
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-[#f4f2ee] flex items-center justify-center text-red-600 font-medium">
        {error}
      </div>
    );
  }

  // --- View Mode A: Standard Dashboard View ---
  if (viewMode === "dashboard") {
    return (
      <div className="min-h-screen bg-[#f4f2ee] py-6 px-4">
        <div className="max-w-6xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="md:col-span-1 space-y-4">
           
            <JobNavigationMenu onJobAdded={loadBackendJobsData} />
            <JobFooterLinks />
          </div>

          <div className="md:col-span-2 space-y-4">
            <TopJobPicks
              jobs={jobs}
              onShowAll={() => {
                if (jobs.length > 0) setSelectedJobId(jobs[0].id);
                setViewMode("all-jobs");
              }}
              onSelectJob={(id: string) => {
                setSelectedJobId(id);
                setViewMode("all-jobs");
              }}
            />
            <RecentSearches jobs={jobs} />
            <PremiumBanner />
          </div>
        </div>
      </div>
    );
  }

  // --- View Mode B: Split Screen View ("Show all" View) ---
  return (
    <div className="min-h-screen bg-white border-t border-gray-200">
      <div className="max-w-7xl mx-auto flex h-[calc(100vh-60px)]">
        {/* Left Side: Jobs List Feed */}
        <div className="w-full md:w-[400px] border-r border-gray-200 overflow-y-auto flex-shrink-0 flex flex-col">
          <div className="p-4 border-b border-gray-100 bg-white sticky top-0 z-10">
            <button
              onClick={() => setViewMode("dashboard")}
              className="text-xs font-semibold text-blue-600 hover:underline mb-2 block"
            >
              ← Back to Dashboard
            </button>
            <h2 className="text-lg font-bold text-gray-900">
              Top job picks for you
            </h2>
            <p className="text-xs text-gray-500 mt-0.5">
              {jobs.length} results
            </p>
          </div>

          <div className="divide-y divide-gray-200 flex-1">
            {jobs.map((job) => {
              const isSelected = job.id === selectedJobId;
              return (
                <div
                  key={job.id}
                  onClick={() => setSelectedJobId(job.id)}
                  className={`p-4 flex gap-3 cursor-pointer transition-all relative ${
                    isSelected
                      ? "bg-gray-100 border-l-4 border-black"
                      : "hover:bg-gray-50"
                  }`}
                >
                  <div className="w-12 h-12 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold text-sm uppercase tracking-wider select-none flex-shrink-0">
                    {job.title ? job.title.trim().slice(0, 2) : "JB"}
                  </div>
                  <div className="flex-1 pr-4">
                    <h3 className="font-semibold text-sm text-blue-600 leading-snug hover:underline">
                      {job.title}
                    </h3>
                    <p className="text-xs text-gray-500">{job.location}</p>
                    <p className="text-xs text-gray-600 mt-1 font-medium">
                      {formatSalary(job.salaryMin, job.salaryMax)}
                    </p>

                    {job.status && (
                      <div className="text-[11px] font-medium text-green-700 mt-2 flex items-center gap-1">
                        <span>✓</span> {job.status}
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Right Side: Detailed View Layer */}
        <div className="hidden md:block flex-1 overflow-y-auto p-6 bg-white">
          {currentSelectedJob ? (
            <div className="space-y-6 max-w-2xl">
              <div>
                <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold text-xs uppercase tracking-wider select-none mb-3">
                  {currentSelectedJob?.title
                    ? currentSelectedJob.title.trim().slice(0, 2)
                    : "JB"}
                </div>
                <h1 className="text-2xl font-bold text-gray-900">
                  {currentSelectedJob.title}
                </h1>
                <p className="text-xs text-gray-500 mt-1">
                  {currentSelectedJob.location} ·{" "}
                  <span className="text-green-700 font-medium">
                    Posted{" "}
                    {new Date(currentSelectedJob.createdAt).toLocaleDateString()}
                  </span>
                </p>
              </div>

              <div className="flex gap-2">
                <button className="bg-blue-600 hover:bg-blue-700 text-white font-semibold text-sm px-5 py-2 rounded-full">
                  Easy Apply
                </button>
              </div>

              <hr className="border-gray-200" />

              {/* Requirements Section */}
              {currentSelectedJob.requirements &&
                currentSelectedJob.requirements.length > 0 && (
                  <div>
                    <h3 className="text-base font-semibold text-gray-900 mb-2">
                      Job Requirements
                    </h3>
                    <ul className="list-disc pl-5 space-y-1">
                      {currentSelectedJob.requirements.map((req) => (
                        <li key={req.id} className="text-sm text-gray-700">
                          {req.requirement}{" "}
                          {req.isMandatory && (
                            <span className="text-xs font-semibold text-red-500">
                              (Required)
                            </span>
                          )}
                        </li>
                      ))}
                    </ul>
                  </div>
                )}

              {/* Benefits Section */}
              {currentSelectedJob.benefits &&
                currentSelectedJob.benefits.length > 0 && (
                  <div>
                    <h3 className="text-base font-semibold text-gray-900 mb-2">
                      Benefits & Perks
                    </h3>
                    <div className="flex flex-wrap gap-2">
                      {currentSelectedJob.benefits.map((benefit, i) => (
                        <span
                          key={i}
                          className="px-3 py-1 bg-amber-50 text-amber-800 border border-amber-200 rounded-md text-xs font-medium"
                        >
                          ✦ {benefit}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

              <div>
                <h3 className="text-base font-semibold text-gray-900 mb-2">
                  About the job
                </h3>
                <p className="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">
                  {currentSelectedJob.description}
                </p>
              </div>
            </div>
          ) : (
            <div className="h-full flex items-center justify-center text-gray-400">
              Select a job from the left feed list to display full description layers.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}