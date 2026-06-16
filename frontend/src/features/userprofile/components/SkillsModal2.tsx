import React from "react";
import { X, Pencil, Trash2 } from "lucide-react";

interface SkillsModal2Props {
  isOpen: boolean;
  onClose: () => void;
  skills: any[];
  onEditClick: (skill: any) => void;
  onDeleteClick: (id: string) => void;
}

export default function SkillsModal2({ 
  isOpen, 
  onClose, 
  skills, 
  onEditClick, 
  onDeleteClick 
}: SkillsModal2Props) {
  
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div className="w-full max-w-2xl bg-white rounded-xl shadow-xl flex flex-col overflow-hidden max-h-[90vh]">
        
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <h2 className="text-xl font-bold text-gray-900">Skills</h2>
          <button 
            onClick={onClose} 
            className="p-1.5 hover:bg-gray-100 rounded-full text-gray-500 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Scrollable Skill List Container */}
        <div className="p-6 overflow-y-auto divide-y divide-gray-200 flex-1">
          {skills && skills.length > 0 ? (
            skills.map((skill: any) => (
              <div key={skill.id} className="py-4 flex justify-between items-center group/skill-row first:pt-0 last:pb-0">
                
                <div className="flex flex-col">
                  <span className="font-semibold text-gray-800">{skill.skillName}</span>
                  {skill.endorsementCount > 0 && (
                    <span className="text-xs text-gray-500 mt-0.5">
                      {skill.endorsementCount} endorsements
                    </span>
                  )}
                </div>

                {/* Edit & Delete Actions */}
                <div className="flex items-center gap-1 opacity-0 group-hover/skill-row:opacity-100 transition-opacity">
                  <button
                    onClick={() => {
                      onClose(); // Close the "Show All" modal view
                      onEditClick(skill); // Open the Edit Form modal
                    }}
                    className="p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-full transition-colors"
                    title="Edit skill"
                  >
                    <Pencil className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => {
                      if (window.confirm(`Are you sure you want to delete ${skill.skillName}?`)) {
                        onDeleteClick(skill.id);
                      }
                    }}
                    className="p-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-full transition-colors"
                    title="Delete skill"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>

              </div>
            ))
          ) : (
            <p className="text-center py-6 text-sm text-gray-500">No skills added yet.</p>
          )}
        </div>

      </div>
    </div>
  );
}