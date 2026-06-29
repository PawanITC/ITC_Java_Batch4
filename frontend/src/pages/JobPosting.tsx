import { useState } from "react";
import { JobFooterLinks } from "../features/jobposting/JobFooterLinks";
import { JobNavigationMenu } from "../features/jobposting/JobNavigationMenu";
import { PremiumBanner } from "../features/jobposting/PremiumBanner";
import { RecentSearches } from "../features/jobposting/RecentSearches";
import { TopJobPicks, DUMMY_JOBS } from "../features/jobposting/TopJobPicks";

export default function JobPosting() {
  const [viewMode, setViewMode] = useState("dashboard"); // Supports "dashboard" or "all-jobs"
  const [selectedJobId, setSelectedJobId] = useState(DUMMY_JOBS[0]?.id);

  const currentSelectedJob = DUMMY_JOBS.find((job) => job.id === selectedJobId) || DUMMY_JOBS[0];

  // --- View Mode A: Standard Dashboard View ---
  if (viewMode === "dashboard") {
    return (
      <div className="min-h-screen bg-[#f4f2ee] py-6 px-4">
        <div className="max-w-6xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-6">
          
          <div className="md:col-span-1 space-y-4">
            <div className="bg-white p-4 rounded-xl border border-gray-200 h-96">
              [My Existing Profile Component]
            </div>
            <JobNavigationMenu />
            <JobFooterLinks />
          </div>

          <div className="md:col-span-2 space-y-4">
            <TopJobPicks 
              onShowAll={() => {
                setSelectedJobId(DUMMY_JOBS[0]?.id);
                setViewMode("all-jobs");
              }} 
              onSelectJob={(id:any) => {
                setSelectedJobId(id);
                setViewMode("all-jobs");
              }}
            />
            <RecentSearches /> 
            <PremiumBanner /> 
          </div>

        </div>
      </div>
    );
  }

  // --- View Mode B: Split Screen View ("Show all" View ---
  return (
    <div className="min-h-screen bg-white border-t border-gray-200">
      <div className="max-w-7xl mx-auto flex h-[calc(100vh-60px)]">
        
        <div className="w-full md:w-[400px] border-r border-gray-200 overflow-y-auto flex-shrink-0 flex flex-col">
          <div className="p-4 border-b border-gray-100 bg-white sticky top-0 z-10">
            <button 
              onClick={() => setViewMode("dashboard")} 
              className="text-xs font-semibold text-blue-600 hover:underline mb-2 block"
            >
              ← Back to Dashboard
            </button>
            <h2 className="text-lg font-bold text-gray-900">Top job picks for you</h2>
            <p className="text-xs text-gray-500 mt-0.5">{DUMMY_JOBS.length} results</p>
          </div>

          <div className="divide-y divide-gray-200 flex-1">
            {DUMMY_JOBS.map((job) => {
              const isSelected = job.id === selectedJobId;
              return (
                <div
                  key={job.id}
                  onClick={() => setSelectedJobId(job.id)}
                  className={`p-4 flex gap-3 cursor-pointer transition-all relative ${
                    isSelected ? "bg-gray-100 border-l-4 border-black" : "hover:bg-gray-50"
                  }`}
                >
                  <img src={job.logo} alt={job.company} className="w-12 h-12 rounded object-cover flex-shrink-0" />
                  <div className="flex-1 pr-4">
                    <h3 className="font-semibold text-sm text-blue-600 leading-snug hover:underline">{job.title}</h3>
                    <p className="text-xs text-gray-900 mt-0.5">{job.company}</p>
                    <p className="text-xs text-gray-500">{job.location} ({job.type})</p>
                    <p className="text-xs text-gray-600 mt-1 font-medium">{job.salary}</p>
                    
                    {job.status && (
                      <div className="text-[11px] font-medium text-green-700 mt-2 flex items-center gap-1">
                        <span>✓</span> {job.status}
                      </div>
                    )}
                    <div className="text-[10px] text-gray-400 mt-2">
                      Viewed · Promoted · <span className="text-blue-700 font-semibold">in</span> Easy Apply
                    </div>
                  </div>
                  <button className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 text-sm">&times;</button>
                </div>
              );
            })}
          </div>
        </div>

        <div className="hidden md:block flex-1 overflow-y-auto p-6 bg-white">
          {currentSelectedJob ? (
            <div className="space-y-6 max-w-2xl">
              <div>
                <div className="flex items-center gap-2 mb-2">
                  <img src={currentSelectedJob.logo} alt={currentSelectedJob.company} className="w-8 h-8 rounded" />
                  <span className="text-sm font-medium text-gray-800">{currentSelectedJob.company}</span>
                </div>
                <h1 className="text-2xl font-bold text-gray-900">{currentSelectedJob.title}</h1>
                <p className="text-xs text-gray-500 mt-1">
                  {currentSelectedJob.location} · <span className="text-green-700 font-medium">15 minutes ago</span> · 21 applicants
                </p>
                <p className="text-xs text-gray-400 mt-0.5">Promoted by hirer · No response insights available yet</p>
              </div>

              <div className="flex gap-2 text-xs font-semibold text-gray-700">
                <span className="px-3 py-1.5 bg-gray-100 rounded-full flex items-center gap-1">
                  ✓ {currentSelectedJob.type}
                </span>
                <span className="px-3 py-1.5 bg-gray-100 rounded-full">Full-time</span>
              </div>

              <div className="flex gap-2">
                <button className="bg-blue-600 hover:bg-blue-700 text-white font-semibold text-sm px-5 py-2 rounded-full">
                  in Easy Apply
                </button>
                <button className="border border-blue-600 text-blue-600 hover:bg-blue-50 font-semibold text-sm px-5 py-2 rounded-full">
                  Save
                </button>
              </div>

              <hr className="border-gray-200" />

              <div className="border border-gray-200 rounded-xl p-4 bg-gradient-to-br from-white to-amber-50/10">
                <h3 className="font-semibold text-sm text-gray-900">How your profile and resume fit this job</h3>
                <p className="text-xs text-gray-500 mt-1">Get AI-powered advice on this job and more exclusive features with Premium.</p>
                <div className="flex gap-2 mt-3">
                  <button className="border border-gray-300 bg-white hover:bg-gray-50 text-xs font-semibold text-gray-700 px-3 py-1.5 rounded-full">✦ Show match details</button>
                  <button className="border border-gray-300 bg-white hover:bg-gray-50 text-xs font-semibold text-gray-700 px-3 py-1.5 rounded-full">✦ Tailor my resume</button>
                </div>
              </div>

              <div>
                <h3 className="text-base font-semibold text-gray-900 mb-2">About the job</h3>
                <p className="text-sm text-gray-700 leading-relaxed">
                  We are seeking a driven and detail-oriented professional to fill the {currentSelectedJob.title} position at {currentSelectedJob.company}. You will work closely with experienced team leads to design, deploy, and maintain robust infrastructure configurations.
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