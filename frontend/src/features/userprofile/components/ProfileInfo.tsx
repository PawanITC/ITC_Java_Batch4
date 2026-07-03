import { ShieldCheck, MapPin, Pencil } from "lucide-react";
import companyphoto from "../../../assets/profile.jpeg";
import type { Profile } from "../api";

type ProfileInfoProps = {
  profile: Profile | null;
  onEdit: () => void;
};

function ProfileInfo({ profile, onEdit }: ProfileInfoProps) {
  if (!profile) return null;

  return (
    <div className="bg-white p-8 flex justify-between">
      
      {/* LEFT */}
      <div className="flex-1">

        <div className="flex items-center gap-3 flex-wrap">
          <h1 className="text-2xl font-semibold">
            {profile.firstName} {profile.lastName}
          </h1>

          <span className="text-xl text-gray-600">
            He/Him
          </span>

          <button className="flex items-center gap-2 border-2 border-dashed border-blue-600 rounded-full px-5 py-1 text-blue-700 font-semibold">
            <ShieldCheck size={15} />
            Add verification badge
          </button>
        </div>

        <p className="text-xl mt-2 text-gray-900">
          {profile.headline}
        </p>

        <div className="flex items-center gap-2 mt-2 text-gray-500 text-xl">
          <MapPin size={18} />

          <span>
            {profile.city}, {profile.country}
          </span>

          <span>·</span>

          <button className="text-blue-700 font-semibold">
            Contact info
          </button>
        </div>

        <p className="text-blue-700 font-semibold text-xl mt-6">
          500+ connections
        </p>

      </div>

      {/* RIGHT */}
      <div className="w-80 ml-10 space-y-8">
        <div className="flex justify-end">
          <button
            onClick={onEdit}
            className="rounded-full p-2 text-gray-600 transition-colors hover:bg-gray-100"
            title="Edit profile"
          >
            <Pencil size={20} />
          </button>
        </div>
        
        <div className="flex items-center gap-4">
          <img src={companyphoto} alt="Company" className="w-16 h-16 rounded-full" />
          <h3 className="text-xl font-semibold">{profile.currentCompany || "Fiverr"}</h3>
        </div>

        <div className="flex items-center gap-4">
          <img src={companyphoto} alt="company" className="w-16 h-16 rounded-full" />
          <h3 className="text-xl font-semibold">
            {profile.currentPosition || "COMSATS University Islamabad"}
          </h3>
        </div>

      </div>
    </div>
  );
}

export default ProfileInfo;
