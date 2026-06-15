import { SearchType } from "../../types/search";

type Props = {
  activeTab: SearchType;
  onChange: (tab: SearchType) => void;
};

const tabs: SearchType[] = ["people", "posts", "jobs", "companies"];

export default function SearchTabs({ activeTab, onChange }: Props) {
  return (
    <div className="bg-white rounded-lg shadow p-2 flex gap-2">
      {tabs.map((tab) => (
        <button
          key={tab}
          onClick={() => onChange(tab)}
          className={`px-4 py-2 rounded-full text-sm font-semibold capitalize ${
            activeTab === tab
              ? "bg-[#0A66C2] text-white"
              : "text-gray-600 hover:bg-gray-100"
          }`}
        >
          {tab}
        </button>
      ))}
    </div>
  );
}