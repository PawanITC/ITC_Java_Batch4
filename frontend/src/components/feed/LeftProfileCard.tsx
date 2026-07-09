import {
  Bookmark,
  CalendarDays,
  Eye,
  Gem,
  Hash,
  Plus,
  TrendingUp,
  Users,
} from "lucide-react";
import { Link } from "react-router-dom";
import Avatar from "../common/Avatar";
import { Profile } from "../../features/userprofile/api";

type Props = {
  profile: Profile | null;
  loading?: boolean;
  missing?: boolean;
};

function displayName(profile: Profile | null) {
  if (!profile) return "Your profile";
  return `${profile.firstName} ${profile.lastName}`.trim() || "Your profile";
}

export default function LeftProfileCard({ profile, loading = false, missing = false }: Props) {
  const name = displayName(profile);
  const headline = profile?.headline || profile?.currentPosition || "Complete your profile";

  return (
    <aside className="sticky top-[68px] space-y-2">
      <div className="overflow-hidden rounded-lg border border-[#d6d6d6] bg-white shadow-[0_1px_2px_rgba(0,0,0,0.08)]">
        <div className="relative h-[116px] bg-white">
          <div className="absolute inset-x-0 top-0 h-20 overflow-hidden bg-[#0a66c2]">
            {profile?.coverPhotoUrl ? (
              <img
                src={profile.coverPhotoUrl}
                alt=""
                className="absolute inset-0 h-full w-full object-cover"
              />
            ) : (
              <div className="absolute inset-0 bg-[radial-gradient(circle_at_18%_20%,rgba(255,255,255,0.28)_0,rgba(255,255,255,0.28)_12%,transparent_13%),radial-gradient(circle_at_78%_28%,rgba(255,255,255,0.18)_0,rgba(255,255,255,0.18)_10%,transparent_11%),linear-gradient(135deg,#0a66c2_0%,#004182_100%)]" />
            )}
            <div className="absolute bottom-0 left-0 right-0 h-5 bg-gradient-to-t from-black/10 to-transparent" />
          </div>

          <div className="absolute bottom-0 left-1/2 z-10 -translate-x-1/2 rounded-full border-4 border-white bg-white shadow-sm">
            <Avatar
              name={name}
              src={profile?.profilePictureUrl}
              sizeClassName="h-20 w-20"
              textClassName="text-lg"
            />
          </div>
        </div>

        <div className="px-3 pb-4 text-center">
          <Link
            to="/profile"
            className="mt-3 block text-base font-semibold hover:underline"
          >
            {loading ? "Loading profile..." : name}
          </Link>
          <p className="mt-1 text-xs leading-5 text-gray-500">
            {loading ? "Checking your profile" : headline}
          </p>
          {missing && (
            <Link
              to="/profile"
              className="mt-3 inline-flex rounded-full bg-[#0a66c2] px-4 py-1.5 text-xs font-semibold text-white hover:bg-[#004182]"
            >
              Complete profile
            </Link>
          )}
        </div>

        <div className="border-t border-[#edf0f3] py-2 text-xs">
          <button className="flex w-full items-center justify-between px-3 py-1.5 text-left hover:bg-gray-100">
            <span className="flex items-center gap-2 text-gray-500">
              <Eye size={15} className="text-[#0a66c2]" />
              Profile viewers
            </span>
            <span className="font-semibold text-[#0a66c2]">128</span>
          </button>
          <button className="flex w-full items-center justify-between px-3 py-1.5 text-left hover:bg-gray-100">
            <span className="flex items-center gap-2 text-gray-500">
              <TrendingUp size={15} className="text-[#0a66c2]" />
              Post impressions
            </span>
            <span className="font-semibold text-[#0a66c2]">1,420</span>
          </button>
        </div>

        <button className="flex w-full items-start gap-2 border-t border-[#edf0f3] px-3 py-3 text-left text-xs hover:bg-gray-100">
          <Gem size={16} className="mt-0.5 text-[#915907]" />
          <span>
            <span className="block text-gray-500">Access exclusive tools</span>
            <span className="font-semibold text-gray-900">
              Try Premium for free
            </span>
          </span>
        </button>

        <button className="flex w-full items-center gap-2 border-t border-[#edf0f3] px-3 py-3 text-left text-xs font-semibold text-gray-700 hover:bg-gray-100">
          <Bookmark size={16} />
          Saved items
        </button>
      </div>

      <div className="overflow-hidden rounded-lg border border-[#d6d6d6] bg-white py-2 text-sm shadow-[0_1px_2px_rgba(0,0,0,0.08)]">
        <button className="flex w-full items-center justify-between px-3 py-2 text-left font-semibold text-[#0a66c2] hover:bg-gray-100">
          Groups
          <Plus size={16} className="text-gray-500" />
        </button>
        <button className="flex w-full items-center gap-2 px-3 py-2 text-left font-semibold text-[#0a66c2] hover:bg-gray-100">
          <CalendarDays size={16} />
          Events
        </button>
        <button className="flex w-full items-center gap-2 px-3 py-2 text-left font-semibold text-[#0a66c2] hover:bg-gray-100">
          <Hash size={16} />
          Followed Hashtags
        </button>
        <button className="flex w-full items-center gap-2 border-t border-[#edf0f3] px-3 py-3 text-left text-sm font-semibold text-gray-600 hover:bg-gray-100">
          <Users size={16} />
          Discover more
        </button>
      </div>
    </aside>
  );
}
