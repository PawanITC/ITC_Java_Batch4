import React, { useState } from 'react';
import ExperienceModal from './ExperienceModal';

// Define an interface for the experience structure
interface ExperienceItem {
  id: number;
  role: string;
  company: string;
  type: string;
  duration: string;
  period: string;
  location?: string;
  workplaceType?: string;
  linkedInHired?: boolean;
  skillsBadge?: string;
  logoText?: string;
  logoBg?: string;
  description?: string;
}

export default function Experience() {
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  
  // FIXED: Explicitly typed dictionary state
  const [expandedDescriptions, setExpandedDescriptions] = useState<Record<number, boolean>>({});

  const initialExperiences: ExperienceItem[] = [
    {
      id: 1,
      role: 'React Developer',
      company: 'Fiverr',
      type: 'Freelance',
      duration: 'Dec 2021 - Present',
      period: '4 yrs 7 mos',
      logoBg: 'bg-emerald-500',
    },
    {
      id: 2,
      role: 'Associate Software Engineer',
      company: 'Futurenostics',
      type: 'Full-time',
      duration: 'Aug 2023 - Apr 2025',
      period: '1 yr 9 mos',
      location: 'Islamabad, Islāmābād, Pakistan',
      workplaceType: 'On-site',
      linkedInHired: true,
      skillsBadge: 'React.js, Next.js and +14 skills',
      logoText: 'F',
      logoBg: 'bg-teal-700'
    },
    {
      id: 3,
      role: 'Associate Software Engineer',
      company: 'Texinity Technologies',
      type: 'Full-time',
      duration: 'Sep 2022 - Jul 2023',
      period: '11 mos',
      description: 'Developed robust and scalable web applications, following best practices in software development, to meet the needs of the business and end-users. Conducted thorough testing and debugging of applications, ensuring high-quality and bug-free code. Resolved technical issues and provided troubleshooting support, improving the overall performance and stability of the applications.',
      logoText: 'TT',
      logoBg: 'bg-blue-300'
    },
    {
      id: 4,
      role: 'Back end intern',
      company: 'syntecX',
      type: 'Full-time',
      duration: 'Jul 2022 - Oct 2022',
      period: '4 mos',
      location: 'Islāmābād, Pakistan',
      skillsBadge: 'JavaScript, AngularJS and +3 skills',
      logoText: 'syntecX',
    },
    {
      id: 5,
      role: 'Software Engineer Intern',
      company: 'SyntecX Solutions',
      type: 'Internship',
      duration: 'Jun 2022 - Sep 2022',
      period: '4 mos',
      description: 'Given the responsibility of developing REST APIs for company product Voter.pk. Core functionalities involved Node.js, Express with information stored with MySQL queries on a remote server database. Learned software engineering process improvements and best practices. Collaborated with other developers to identify and alleviate a number of bugs and errors in software.',
      logoText: 'S'
    }
  ];

  // FIXED: Explicitly typed id parameter
  const toggleDescription = (id: number) => {
    setExpandedDescriptions(prev => ({ ...prev, [id]: !prev[id] }));
  };

  const dashboardPreviewItems = initialExperiences.slice(0, 3);

  return (
    <div className="w-full mt-6 bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden font-sans">
      
      {/* Container Header */}
      <div className="p-6 pb-2 flex justify-between items-center">
        <h2 className="text-xl font-bold text-gray-900">Experience</h2>
        <div className="flex gap-2 text-gray-600">
          <button className="p-1.5 hover:bg-gray-100 rounded-full transition"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6"><path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" /></svg></button>
          <button className="p-1.5 hover:bg-gray-100 rounded-full transition"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6"><path strokeLinecap="round" strokeLinejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L6.832 19.82a4.5 4.5 0 01-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 011.13-1.897L16.863 4.487zm0 0L19.5 7.125" /></svg></button>
        </div>
      </div>

      {/* Main Experience Render */}
      <div className="divide-y divide-gray-100 px-6">
        {dashboardPreviewItems.map((exp) => {
          const isExpanded = !!expandedDescriptions[exp.id];
          const hasLongDescription = exp.description && exp.description.length > 140;
          
          return (
            <div key={exp.id} className="py-5 flex gap-4">
              <div className="w-12 h-12 rounded-md bg-gray-50 border border-gray-200 flex-shrink-0 flex items-center justify-center overflow-hidden">
                {exp.logoText ? (
                  <span className="text-xs font-black tracking-tighter text-gray-700">{exp.logoText}</span>
                ) : (
                  <div className={`w-full h-full flex items-center justify-center text-white font-bold text-lg ${exp.logoBg || 'bg-emerald-500'}`}>
                    {exp.company[0]}
                  </div>
                )}
              </div>

              <div className="flex-1">
                <h3 className="text-base font-semibold text-gray-900 leading-tight">{exp.role}</h3>
                <p className="text-sm text-gray-800 mt-0.5">{exp.company} · {exp.type}</p>
                <p className="text-xs text-gray-500 mt-0.5">{exp.duration} · {exp.period}</p>
                {exp.location && <p className="text-xs text-gray-500">{exp.location} · {exp.workplaceType}</p>}

                {exp.linkedInHired && (
                  <div className="flex items-center gap-1.5 text-xs text-gray-600 mt-2">
                    <span className="bg-blue-600 text-white font-bold px-1 rounded-sm text-[10px]">in</span>
                    <span>helped me get this job</span>
                  </div>
                )}

                {exp.description && (
                  <div className="text-sm text-gray-600 mt-2.5 leading-relaxed">
                    {hasLongDescription && !isExpanded ? (
                      <p>
                        {exp.description.slice(0, 140)}...
                        <button 
                          onClick={() => toggleDescription(exp.id)} 
                          className="text-gray-500 font-semibold hover:text-blue-600 ml-1 hover:underline"
                        >
                          more
                        </button>
                      </p>
                    ) : (
                      <p className="whitespace-pre-line">
                        {exp.description}
                        {hasLongDescription && (
                          <button 
                            onClick={() => toggleDescription(exp.id)} 
                            className="text-gray-500 font-semibold hover:text-blue-600 ml-2 hover:underline text-xs"
                          >
                            show less
                          </button>
                        )}
                      </p>
                    )}
                  </div>
                )}

                {exp.skillsBadge && (
                  <div className="flex items-center gap-2 mt-3 text-sm text-gray-700">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-4 h-4 text-gray-400">
                      <path strokeLinecap="round" strokeLinejoin="round" d="M11.48 3.499c.172-.435.744-.435.916 0l1.944 4.926 5.302.43c.472.039.66.618.321.92l-3.934 3.511.99 5.28c.088.472-.416.839-.817.588L12 16.51l-4.706 2.504c-.401.251-.905-.116-.817-.588l.99-5.281L3.533 10.77c-.339-.302-.15-.882.321-.92l5.302-.43 1.944-4.927z" />
                    </svg>
                    <span className="font-medium text-gray-800">{exp.skillsBadge}</span>
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>

      {/* Show All Footer */}
      <button 
        onClick={() => setIsModalOpen(true)}
        className="w-full border-t border-gray-100 py-3.5 text-center text-gray-600 font-semibold hover:bg-gray-50 flex items-center justify-center gap-1.5 transition text-sm"
      >
        Show all {initialExperiences.length} experiences
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2.5} stroke="currentColor" className="w-4 h-4">
          <path strokeLinecap="round" strokeLinejoin="round" d="M13.5 4.5L21 12m0 0l-7.5 7.5M21 12H3" />
        </svg>
      </button>

      <ExperienceModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        experiences={initialExperiences} 
      />
    </div>
  );
}