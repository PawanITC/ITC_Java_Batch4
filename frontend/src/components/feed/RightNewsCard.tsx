import { ArrowRight, Info, Plus } from "lucide-react";
import coverImage from "../../assets/cover.jpeg";

const newsItems = [
  {
    title: "Java jobs continue to grow",
    meta: "2h ago - 1,240 readers",
  },
  {
    title: "Spring Boot microservices in demand",
    meta: "4h ago - 980 readers",
  },
  {
    title: "Kubernetes skills rising",
    meta: "1d ago - 2,540 readers",
  },
  {
    title: "Teams invest in platform engineering",
    meta: "1d ago - 842 readers",
  },
];

const followSuggestions = [
  {
    name: "Spring Boot Community",
    meta: "Company - Software development",
    initials: "SB",
  },
  {
    name: "Cloud Native India",
    meta: "Technology community",
    initials: "CN",
  },
  {
    name: "React Developers",
    meta: "Group - 1.2M members",
    initials: "RD",
  },
];

export default function RightNewsCard() {
  return (
    <aside className="sticky top-[68px] space-y-2">
      <div className="rounded-lg border border-[#d6d6d6] bg-white p-3 shadow-[0_1px_2px_rgba(0,0,0,0.08)]">
        <div className="mb-2 flex items-center justify-between">
          <h3 className="text-base font-semibold">LinkedIn News</h3>
          <Info size={17} className="text-gray-500" />
        </div>

        <ul className="space-y-1">
          {newsItems.map((item) => (
            <li key={item.title}>
              <button className="w-full rounded px-1 py-2 text-left hover:bg-[#f3f6f8]">
                <p className="text-sm font-semibold leading-5">{item.title}</p>
                <p className="mt-0.5 text-xs text-gray-500">{item.meta}</p>
              </button>
            </li>
          ))}
        </ul>
      </div>

      <div className="rounded-lg border border-[#d6d6d6] bg-white p-3 shadow-[0_1px_2px_rgba(0,0,0,0.08)]">
        <div className="mb-3 flex items-center justify-between">
          <h3 className="text-base font-semibold">Add to your feed</h3>
          <Info size={17} className="text-gray-500" />
        </div>

        <div className="space-y-3">
          {followSuggestions.map((suggestion) => (
            <div key={suggestion.name} className="flex gap-2">
              <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-[#edf3f8] text-sm font-semibold text-[#0a66c2]">
                {suggestion.initials}
              </div>
              <div className="min-w-0">
                <p className="truncate text-sm font-semibold">
                  {suggestion.name}
                </p>
                <p className="line-clamp-2 text-xs leading-4 text-gray-500">
                  {suggestion.meta}
                </p>
                <button className="mt-2 flex h-8 items-center gap-1 rounded-full border border-[#0a66c2] px-3 text-sm font-semibold text-[#0a66c2] hover:border-[#004182] hover:bg-[#edf3f8] hover:text-[#004182]">
                  <Plus size={16} />
                  Follow
                </button>
              </div>
            </div>
          ))}
        </div>

        <button className="mt-3 flex items-center gap-1 rounded px-1 py-1 text-sm font-semibold text-gray-600 hover:bg-[#f3f6f8] hover:text-[#0a66c2]">
          View all recommendations
          <ArrowRight size={16} />
        </button>
      </div>

      <div
        className="relative h-[250px] overflow-hidden rounded-lg border border-[#d6d6d6] bg-cover bg-center shadow-[0_1px_2px_rgba(0,0,0,0.08)]"
        style={{ backgroundImage: `url(${coverImage})` }}
      >
        <div className="absolute inset-0 bg-black/25" />
        <div className="relative p-4 text-white">
          <p className="text-lg font-semibold leading-6">
            Your dream job is closer than you think
          </p>
          <button className="mt-4 rounded bg-[#0a66c2] px-3 py-1.5 text-sm font-semibold">
            See jobs
          </button>
        </div>
      </div>

      <div className="px-4 py-3 text-center text-xs leading-6 text-gray-500">
        About - Accessibility - Help Center - Privacy - Terms - Advertising
      </div>
    </aside>
  );
}
