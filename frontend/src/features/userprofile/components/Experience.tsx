import React, { useState } from "react";
import { Plus, Pencil, Trash2 } from "lucide-react";
import ExperienceModal from "./ExperienceModal";
import AddExperienceModal from "./AddandEditExperienceModal"; // This will handle both Add & Edit now

export default function Experience({ experiences }: any) {
  const [isAllModalOpen, setIsAllModalOpen] = useState(false);
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [selectedExperience, setSelectedExperience] = useState<any>(null);
  const [expandedDescriptions, setExpandedDescriptions] = useState<Record<string, boolean>>({});

  // Helper function to calculate human-readable duration
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
    setExpandedDescriptions(prev => ({ ...prev, [id]: !prev[id] }));
  };

  // Trigger for adding a completely fresh role
  const handleAddClick = () => {
    setSelectedExperience(null); 
    setIsFormModalOpen(true);
  };

  // Trigger for editing an existing role
  const handleEditClick = (exp: any) => {
    setSelectedExperience(exp); 
    setIsFormModalOpen(true);
  };

  const dashboardPreviewItems = experiences?.slice(0, 3) || [];

  return (
    <div className="w-full mt-6 bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
      
      {/* Header */}
      <div className="p-6 flex justify-between items-center">
        <h2 className="text-xl font-bold text-gray-900">Experience</h2>
        <button 
          onClick={handleAddClick}
          className="p-1.5 text-gray-600 hover:bg-gray-100 rounded-full transition-colors"
          title="Add experience"
        >
          <Plus className="w-6 h-6" />
        </button>
      </div>

      <div className="divide-y px-6">
        {dashboardPreviewItems.map((exp: any) => {
          const durationText = calculateDuration(exp.startDate, exp.endDate, exp.current);
          
          return (
            <div key={exp.id} className="py-5 flex gap-4 group/item">

              <div className="w-12 h-12 bg-gray-100 flex items-center justify-center rounded-md border border-gray-200 flex-shrink-0">
                <span className="font-bold text-gray-700">
                  {exp.companyName?.[0] || "?"}
                </span>
              </div>

              <div className="flex-1">
                <h3 className="font-semibold text-gray-900">{exp.title}</h3>
                <p className="text-sm text-gray-700">{exp.companyName}</p>
                <p className="text-xs text-gray-500 mt-0.5">
                  {formatDisplayDate(exp.startDate)} – {exp.current ? "Present" : formatDisplayDate(exp.endDate)}
                  {durationText && ` · ${durationText}`}
                </p>

                {exp.description && (
                  <p className="text-sm mt-2 text-gray-600 leading-relaxed">
                    {expandedDescriptions[exp.id]
                      ? exp.description
                      : `${exp.description.slice(0, 120)}...`
                    }
                    {exp.description.length > 120 && (
                      <button
                        onClick={() => toggleDescription(exp.id)}
                        className="ml-2 text-blue-600 font-medium hover:underline text-xs"
                      >
                        {expandedDescriptions[exp.id] ? "less" : "more"}
                      </button>
                    )}
                  </p>
                )}
              </div>

              {/* Action Buttons */}
              <div className="flex items-start gap-1 opacity-0 group-hover/item:opacity-100 transition-opacity">
                <button
                  onClick={() => handleEditClick(exp)}
                  className="p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-full transition-colors"
                  title="Edit entry"
                >
                  <Pencil className="w-4 h-4" />
                </button>
                <button
                  onClick={() => console.log("Delete clicked for id: ", exp.id)}
                  className="p-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-full transition-colors"
                  title="Delete entry"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>

            </div>
          );
        })}
      </div>

      {experiences && experiences.length > 0 && (
        <button
          onClick={() => setIsAllModalOpen(true)}
          className="w-full border-t py-3 text-center text-sm font-semibold text-gray-600 hover:bg-gray-50 transition-colors"
        >
          Show all {experiences.length} experiences
        </button>
      )}

      {/* Flow 1: Old simple list view workflow */}
    <ExperienceModal
  isOpen={isAllModalOpen}
  onClose={() => setIsAllModalOpen(false)}
  experiences={experiences}
  onEditClick={handleEditClick} 
  onDeleteClick={(id: string) => console.log("Delete ID:", id)} // <-- Optional: add your delete handler here too
/>

      {/* Flow 2: Multi-purpose Form validation flow (Add & Edit) */}
      <AddExperienceModal
        isOpen={isFormModalOpen}
        onClose={() => setIsFormModalOpen(false)}
        initialData={selectedExperience}
      />
    </div>
  );
}