import { BriefcaseBusiness, Building2, MapPin, Pencil, ShieldCheck } from "lucide-react";
import type { ReactNode } from "react";
import type { Profile } from "../api";

type ProfileInfoProps = {
  profile: Profile | null;
  onEdit: () => void;
  canEdit?: boolean;
  actions?: ReactNode;
};

function ProfileInfo({ profile, onEdit, canEdit = true, actions }: ProfileInfoProps) {
  if (!profile) return null;

  const location = [profile.city, profile.country].filter(Boolean).join(", ");
  const headline = profile.headline || profile.currentPosition || "Complete your profile";

  return (
    <div className="flex justify-between bg-white p-8">
      <div className="flex-1">
        <div className="flex flex-wrap items-center gap-3">
          <h1 className="text-2xl font-semibold">
            {profile.firstName} {profile.lastName}
          </h1>

          {canEdit && (
            <button className="flex items-center gap-2 rounded-full border-2 border-dashed border-blue-600 px-5 py-1 font-semibold text-blue-700">
              <ShieldCheck size={15} />
              Add verification badge
            </button>
          )}
        </div>

        <p className="mt-2 text-xl text-gray-900">{headline}</p>

        {actions && <div className="mt-4 flex flex-wrap items-center gap-3">{actions}</div>}

        {location && (
          <div className="mt-2 flex items-center gap-2 text-xl text-gray-500">
            <MapPin size={18} />
            <span>{location}</span>
            <span>-</span>
            <button className="font-semibold text-blue-700">Contact info</button>
          </div>
        )}
      </div>

      <div className="ml-10 w-80 space-y-8">
        {canEdit && (
          <div className="flex justify-end">
            <button
              onClick={onEdit}
              className="rounded-full p-2 text-gray-600 transition-colors hover:bg-gray-100"
              title="Edit profile"
            >
              <Pencil size={20} />
            </button>
          </div>
        )}

        {profile.currentCompany && (
          <div className="flex items-center gap-4">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-gray-100 text-gray-600">
              <Building2 size={28} />
            </div>
            <h3 className="text-xl font-semibold">{profile.currentCompany}</h3>
          </div>
        )}

        {profile.currentPosition && (
          <div className="flex items-center gap-4">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-gray-100 text-gray-600">
              <BriefcaseBusiness size={28} />
            </div>
            <h3 className="text-xl font-semibold">{profile.currentPosition}</h3>
          </div>
        )}
      </div>
    </div>
  );
}

export default ProfileInfo;
