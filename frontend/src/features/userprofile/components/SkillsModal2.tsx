import React, { useState } from 'react';

export default function SkillsModal2({ isOpen, onClose, skills }: any) {
  const [activeTab, setActiveTab] = useState('All');
  
  if (!isOpen) return null;

  const categories = ['All'];

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">

      <div className="w-full max-w-2xl bg-white rounded-xl shadow-xl flex flex-col max-h-[85vh]">
        
   
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <div className="flex items-center gap-4">
            <button 
              onClick={onClose}
              className="p-2 hover:bg-gray-100 rounded-full transition text-gray-600"
            >
    
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6">
                <path strokeLinecap="round" strokeLinejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" />
              </svg>
            </button>
            <h2 className="text-xl font-semibold text-gray-900">Skills</h2>
          </div>
          
          <div className="flex items-center gap-2">
           
            <button className="p-2 hover:bg-gray-100 rounded-full text-gray-600">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6">
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
              </svg>
            </button>
          </div>
        </div>


        <div className="flex flex-wrap gap-2 px-6 py-3 border-b border-gray-100 overflow-x-auto">
          {categories.map((category) => (
            <button
              key={category}
              onClick={() => setActiveTab(category)}
              className={`px-4 py-1.5 rounded-full text-sm font-medium border transition whitespace-nowrap ${
                activeTab === category
                  ? 'bg-emerald-800 text-white border-emerald-800'
                  : 'bg-white text-gray-600 border-gray-300 hover:bg-gray-50'
              }`}
            >
              {category}
            </button>
          ))}
        </div>


        <div className="flex-1 overflow-y-auto divide-y divide-gray-100">
          {skills.map((skill: any) => (
            <div key={skill.id} className="flex items-center justify-between px-6 py-4 hover:bg-gray-50 group transition">
              <div className="flex flex-col">
                <span className="text-gray-900 font-medium text-base">{skill.skillName}</span>
                {skill.endorsementCount > 0 && (
                  <span className="text-xs text-gray-500 mt-0.5">
                    {skill.endorsementCount} endorsements
                  </span>
                )}
              </div>
              <button className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-200 rounded-full transition">
                {/* Edit Pencil Icon */}
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-5 h-5">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L6.832 19.82a4.5 4.5 0 01-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 011.13-1.897L16.863 4.487zm0 0L19.5 7.125" />
                </svg>
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}