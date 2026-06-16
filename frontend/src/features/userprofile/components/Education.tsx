import React, { useState } from 'react';
import SkillsModal2 from './SkillsModal2';

export default function Education({ profile }: any) {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const educationsList = profile?.educations || [];
  const skillsList = profile?.skills || [];

  const previewSkills = skillsList.slice(0, 3);

  return (
    <div className="w-full mt-6 flex flex-col gap-6 items-center pb-24">
      <div className="w-full flex flex-col gap-4">
        
        {/* Education Section */}
        <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-bold text-gray-900">Education</h2>
            <div className="flex gap-2 text-gray-600">
              <button className="p-1.5 hover:bg-gray-100 rounded-full">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6"><path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" /></svg>
              </button>
              <button className="p-1.5 hover:bg-gray-100 rounded-full">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6"><path strokeLinecap="round" strokeLinejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L6.832 19.82a4.5 4.5 0 01-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 011.13-1.897L16.863 4.487zm0 0L19.5 7.125" /></svg>
              </button>
            </div>
          </div>

          <div className="space-y-6">
            {educationsList.length === 0 ? (
              <p className="text-gray-500 text-sm">No education history available.</p>
            ) : (
              educationsList.map((edu: any, index: number) => (
                <div key={edu.id}>
                  <div className="flex gap-4">
                    {/* Generates placeholder initials from the school name */}
                    <div className="w-12 h-12 rounded-xl bg-blue-900 flex-shrink-0 flex items-center justify-center text-white text-[10px] font-bold text-center p-1 break-words leading-tight">
                      {edu.schoolName?.split(" ").slice(0, 2).map((w: string) => w[0]).join("").toUpperCase() || "EDU"}
                    </div>
                    <div>
                      <h3 className="font-semibold text-gray-900">{edu.schoolName}</h3>
                      <p className="text-gray-700 text-sm">
                        {edu.degree}, {edu.fieldOfStudy}
                      </p>
                      <p className="text-gray-500 text-sm mb-2">
                        {edu.startYear} – {edu.endYear || "Present"}
                      </p>
                    </div>
                  </div>
                  {index < educationsList.length - 1 && (
                    <hr className="border-gray-100 mt-6" />
                  )}
                </div>
              ))
            )}
          </div>
        </div>

        {/* Skills Section */}
        <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
          <div className="p-6 pb-2 flex justify-between items-center">
            <h2 className="text-xl font-bold text-gray-900">Skills</h2>
            <div className="flex gap-2 text-gray-600">
              <button className="p-1.5 hover:bg-gray-100 rounded-full">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6"><path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" /></svg>
              </button>
              <button className="p-1.5 hover:bg-gray-100 rounded-full">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6"><path strokeLinecap="round" strokeLinejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L6.832 19.82a4.5 4.5 0 01-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 011.13-1.897L16.863 4.487zm0 0L19.5 7.125" /></svg>
              </button>
            </div>
          </div>

          <div className="divide-y divide-gray-100 px-6">
            {previewSkills.length === 0 ? (
              <div className="py-4 text-sm text-gray-500">No skills added yet.</div>
            ) : (
              previewSkills.map((skill: any) => (
                <div key={skill.id} className="py-4 font-semibold text-gray-800 flex justify-between items-center">
                  <span>{skill.skillName}</span>
                  {skill.endorsementCount > 0 && (
                    <span className="text-xs font-normal text-gray-500 bg-gray-100 px-2 py-0.5 rounded-full">
                    {skill.endorsementCount} endorsements
                    </span>
                  )}
                </div>
              ))
            )}
          </div>

          {skillsList.length > 0 && (
            <button 
              onClick={() => setIsModalOpen(true)}
              className="w-full border-t border-gray-200 py-3 text-center text-gray-600 font-semibold hover:bg-gray-50 flex items-center justify-center gap-2 transition"
            >
              Show all ({skillsList.length})
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2.5} stroke="currentColor" className="w-4 h-4">
                <path strokeLinecap="round" strokeLinejoin="round" d="M13.5 4.5L21 12m0 0l-7.5 7.5M21 12H3" />
              </svg>
            </button>
          )}
        </div>

      </div>

  
      <SkillsModal2 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        skills={skillsList} 
      />
    </div>
  );
}