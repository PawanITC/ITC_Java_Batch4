import React, { useState } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import EducationFormModal from './EducationFormModal';

interface EducationProps {
  profile: any;
  onRefresh?: () => void; // Optional callback to re-fetch profile data after a change
}

export default function Education({ profile, onRefresh }: EducationProps) {
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [selectedEducation, setSelectedEducation] = useState<any>(null);
  
  const educationsList = profile?.educations || [];

  const handleAddClick = () => {
    setSelectedEducation(null); // Fresh form
    setIsFormModalOpen(true);
  };

  const handleEditClick = (edu: any) => {
    setSelectedEducation(edu); // Load existing values
    setIsFormModalOpen(true);
  };

  const handleDeleteClick = (id: string) => {
    if (window.confirm("Are you sure you want to delete this education history?")) {
      console.log("Submitting DELETE request for ID:", id);
      // Example Axios call:
      // axios.delete(`http://localhost:8083/api/education/${id}`).then(() => onRefresh?.());
    }
  };

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
      {/* Header */}
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-bold text-gray-900">Education</h2>
        <button 
          onClick={handleAddClick}
          className="p-1.5 text-gray-600 hover:bg-gray-100 rounded-full transition-colors"
          title="Add education"
        >
          <Plus className="w-6 h-6" />
        </button>
      </div>

      {/* List items */}
      <div className="space-y-6">
        {educationsList.length === 0 ? (
          <p className="text-gray-500 text-sm">No education history available.</p>
        ) : (
          educationsList.map((edu: any, index: number) => (
            <div key={edu.id || index} className="group/edu-item">
              <div className="flex gap-4 justify-between items-start">
                
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

                {/* Edit & Delete Action Buttons (visible on row hover) */}
                <div className="flex items-center gap-1 opacity-0 group-hover/edu-item:opacity-100 transition-opacity">
                  <button
                    onClick={() => handleEditClick(edu)}
                    className="p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-full transition-colors"
                    title="Edit education"
                  >
                    <Pencil className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleDeleteClick(edu.id)}
                    className="p-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-full transition-colors"
                    title="Delete education"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>

              </div>
              {index < educationsList.length - 1 && (
                <hr className="border-gray-100 mt-6" />
              )}
            </div>
          ))
        )}
      </div>

      {/* Form Validation Modal (Add / Edit) */}
      <EducationFormModal
        isOpen={isFormModalOpen}
        onClose={() => setIsFormModalOpen(false)}
        initialData={selectedEducation}
      />
    </div>
  );
}