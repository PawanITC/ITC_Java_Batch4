import React, { useState } from "react";
import { Plus, ArrowRight, Pencil, Trash2 } from "lucide-react";
import { deleteSkill } from "../api";
import SkillsModal2 from "./SkillsModal2";
import SkillFormModal from "./SkillFormModal";

interface SkillsProps {
  skills: any[];
  profileId: string;
  onRefresh: () => Promise<void>;
  canEdit?: boolean;
}

export default function Skills({ skills, profileId, onRefresh, canEdit = true }: SkillsProps) {
  const [isListModalOpen, setIsListModalOpen] = useState(false);
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [selectedSkill, setSelectedSkill] = useState<any>(null);

  const previewSkills = skills?.slice(0, 3) || [];

  const handleAddClick = () => {
    setSelectedSkill(null); // Clear fields for a new input
    setIsFormModalOpen(true);
  };

  const handleEditClick = (skill: any) => {
    setSelectedSkill(skill); // Load active field values
    setIsFormModalOpen(true);
  };

  const handleDelete = async (id: string) => {
    try {
      await deleteSkill(id);
      await onRefresh();
    } catch (error) {
      console.error(error);
      window.alert("Unable to delete the skill.");
    }
  };

  return (
    <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
      
      {/* Header */}
      <div className="p-6 pb-2 flex justify-between items-center">
        <h2 className="text-xl font-bold text-gray-900">Skills</h2>
        {canEdit && (
          <button 
            onClick={handleAddClick}
            className="p-1.5 text-gray-600 hover:bg-gray-100 rounded-full transition-colors"
            title="Add skill"
          >
            <Plus className="w-6 h-6" />
          </button>
        )}
      </div>

      {/* Main dashboard list container */}
      <div className="divide-y divide-gray-100 px-6">
        {previewSkills.length === 0 ? (
          <div className="py-4 text-sm text-gray-500">No skills added yet.</div>
        ) : (
          previewSkills.map((skill: any) => (
            <div key={skill.id} className="py-4 flex justify-between items-center group/skill-dash">
              
              <div className="flex flex-col">
                <span className="font-semibold text-gray-800">{skill.skillName}</span>
                {skill.endorsementCount > 0 && (
                  <span className="text-xs font-normal text-gray-500 bg-gray-100 px-2 py-0.5 rounded-full mt-1 w-max">
                    {skill.endorsementCount} endorsements
                  </span>
                )}
              </div>

              {/* Quick actions direct from main screen display section */}
              {canEdit && (
                <div className="flex items-center gap-1 opacity-0 group-hover/skill-dash:opacity-100 transition-opacity">
                  <button
                    onClick={() => handleEditClick(skill)}
                    className="p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-full transition-colors"
                  >
                    <Pencil className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => {
                      if (window.confirm(`Delete ${skill.skillName}?`)) handleDelete(skill.id);
                    }}
                    className="p-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-full transition-colors"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              )}

            </div>
          ))
        )}
      </div>

      {/* Footer view trigger button */}
      {skills && skills.length > 0 && (
        <button 
          onClick={() => setIsListModalOpen(true)}
          className="w-full border-t border-gray-200 py-3 text-center text-sm text-gray-600 font-semibold hover:bg-gray-50 flex items-center justify-center gap-2 transition-colors"
        >
          Show all ({skills.length})
          <ArrowRight className="w-4 h-4 text-gray-600 stroke-[2.5]" />
        </button>
      )}

      {/* Flow 1: Full skills manager popup modal */}
      <SkillsModal2 
        isOpen={isListModalOpen} 
        onClose={() => setIsListModalOpen(false)} 
        skills={skills} 
        onEditClick={handleEditClick}
        onDeleteClick={handleDelete}
        canEdit={canEdit}
      />

      {/* Flow 2: Formik + Yup isolated validation creation layout */}
      <SkillFormModal
        isOpen={isFormModalOpen}
        onClose={() => setIsFormModalOpen(false)}
        initialData={selectedSkill}
        profileId={profileId}
        onSaved={onRefresh}
      />

    </div>
  );
}
