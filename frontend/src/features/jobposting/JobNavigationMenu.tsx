import { useState } from 'react';
import JobPostModal from './JobPostModal';

interface JobNavigationMenuProps {
  onJobAdded: () => void;
}

export function JobNavigationMenu({ onJobAdded }: JobNavigationMenuProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);

  return (
    <div className="bg-white rounded-xl border border-gray-200 overflow-hidden shadow-sm">
      <div className="p-4 space-y-5">
        <button className="flex items-center gap-3 w-full text-left group">
          <svg className="w-6 h-6 text-gray-700" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
          <span className="font-bold text-gray-800 text-[15px] group-hover:underline">Preferences</span>
        </button>

        <button className="flex items-center gap-3 w-full text-left group">
          <svg className="w-6 h-6 text-gray-800 fill-current" viewBox="0 0 24 24">
            <path d="M5 3c-1.1 0-2 .9-2 2v16l9-4 9 4V5c0-1.1-.9-2-2-2H5z" />
          </svg>
          <span className="font-bold text-gray-800 text-[15px] group-hover:underline">Job tracker</span>
        </button>

        <button className="flex items-center gap-3 w-full text-left group">
          <div className="w-5 h-5 bg-amber-600 rounded-sm transform rotate-12 flex-shrink-0"></div>
          <span className="font-bold text-gray-800 text-[15px] group-hover:underline">My Career Insights</span>
        </button>
      </div>

      <hr className="border-gray-200" />

      <div className="p-4">
        <button onClick={() => setIsModalOpen(true)} className="flex items-center gap-3 w-full text-left group text-blue-600">
          <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
          </svg>
          <span className="font-bold text-[15px] group-hover:underline">Post a job</span>
        </button>
      </div>

      <div className="bg-gray-100/80 p-4 border-t border-gray-200">
        <button className="flex items-center gap-3 w-full text-left group">
          <svg className="w-6 h-6 text-gray-800 fill-current" viewBox="0 0 24 24">
            <path d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z" />
          </svg>
          <span className="font-bold text-gray-800 text-[15px] group-hover:underline">Manage job posts</span>
        </button>
      </div>

      <JobPostModal
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        onSubmitSuccess={onJobAdded} 
      />
    </div>
  );
}