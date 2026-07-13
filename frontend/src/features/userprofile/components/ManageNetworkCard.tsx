import React, { useEffect, useState } from 'react';
import { Users, UserPlus, UsersRound, Calendar, Building2, Newspaper } from 'lucide-react';
import { NetworkModal } from './NetworkModal';
import { getCurrentProfile, getFollowersCount, getFollowingCount, Profile } from '../api';

interface NetworkItem {
  id: string;
  label: string;
  count?: number;
  icon: React.ComponentType<{ className?: string }>;
}

export const ManageNetworkCard: React.FC = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentProfile, setCurrentProfile] = useState<Profile | null>(null);
  const [followersCount, setFollowersCount] = useState<number>(0);
  const [followingCount, setFollowingCount] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);

  // Fetch current user and their counts
  const fetchNetworkData = async () => {
    try {
      setLoading(true);
      const profile = await getCurrentProfile();
      setCurrentProfile(profile);

      if (profile?.id) {
        const [followers, following] = await Promise.all([
          getFollowersCount(profile.id),
          getFollowingCount(profile.id),
        ]);
        setFollowersCount(followers);
        setFollowingCount(following);
      }
    } catch (error) {
      console.error("Error fetching network metrics:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNetworkData();
  }, []);

  const networkItems: NetworkItem[] = [
    { id: 'connections', label: 'Connections', count: 0, icon: Users }, // Replace 0 with an actual API endpoint count if available
    { id: 'following', label: 'Following & followers', icon: UserPlus },
    { id: 'groups', label: 'Groups', count: 0, icon: UsersRound },
    { id: 'events', label: 'Events', count: 0, icon: Calendar },
    { id: 'pages', label: 'Pages', count: 0, icon: Building2 },
    { id: 'newsletters', label: 'Newsletters', count: 0, icon: Newspaper },
  ];

  if (loading) {
    return <div className="w-[320px] bg-white border border-gray-200 rounded-xl p-4 animate-pulse text-gray-400">Loading network...</div>;
  }

  return (
    <>
      <div className="w-[320px] bg-white border border-gray-200 rounded-xl shadow-sm overflow-hidden font-sans">
        <div className="px-4 py-4 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900 tracking-wide">
            Manage my network
          </h2>
        </div>

        <ul className="divide-y divide-transparent">
          {networkItems.map((item) => {
            const IconComponent = item.icon;
            // Aggregate both follower and following metrics for this specific row display
            const displayCount = item.id === 'following' ? followersCount + followingCount : item.count;

            return (
              <li key={item.id}>
                <button
                  onClick={() => {
                    if (item.id === 'following') setIsModalOpen(true);
                  }}
                  className="w-full flex items-center justify-between px-4 py-3.5 text-gray-600 hover:bg-gray-50 transition-colors duration-150 group text-left focus:outline-none"
                >
                  <div className="flex items-center space-x-3.5">
                    <IconComponent className="w-5 h-5 text-gray-500 group-hover:text-gray-700 stroke-[2]" />
                    <span className="text-[15px] font-medium text-gray-700 group-hover:text-gray-900">
                      {item.label}
                    </span>
                  </div>

                  {displayCount !== undefined && displayCount > 0 && (
                    <span className="text-[15px] text-gray-500 font-normal">
                      {displayCount.toLocaleString()}
                    </span>
                  )}
                </button>
              </li>
            );
          })}
        </ul>
      </div>

      {currentProfile && (
        <NetworkModal
          isOpen={isModalOpen}
          onClose={() => {
            setIsModalOpen(false);
            fetchNetworkData(); // Refresh counts on card close in case user unfollowed someone
          }}
          currentProfile={currentProfile}
        />
      )}
    </>
  );
};