import { JobSearchResult } from "../../types/search";

export default function JobResultCard({ job }: { job: JobSearchResult }) {
  return (
    <div className="bg-white rounded-lg shadow p-4">
      <h3 className="font-semibold text-[#0A66C2]">{job.title}</h3>
      <p className="text-sm text-gray-700">{job.companyName}</p>
      <p className="text-sm text-gray-500">{job.location}</p>
      <p className="text-xs text-green-700 mt-2">{job.workplaceType}</p>
    </div>
  );
}