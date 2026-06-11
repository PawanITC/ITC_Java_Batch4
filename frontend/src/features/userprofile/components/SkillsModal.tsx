import React from "react";
import { X } from "lucide-react";

interface SkillsModalProps {
  isOpen: boolean;
  onClose: () => void;
}

function SkillsModal({ isOpen, onClose }: SkillsModalProps) {
  if (!isOpen) return null;

  const skills = [
    "React.js",
    "Next.js",
    "TypeScript",
    "JavaScript",
    "Node.js",
    "Express.js",
    "MongoDB",
    "Redux Toolkit",
    "Tailwind CSS",
    "REST APIs",
    "Git & GitHub",
    "React Native",
  ];

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
      </div>
    </div>
  );
}

export default SkillsModal;