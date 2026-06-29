import React, { useState } from "react";
import { Plus, Pencil, Trash2 } from "lucide-react";
import { deleteEducation } from "../api";
import EducationFormModal from "./EducationFormModal";

interface EducationProps {
  profile: any;
  onRefresh?: () => Promise<void>;
}

export default function Education({ profile, onRefresh }: EducationProps) {
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [selectedEducation, setSelectedEducation] = useState<any>(null);

  const educationsList = profile?.educations || [];

  const handleAddClick = () => {
    setSelectedEducation(null);
    setIsFormModalOpen(true);
  };

  const handleEditClick = (education: any) => {
    setSelectedEducation(education);
    setIsFormModalOpen(true);
  };

  const handleDeleteClick = (id: string) => {
    if (!window.confirm("Are you sure you want to delete this education history?")) {
      return;
    }

    deleteEducation(id)
      .then(() => onRefresh?.())
      .catch((error) => {
        console.error(error);
        window.alert("Unable to delete the education entry.");
      });
  };

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-xl font-bold text-gray-900">Education</h2>
        <button
          onClick={handleAddClick}
          className="rounded-full p-1.5 text-gray-600 transition-colors hover:bg-gray-100"
          title="Add education"
        >
          <Plus className="h-6 w-6" />
        </button>
      </div>

      <div className="space-y-6">
        {educationsList.length === 0 ? (
          <p className="text-sm text-gray-500">No education history available.</p>
        ) : (
          educationsList.map((education: any, index: number) => (
            <div key={education.id || index} className="group/edu-item">
              <div className="flex items-start justify-between gap-4">
                <div className="flex gap-4">
                  <div className="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-xl bg-blue-900 p-1 text-center text-[10px] font-bold leading-tight text-white">
                    {education.schoolName
                      ?.split(" ")
                      .slice(0, 2)
                      .map((word: string) => word[0])
                      .join("")
                      .toUpperCase() || "EDU"}
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900">{education.schoolName}</h3>
                    <p className="text-sm text-gray-700">
                      {education.degree}, {education.fieldOfStudy}
                    </p>
                    <p className="mb-2 text-sm text-gray-500">
                      {education.startYear} - {education.endYear || "Present"}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-1 opacity-0 transition-opacity group-hover/edu-item:opacity-100">
                  <button
                    onClick={() => handleEditClick(education)}
                    className="rounded-full p-1.5 text-gray-500 transition-colors hover:bg-blue-50 hover:text-blue-600"
                    title="Edit education"
                  >
                    <Pencil className="h-4 w-4" />
                  </button>
                  <button
                    onClick={() => handleDeleteClick(education.id)}
                    className="rounded-full p-1.5 text-gray-500 transition-colors hover:bg-red-50 hover:text-red-600"
                    title="Delete education"
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                </div>
              </div>
              {index < educationsList.length - 1 && <hr className="mt-6 border-gray-100" />}
            </div>
          ))
        )}
      </div>

      <EducationFormModal
        isOpen={isFormModalOpen}
        onClose={() => setIsFormModalOpen(false)}
        initialData={selectedEducation}
        profileId={profile?.id}
        onSaved={async () => {
          await onRefresh?.();
        }}
      />
    </div>
  );
}
