import { CompanySearchResult } from "../../types/search";

export default function CompanyResultCard({ company }: { company: CompanySearchResult }) {
  return (
    <div className="bg-white rounded-lg shadow p-4 flex gap-4">
      <div className="w-14 h-14 bg-gray-200 rounded flex items-center justify-center">
        🏢
      </div>

      <div>
        <h3 className="font-semibold">{company.name}</h3>
        <p className="text-sm text-gray-600">{company.industry}</p>
        <p className="text-xs text-gray-500">{company.location}</p>
        <p className="text-xs text-gray-400">{company.followers} followers</p>
      </div>
    </div>
  );
}