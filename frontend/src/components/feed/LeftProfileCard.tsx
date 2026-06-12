export default function LeftProfileCard() {
  return (
    <aside className="space-y-4">
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="h-20 bg-[#0A66C2]" />

        <div className="text-center px-4 pb-5">
          <div className="w-20 h-20 bg-gray-300 rounded-full mx-auto -mt-10 border-4 border-white flex items-center justify-center text-3xl">
            👩‍💻
          </div>

          <h2 className="font-semibold mt-3">Shubhra Tripathi</h2>
          <p className="text-sm text-gray-500">
            Java Full Stack Developer | Spring Boot | React
          </p>
        </div>

        <div className="border-t text-sm">
          <div className="flex justify-between px-4 py-2">
            <span className="text-gray-500">Profile viewers</span>
            <span className="text-[#0A66C2] font-semibold">128</span>
          </div>
          <div className="flex justify-between px-4 py-2">
            <span className="text-gray-500">Post impressions</span>
            <span className="text-[#0A66C2] font-semibold">1,420</span>
          </div>
        </div>
      </div>
    </aside>
  );
}