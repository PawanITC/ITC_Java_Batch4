import React from "react";
import { X } from "lucide-react";

interface SkillsModalProps {
  isOpen: boolean;
  onClose: () => void;
  skills: string[];
}

function SkillsModal({ isOpen, onClose, skills }: SkillsModalProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white w-[500px] rounded-xl p-6 relative">
        <button
          onClick={onClose}
          className="absolute right-4 top-4"
        >
          <X />
        </button>

        <h2 className="text-2xl font-bold mb-5">
          Top Skills
        </h2>

        {skills.length > 0 ? (
          <div className="flex flex-wrap gap-3">
            {skills.map((skill) => (
              <span
                key={skill}
                className="px-4 py-2 bg-gray-100 rounded-full"
              >
                {skill}
              </span>
            ))}
          </div>
        ) : (
          <p className="text-gray-500">No skills added yet.</p>
        )}
      </div>
    </div>
  );
}

export default SkillsModal;
