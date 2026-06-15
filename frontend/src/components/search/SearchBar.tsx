type Props = {
  query: string;
  onChange: (value: string) => void;
  onSearch: () => void;
};

export default function SearchBar({ query, onChange, onSearch }: Props) {
  return (
    <div className="bg-white rounded-lg shadow p-4 flex gap-3">
      <input
        value={query}
        onChange={(e) => onChange(e.target.value)}
        placeholder="Search people, jobs, posts, companies..."
        className="flex-1 bg-[#eef3f8] px-4 py-3 rounded-md outline-none"
      />

      <button
        onClick={onSearch}
        className="bg-[#0A66C2] text-white px-6 py-2 rounded-full font-semibold"
      >
        Search
      </button>
    </div>
  );
}