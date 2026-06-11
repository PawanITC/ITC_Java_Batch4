import React, { useState } from 'react';
import SkillsModal from './SkillsModal';
import SkillsModal2 from './SkillsModal2';

export default function Education() {
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Separate array to dynamically inject more items inside the popup 
  const previewSkills = ['Tailwind CSS', 'Responsive Web Design'];
  const extraSkills = [
    'Software Quality',
    'Software Engineering Practices',
    'Teamwork',
    'Communication',
    'Web Testing',
    'Secure Messaging'
  ];
  const allSkills = [...previewSkills, ...extraSkills];

  return (
    <div className="min-h-screen mt-6 flex flex-col gap-6 items-center ">
      <div className="w-full flex flex-col gap-4">
        
        {/* Education Section */}
        <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-bold text-gray-900">Education</h2>
            <div className="flex gap-2 text-gray-600">
              <button className="p-1.5 hover:bg-gray-100 rounded-full"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6"><path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" /></svg></button>
              <button className="p-1.5 hover:bg-gray-100 rounded-full"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6"><path strokeLinecap="round" strokeLinejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L6.832 19.82a4.5 4.5 0 01-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 011.13-1.897L16.863 4.487zm0 0L19.5 7.125" /></svg></button>
            </div>
          </div>

          <div className="space-y-6">
            {/* Degree 1 */}
            <div className="flex gap-4">
              <div className="w-12 h-12 rounded-full bg-blue-900 flex-shrink-0 flex items-center justify-center text-white text-xs font-bold text-center p-1">COMSATS</div>
              <div>
                <h3 className="font-semibold text-gray-900">COMSATS University Islamabad</h3>
                <p className="text-gray-700 text-sm">Bachelor's degree, Computer Engineering</p>
                <p className="text-gray-500 text-sm mb-2">Sep 2018 – Jul 2022</p>
                <p className="text-gray-700 text-sm mb-2"><span className="font-medium">Activities and societies:</span> Football and Swimming</p>
                <p className="text-gray-600 text-sm leading-relaxed">Javascript developer with hands on experience in Web development with MERN Stack. Quick solution provider with strong concepts of core computer science and have excellent communication skills.</p>
              </div>
            </div>
            
            <hr className="border-gray-100" />

            {/* Degree 2 */}
            <div className="flex gap-4">
              <div className="w-12 h-12 rounded-full bg-blue-900 flex-shrink-0 flex items-center justify-center text-white text-xs font-bold text-center p-1">COMSATS</div>
              <div>
                <h3 className="font-semibold text-gray-900">COMSATS University Islamabad</h3>
                <p className="text-gray-700 text-sm">Bachelor of Science, Computer Engineering</p>
              </div>
            </div>
          </div>
        </div>

        {/* Skills Section */}
        <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
          <div className="p-6 pb-2 flex justify-between items-center">
            <h2 className="text-xl font-bold text-gray-900">Skills</h2>
            <div className="flex gap-2 text-gray-600">
              <button className="p-1.5 hover:bg-gray-100 rounded-full"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6"><path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" /></svg></button>
              <button className="p-1.5 hover:bg-gray-100 rounded-full"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6"><path strokeLinecap="round" strokeLinejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L6.832 19.82a4.5 4.5 0 01-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 011.13-1.897L16.863 4.487zm0 0L19.5 7.125" /></svg></button>
            </div>
          </div>

          {/* List Preview Skills */}
          <div className="divide-y divide-gray-100 px-6">
            {previewSkills.map((skill, index) => (
              <div key={index} className="py-4 font-semibold text-gray-800">
                {skill}
              </div>
            ))}
          </div>

          {/* Show All Button footer */}
          <button 
            onClick={() => setIsModalOpen(true)}
            className="w-full border-t border-gray-200 py-3 text-center text-gray-600 font-semibold hover:bg-gray-50 flex items-center justify-center gap-2 transition"
          >
            Show all 
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2.5} stroke="currentColor" className="w-4 h-4">
              <path strokeLinecap="round" strokeLinejoin="round" d="M13.5 4.5L21 12m0 0l-7.5 7.5M21 12H3" />
            </svg>
          </button>
        </div>

      </div>

      {/* Pop up Overlay Injector */}
      <SkillsModal2 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        skills={allSkills} 
      />
    </div>
  );
}