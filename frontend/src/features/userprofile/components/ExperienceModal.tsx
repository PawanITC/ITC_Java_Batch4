import { X, Pencil, Trash2 } from "lucide-react";

interface ExperienceModalProps {
  isOpen: boolean;
  onClose: () => void;
  experiences: any[];
  onEditClick: (exp: any) => void;
  onDeleteClick?: (id: string) => void;
}

export default function ExperienceModal({ 
  isOpen, 
  onClose, 
  experiences, 
  onEditClick,
  onDeleteClick 
}: ExperienceModalProps) {
  
  if (!isOpen) return null;

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

  // Helper to format string dates to human readable string (e.g., "Jan 2024")
  const formatDisplayDate = (dateStr: string) => {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    return date.toLocaleDateString("en-US", { month: "short", year: "numeric" });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div className="w-full max-w-3xl bg-white rounded-xl shadow-xl flex flex-col overflow-hidden max-h-[90vh]">
        
   
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <h2 className="text-xl font-bold text-gray-900">All Experience</h2>
          <button 
            type="button" 
            onClick={onClose} 
            className="p-1.5 hover:bg-gray-100 rounded-full text-gray-500 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>


        <div className="p-6 overflow-y-auto divide-y divide-gray-200 flex-1 space-y-0">
          {experiences && experiences.length > 0 ? (
            experiences.map((exp: any) => {
              const durationText = calculateDuration(exp.startDate, exp.endDate, exp.current);

              return (
                <div key={exp.id} className="py-5 flex gap-4 group/modal-item first:pt-0 last:pb-0">
                  
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
                      <p className="text-sm mt-2 text-gray-600 leading-relaxed whitespace-pre-line">
                        {exp.description}
                      </p>
                    )}
                  </div>

                  <div className="flex items-start gap-1 opacity-0 group-hover/modal-item:opacity-100 transition-opacity">
                    <button
                      onClick={() => {
                        onClose(); 
                        onEditClick(exp); 
                      }}
                      className="p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-full transition-colors"
                      title="Edit entry"
                    >
                      <Pencil className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => {
                        if (onDeleteClick) onDeleteClick(exp.id);
                        console.log("Delete clicked inside full modal view targeting ID:", exp.id);
                      }}
                      className="p-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-full transition-colors"
                      title="Delete entry"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>

                </div>
              );
            })
          ) : (
            <p className="text-center py-8 text-sm text-gray-500">No experiences listed yet.</p>
          )}
        </div>

      </div>
    </div>
  );
}