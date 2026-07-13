import React, { useState, useEffect, useCallback } from 'react';
import { X } from 'lucide-react';
import { getFollowers, getFollowing, Profile, toggleFollowUser } from '../api';

interface NetworkModalProps {
  isOpen: boolean;
  onClose: () => void;
  currentProfile: Profile; // Logged-in user's profile information passed from parent
}

export const NetworkModal: React.FC<NetworkModalProps> = ({
  isOpen,
  onClose,
  currentProfile,
}) => {
  const [activeTab, setActiveTab] = useState<'following' | 'followers'>('following');
  const [people, setPeople] = useState<Profile[]>([]);
  const [followingIds, setFollowingIds] = useState<Set<string>>(new Set());
  const [loading, setLoading] = useState<boolean>(false);

  // Fetch the lists from the API based on the selected tab
  const fetchList = useCallback(async () => {
    if (!currentProfile?.id) return;
    try {
      setLoading(true);
      
      // We always want to know who the user is currently following to compute button states accurately
      const cleanFollowingList = await getFollowing(currentProfile.id);
      const followingSet = new Set(cleanFollowingList.map(p => p.id));
      setFollowingIds(followingSet);

      if (activeTab === 'following') {
        setPeople(cleanFollowingList);
      } else {
        const followersList = await getFollowers(currentProfile.id);
        setPeople(followersList);
      }
    } catch (error) {
      console.error(`Failed to fetch ${activeTab} list:`, error);
    } finally {
      setLoading(false);
    }
  }, [activeTab, currentProfile?.id]);

  useEffect(() => {
    if (isOpen) {
      fetchList();
    }
  }, [isOpen, activeTab, fetchList]);

  if (!isOpen) return null;

  // Handle follow/unfollow toggle interaction via the API
  const handleFollowToggle = async (targetProfileId: string) => {
    try {
      await toggleFollowUser({
        followerId: currentProfile.id,
        followingId: targetProfileId,
      });

      // Optimistically update local follow status states immediately
      setFollowingIds((prev) => {
        const updated = new Set(prev);
        if (updated.has(targetProfileId)) {
          updated.delete(targetProfileId);
        } else {
          updated.add(targetProfileId);
        }
        return updated;
      });
      
    } catch (error) {
      console.error("Failed to update follow status:", error);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 font-sans backdrop-blur-[1px]">
      <div className="absolute inset-0" onClick={onClose} />

      <div className="bg-white w-full max-w-[620px] h-[85vh] rounded-xl shadow-xl flex flex-col overflow-hidden relative z-10">
        
        {/* Modal Header */}
        <div className="flex items-center justify-between px-6 pt-4 pb-2">
          <h3 className="text-xl font-normal text-gray-900">
            {currentProfile.firstName}’s Network
          </h3>
          <button
            onClick={onClose}
            className="text-gray-500 hover:bg-gray-100 p-1.5 rounded-full transition-colors focus:outline-none"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* Tabs */}
        <div className="border-b border-gray-200 px-6 flex space-x-6">
          <button
            onClick={() => setActiveTab('following')}
            className={`py-3 text-[15px] font-semibold tracking-wide border-b-2 transition-colors focus:outline-none ${
              activeTab === 'following'
                ? 'border-green-700 text-green-700'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            Following
          </button>
          <button
            onClick={() => setActiveTab('followers')}
            className={`py-3 text-[15px] font-semibold tracking-wide border-b-2 transition-colors focus:outline-none ${
              activeTab === 'followers'
                ? 'border-green-700 text-green-700'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            Followers
          </button>
        </div>

        {/* Sub-header Context Banner */}
        <div className="bg-gray-50/70 px-6 py-3 border-b border-gray-100">
          <p className="text-[14px] text-gray-500">
            {activeTab === 'following'
              ? `You are following ${people.length} people out of your network`
              : 'People who follow your public updates'}
          </p>
        </div>

        {/* List Content Area */}
        <div className="flex-1 overflow-y-auto px-6 divide-y divide-gray-200">
          {loading ? (
            <div className="flex items-center justify-center h-32 text-gray-400">Loading details...</div>
          ) : people.length > 0 ? (
            people.map((person) => {
              const isCurrentlyFollowing = followingIds.has(person.id);

              return (
                <div key={person.id} className="flex items-start justify-between py-4 gap-4">
                  <div className="flex gap-3 items-start">
                    <img
                      src={person.profilePictureUrl || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150'}
                      alt={`${person.firstName} ${person.lastName}`}
                      className="w-12 h-12 rounded-full object-cover border border-gray-100 shrink-0"
                    />
                    <div className="flex flex-col">
                      <h4 className="text-[15px] font-semibold text-gray-900 leading-snug hover:underline cursor-pointer">
                        {person.firstName} {person.lastName}
                      </h4>
                      <p className="text-[13px] text-gray-600 line-clamp-2 mt-0.5 leading-normal">
                        {person.headline || "LinkedIn User"}
                      </p>
                    </div>
                  </div>

                  {/* Follow/Unfollow Action button mapped with current logic */}
                  <button
                    onClick={() => handleFollowToggle(person.id)}
                    className={`text-[15px] font-semibold px-4 py-1.5 rounded-full border transition-all duration-150 shrink-0 focus:outline-none ${
                      isCurrentlyFollowing
                        ? 'border-gray-500 text-gray-600 hover:bg-gray-100 hover:border-gray-600'
                        : 'border-blue-600 text-blue-600 hover:bg-blue-50 hover:border-blue-700'
                    }`}
                  >
                    {isCurrentlyFollowing ? 'Following' : 'Follow'}
                  </button>
                </div>
              );
            })
          ) : (
            <div className="text-center py-12 text-gray-400 text-sm">
              No profiles to display.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};