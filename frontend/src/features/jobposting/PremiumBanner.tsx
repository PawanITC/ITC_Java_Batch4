export function PremiumBanner() {
  return (
    <div className="bg-white rounded-xl border-t-4 border-t-amber-500 border border-x-gray-200 border-b-gray-200 p-4">
      <div className="flex items-center gap-1.5 text-xs font-semibold text-amber-700 mb-2">
        <span className="bg-amber-500 text-white text-[9px] px-1 py-0.5 rounded-sm">👑</span> Premium
      </div>
      <h2 className="text-base font-semibold text-gray-900">Jobs where you're more likely to hear back</h2>
      <p className="text-xs text-gray-500 mt-0.5">Based on your chances of hearing back</p>

      <div className="flex items-center gap-3 mt-4">
        {/* Profile Stacking Section */}
        <div className="flex -space-x-2 overflow-hidden flex-shrink-0">
          <img 
            className="inline-block h-8 w-8 rounded-full ring-2 ring-white object-cover" 
            src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&q=80" 
            alt="Premium member 1" 
          />
          <img 
            className="inline-block h-8 w-8 rounded-full ring-2 ring-white object-cover" 
            src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100&q=80" 
            alt="Premium member 2" 
          />
        </div>
        
        <div className="text-xs text-gray-600 leading-snug">
          <p className="font-semibold text-gray-900">Apply smarter with jobs personalized for you</p>
          <p>Muhammad and millions of other members use Premium</p>
        </div>
      </div>

      <div className="mt-4">
        <button className="bg-amber-500 hover:bg-amber-600 text-amber-950 text-sm font-semibold py-1.5 px-4 rounded-full transition-colors">
          Try Premium for £0
        </button>
      </div>
    </div>
  );
}