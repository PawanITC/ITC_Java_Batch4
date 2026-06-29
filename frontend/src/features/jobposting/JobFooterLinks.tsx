export function JobFooterLinks() {
  const links = ['About', 'Accessibility', 'Help Center', 'Advertising', 'Get the LinkedIn app', 'More'];
  
  return (
    <div className="mt-6 px-4 text-center">
      <div className="flex flex-wrap justify-center gap-x-3 gap-y-1.5 text-xs text-gray-500 font-medium">
        {links.slice(0, 3).map((link) => (
          <a key={link} href="#" className="hover:underline hover:text-blue-600">{link}</a>
        ))}
        
        <button className="flex items-center gap-0.5 hover:underline hover:text-blue-600">
          Privacy & Terms <span className="text-[10px]">▼</span>
        </button>
        
        <a href="#" className="hover:underline hover:text-blue-600">Ad Choices</a>
        {links.slice(3).map((link) => (
          <a key={link} href="#" className="hover:underline hover:text-blue-600">{link}</a>
        ))}
      </div>

      <div className="mt-4 flex items-center justify-center gap-1.5 text-xs text-gray-700 font-semibold">
        <span className="bg-blue-600 text-white text-[10px] px-1 rounded-sm font-black">in</span>
        <span>LinkedIn Corporation © 2026</span>
      </div>
    </div>
  );
}