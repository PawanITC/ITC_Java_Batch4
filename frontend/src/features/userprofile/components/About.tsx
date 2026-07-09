import { useState } from "react";
import { ArrowRight, Gem, Pencil } from "lucide-react";
import SkillsModal from "./SkillsModal";
import type { Profile } from "../api";

type AboutProps = {
  profile?: Profile | null;
  onEdit?: () => void;
  canEdit?: boolean;
};

function About({ profile, onEdit, canEdit = true }: AboutProps) {
  const [openModal, setOpenModal] = useState(false);
  const topSkills =
    profile?.skills?.slice(0, 5).map((skill) => skill.skillName).filter(Boolean) || [];

  return (
    <>
      <div className="rounded-xl border bg-white p-8">
        <div className="flex items-start justify-between">
          <h2 className="text-3xl font-bold">About</h2>

          {canEdit && <Pencil size={22} className="cursor-pointer" onClick={onEdit} />}
        </div>

        <div className="mt-8 text-md leading-relaxed">
          {profile?.about?.trim() ? (
            <p>{profile.about}</p>
          ) : (
            <p className="text-gray-500">No about section added yet.</p>
          )}
        </div>

        {topSkills.length > 0 && (
          <div className="mt-8 rounded-2xl border p-8">
            <div className="flex items-center justify-between">
              <div className="flex gap-5">
                <Gem size={40} />

                <div>
                  <h3 className="text-2xl font-bold">Top skills</h3>
                  <p className="mt-1 text-xl">{topSkills.join(" - ")}</p>
                </div>
              </div>

              <ArrowRight
                size={25}
                className="cursor-pointer"
                onClick={() => setOpenModal(true)}
              />
            </div>
          </div>
        )}
      </div>

      <SkillsModal isOpen={openModal} onClose={() => setOpenModal(false)} skills={topSkills} />
    </>
  );
}

export default About;
