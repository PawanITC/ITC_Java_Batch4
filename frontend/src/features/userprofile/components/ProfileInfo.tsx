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
    <section className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm">
      <div className="grid gap-8 p-8 lg:grid-cols-[minmax(0,1fr)_320px]">
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-3">
            <h1 className="text-3xl font-semibold tracking-normal text-gray-950">
              {profile.firstName} {profile.lastName}
            </h1>

            {canEdit && (
              <button className="inline-flex items-center gap-2 rounded-full border border-dashed border-blue-600 px-4 py-1.5 text-sm font-semibold text-blue-700 transition hover:bg-blue-50">
                <ShieldCheck size={15} />
                Add verification badge
              </button>
            )}
          </div>

          <p className="mt-3 max-w-3xl text-lg leading-7 text-gray-900">{headline}</p>

          {location && (
            <div className="mt-4 flex flex-wrap items-center gap-2 text-base text-gray-600">
              <MapPin size={18} />
              <span>{location}</span>
              <span className="text-gray-300">-</span>
              <button className="font-semibold text-blue-700 hover:underline">Contact info</button>
            </div>
          )}

          {actions && <div className="mt-6 flex flex-wrap items-center gap-3">{actions}</div>}
        </div>

        <aside className="relative min-w-0 border-t border-gray-100 pt-6 lg:border-l lg:border-t-0 lg:pl-8 lg:pt-0">
          {canEdit && (
            <button
              onClick={onEdit}
              className="absolute right-0 top-0 rounded-full p-2 text-gray-600 transition-colors hover:bg-gray-100"
              title="Edit profile"
            >
              <Pencil size={20} />
            </button>
          )}

          <div className="space-y-5 pr-10">
            {profile.currentCompany && (
              <div className="flex items-center gap-4">
                <div className="flex h-12 w-12 flex-none items-center justify-center rounded-full bg-slate-100 text-slate-600">
                  <Building2 size={23} />
                </div>
                <h3 className="break-words text-lg font-semibold text-gray-950">
                  {profile.currentCompany}
                </h3>
              </div>
            )}

            {profile.currentPosition && (
              <div className="flex items-center gap-4">
                <div className="flex h-12 w-12 flex-none items-center justify-center rounded-full bg-slate-100 text-slate-600">
                  <BriefcaseBusiness size={23} />
                </div>
                <h3 className="break-words text-lg font-semibold text-gray-950">
                  {profile.currentPosition}
                </h3>
              </div>
            )}
          </div>
        </aside>
      </div>
    </section>
  );
}

export default ProfileInfo;
