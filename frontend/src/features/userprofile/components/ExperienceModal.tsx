import React from 'react';

export default function ExperienceModal({ isOpen, onClose, experiences }:any) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4 animate-fadeIn">
      {/* Modal Container */}
      <div className="w-full max-w-3xl bg-white rounded-xl shadow-2xl flex flex-col max-h-[90vh]">
        
        {/* Header */}
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
            <h2 className="text-xl font-semibold text-gray-900">Experience</h2>
          </div>
          <button className="p-2 hover:bg-gray-100 rounded-full text-gray-600">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6">
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
            </svg>
          </button>
        </div>

        {/* Scrollable Experiences List */}
        <div className="flex-1 overflow-y-auto p-6 space-y-6 divide-y divide-gray-100">
          {experiences.map((exp:any, index:any) => (
            <div key={exp.id} className={`flex gap-4 ${index > 0 ? 'pt-6' : ''}`}>
              {/* Logo Box */}
              <div className="w-12 h-12 rounded-md bg-gray-100 flex-shrink-0 flex items-center justify-center overflow-hidden border border-gray-200">
                {exp.logoText ? (
                  <span className="text-xs font-bold text-gray-700">{exp.logoText}</span>
                ) : (
                  <div className={`w-full h-full flex items-center justify-center text-white font-bold ${exp.logoBg || 'bg-emerald-500'}`}>
                    {exp.company[0]}
                  </div>
                )}
              </div>

              {/* Details */}
              <div className="flex-1">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="text-base font-semibold text-gray-900">{exp.role}</h3>
                    <p className="text-sm text-gray-800">{exp.company} · {exp.type}</p>
                    <p className="text-xs text-gray-500 mt-0.5">{exp.duration} · {exp.period}</p>
                    {exp.location && <p className="text-xs text-gray-500">{exp.location} · {exp.workplaceType}</p>}
                  </div>
                  
                  {/* Edit Action Button */}
                  <button className="p-1.5 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-full transition">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-5 h-5">
                      <path strokeLinecap="round" strokeLinejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L6.832 19.82a4.5 4.5 0 01-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 011.13-1.897L16.863 4.487zm0 0L19.5 7.125" />
                    </svg>
                  </button>
                </div>

                {/* Optional LinkedIn Network Link */}
                {exp.linkedInHired && (
                  <div className="flex items-center gap-1.5 text-xs text-gray-600 mt-2">
                    <span className="bg-blue-600 text-white font-bold px-1 rounded-sm text-[10px]">in</span>
                    <span>helped me get this job</span>
                  </div>
                )}

                {/* Full Description */}
                {exp.description && (
                  <p className="text-sm text-gray-600 mt-3 leading-relaxed whitespace-pre-line">
                    {exp.description}
                  </p>
                )}

                {/* Skills Tag Indicator */}
                {exp.skillsBadge && (
                  <div className="flex items-center gap-2 mt-3 text-sm text-gray-700">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-4 h-4 text-gray-500">
                      <path strokeLinecap="round" strokeLinejoin="round" d="M11.48 3.499c.172-.435.744-.435.916 0l1.944 4.926 5.302.43c.472.039.66.618.321.92l-3.934 3.511.99 5.28 dynamic c.088.472-.416.839-.817.588L12 16.51l-4.706 2.504c-.401.251-.905-.116-.817-.588l.99-5.281L3.533 10.77c-.339-.302-.15-.882.321-.92l5.302-.43 1.944-4.927z" />
                    </svg>
                    <span className="font-medium text-gray-800">{exp.skillsBadge}</span>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}