import { X, Pencil, Trash2 } from "lucide-react";

interface ExperienceModalProps {
  isOpen: boolean;
  onClose: () => void;
  experiences: any[];
  onEditClick: (exp: any) => void;
  onDeleteClick?: (id: string) => void | Promise<void>;
}

export default function ExperienceModal({
  isOpen,
  onClose,
  experiences,
  onEditClick,
  onDeleteClick,
}: ExperienceModalProps) {
  if (!isOpen) return null;

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

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div className="flex max-h-[90vh] w-full max-w-3xl flex-col overflow-hidden rounded-xl bg-white shadow-xl">
        <div className="flex items-center justify-between border-b border-gray-200 px-6 py-4">
          <h2 className="text-xl font-bold text-gray-900">All Experience</h2>
          <button
            type="button"
            onClick={onClose}
            className="rounded-full p-1.5 text-gray-500 transition hover:bg-gray-100"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <div className="flex-1 space-y-0 divide-y divide-gray-200 overflow-y-auto p-6">
          {experiences && experiences.length > 0 ? (
            experiences.map((experience: any) => {
              const durationText = calculateDuration(
                experience.startDate,
                experience.endDate,
                experience.current
              );

              return (
                <div key={experience.id} className="group/modal-item flex gap-4 py-5 first:pt-0 last:pb-0">
                  <div className="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-md border border-gray-200 bg-gray-100">
                    <span className="font-bold text-gray-700">
                      {experience.companyName?.[0] || "?"}
                    </span>
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
                      <p className="mt-2 whitespace-pre-line text-sm leading-relaxed text-gray-600">
                        {experience.description}
                      </p>
                    )}
                  </div>

                  <div className="flex items-start gap-1 opacity-0 transition-opacity group-hover/modal-item:opacity-100">
                    <button
                      onClick={() => {
                        onClose();
                        onEditClick(experience);
                      }}
                      className="rounded-full p-1.5 text-gray-500 transition-colors hover:bg-blue-50 hover:text-blue-600"
                      title="Edit entry"
                    >
                      <Pencil className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => {
                        if (onDeleteClick) onDeleteClick(experience.id);
                      }}
                      className="rounded-full p-1.5 text-gray-500 transition-colors hover:bg-red-50 hover:text-red-600"
                      title="Delete entry"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              );
            })
          ) : (
            <p className="py-8 text-center text-sm text-gray-500">No experiences listed yet.</p>
          )}
        </div>
      </div>
    </div>
  );
}
