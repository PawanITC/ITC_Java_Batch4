import React, { useState } from "react";
import { Plus, Pencil, Trash2 } from "lucide-react";
import { deleteExperience } from "../api";
import AddExperienceModal from "./AddandEditExperienceModal";
import ExperienceModal from "./ExperienceModal";

interface ExperienceProps {
  experiences: any[];
  profileId: string;
  onRefresh: () => Promise<void>;
}

export default function Experience({
  experiences,
  profileId,
  onRefresh,
}: ExperienceProps) {
  const [isAllModalOpen, setIsAllModalOpen] = useState(false);
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [selectedExperience, setSelectedExperience] = useState<any>(null);
  const [expandedDescriptions, setExpandedDescriptions] = useState<Record<string, boolean>>({});

  const calculateDuration = (startDateStr: string, endDateStr: string, isCurrent: boolean) => {
    if (!startDateStr) return "";

    const start = new Date(startDateStr);
    const end = isCurrent ? new Date() : new Date(endDateStr || startDateStr);

    let years = end.getFullYear() - start.getFullYear();
    let months = end.getMonth() - start.getMonth();

    if (months < 0) {
      years--;
      months += 12;
    }

    if (end.getDate() >= start.getDate()) {
      months++;
      if (months === 12) {
        years++;
        months = 0;
      }
    }

    const yearPart = years > 0 ? `${years} yr${years > 1 ? "s" : ""}` : "";
    const monthPart = months > 0 ? `${months} mo${months > 1 ? "s" : ""}` : "";

    return [yearPart, monthPart].filter(Boolean).join(" ");
  };

  const formatDisplayDate = (dateStr: string) => {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    return date.toLocaleDateString("en-US", { month: "short", year: "numeric" });
  };

  const toggleDescription = (id: string) => {
    setExpandedDescriptions((previous) => ({ ...previous, [id]: !previous[id] }));
  };

  const handleAddClick = () => {
    setSelectedExperience(null);
    setIsFormModalOpen(true);
  };

  const handleEditClick = (experience: any) => {
    setSelectedExperience(experience);
    setIsFormModalOpen(true);
  };

  const handleDelete = async (experienceId: string) => {
    try {
      await deleteExperience(experienceId);
      setIsAllModalOpen(false);
      await onRefresh();
    } catch (error) {
      console.error(error);
      window.alert("Unable to delete the experience entry.");
    }
  };

  const dashboardPreviewItems = experiences?.slice(0, 3) || [];

  return (
    <div className="mt-6 w-full overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
      <div className="flex items-center justify-between p-6">
        <h2 className="text-xl font-bold text-gray-900">Experience</h2>
        <button
          onClick={handleAddClick}
          className="rounded-full p-1.5 text-gray-600 transition-colors hover:bg-gray-100"
          title="Add experience"
        >
          <Plus className="h-6 w-6" />
        </button>
      </div>

      <div className="divide-y px-6">
        {dashboardPreviewItems.map((experience: any) => {
          const durationText = calculateDuration(
            experience.startDate,
            experience.endDate,
            experience.current
          );

          return (
            <div key={experience.id} className="group/item flex gap-4 py-5">
              <div className="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-md border border-gray-200 bg-gray-100">
                <span className="font-bold text-gray-700">{experience.companyName?.[0] || "?"}</span>
              </div>

              <div className="flex-1">
                <h3 className="font-semibold text-gray-900">{experience.title}</h3>
                <p className="text-sm text-gray-700">{experience.companyName}</p>
                <p className="mt-0.5 text-xs text-gray-500">
                  {formatDisplayDate(experience.startDate)} -{" "}
                  {experience.current ? "Present" : formatDisplayDate(experience.endDate)}
                  {durationText && ` | ${durationText}`}
                </p>

                {experience.description && (
                  <p className="mt-2 text-sm leading-relaxed text-gray-600">
                    {expandedDescriptions[experience.id]
                      ? experience.description
                      : `${experience.description.slice(0, 120)}...`}
                    {experience.description.length > 120 && (
                      <button
                        onClick={() => toggleDescription(experience.id)}
                        className="ml-2 text-xs font-medium text-blue-600 hover:underline"
                      >
                        {expandedDescriptions[experience.id] ? "less" : "more"}
                      </button>
                    )}
                  </p>
                )}
              </div>

              <div className="flex items-start gap-1 opacity-0 transition-opacity group-hover/item:opacity-100">
                <button
                  onClick={() => handleEditClick(experience)}
                  className="rounded-full p-1.5 text-gray-500 transition-colors hover:bg-blue-50 hover:text-blue-600"
                  title="Edit entry"
                >
                  <Pencil className="h-4 w-4" />
                </button>
                <button
                  onClick={async () => {
                    if (!window.confirm("Delete this experience entry?")) {
                      return;
                    }

                    await handleDelete(experience.id);
                  }}
                  className="rounded-full p-1.5 text-gray-500 transition-colors hover:bg-red-50 hover:text-red-600"
                  title="Delete entry"
                >
                  <Trash2 className="h-4 w-4" />
                </button>
              </div>
            </div>
          );
        })}
      </div>

      {experiences && experiences.length > 0 && (
        <button
          onClick={() => setIsAllModalOpen(true)}
          className="w-full border-t py-3 text-center text-sm font-semibold text-gray-600 transition-colors hover:bg-gray-50"
        >
          Show all {experiences.length} experiences
        </button>
      )}

      <ExperienceModal
        isOpen={isAllModalOpen}
        onClose={() => setIsAllModalOpen(false)}
        experiences={experiences}
        onEditClick={handleEditClick}
        onDeleteClick={handleDelete}
      />

      <AddExperienceModal
        isOpen={isFormModalOpen}
        onClose={() => setIsFormModalOpen(false)}
        initialData={selectedExperience}
        profileId={profileId}
        onSaved={onRefresh}
      />
    </div>
  );
}
