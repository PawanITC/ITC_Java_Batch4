import { useCallback, useEffect, useState } from "react";
import keycloak from "../features/auth/keycloak";
import { FeedPost } from "../types/feed";
import { getTimeline, TimelineSortMode } from "../services/timelineApi";
import { addComment, createPost, deletePost, likePost, unlikePost } from "../services/postApi";
import { getCurrentProfile, Profile } from "../features/userprofile/api";

import CreatePostCard from "../components/feed/CreatePostCard";
import LeftProfileCard from "../components/feed/LeftProfileCard";
import FeedPostCard from "../components/feed/FeedPostCard";
import RightNewsCard from "../components/feed/RightNewsCard";

export default function FeedTimelinePage() {
  const [posts, setPosts] = useState<FeedPost[]>([]);
  const [loading, setLoading] = useState(true);
  const [profile, setProfile] = useState<Profile | null>(null);
  const [profileLoading, setProfileLoading] = useState(true);
  const [profileMissing, setProfileMissing] = useState(false);
  const [error, setError] = useState("");
  const [sortMode, setSortMode] = useState<TimelineSortMode>("top");

  const loadFeed = useCallback(async () => {
    try {
      setLoading(true);
      setError("");
      const data = await getTimeline(sortMode);
      setPosts(data);
    } catch (error) {
      console.error("Feed loading error", error);
      setError("We couldn't load your timeline. Check that the gateway and timeline service are running.");
      setPosts([]);
    } finally {
      setLoading(false);
    }
  }, [sortMode]);

  const loadCurrentProfile = useCallback(async () => {
    try {
      setProfileLoading(true);
      setProfileMissing(false);
      const currentProfile = await getCurrentProfile();
      setProfile(currentProfile);
    } catch (error: any) {
      if (error?.response?.status === 404) {
        setProfile(null);
        setProfileMissing(true);
        return;
      }

      console.error("Current profile loading error", error);
      setProfile(null);
      setError("We couldn't load your profile. Please try again.");
    } finally {
      setProfileLoading(false);
    }
  }, []);

  const handleCreatePost = async (content: string) => {
    setError("");
    const created = await createPost(content);
    setPosts((currentPosts) => [created, ...currentPosts]);
    await loadFeed();
  };

  const handleLike = async (postId: number) => {
    const updated = await likePost(postId);
    setPosts((currentPosts) =>
      currentPosts.map((post) =>
        (post.postId ?? post.id) === postId
          ? { ...post, likesCount: updated.likesCount }
          : post
      )
    );
  };

  const handleUnlike = async (postId: number) => {
    const updated = await unlikePost(postId);
    setPosts((currentPosts) =>
      currentPosts.map((post) =>
        (post.postId ?? post.id) === postId
          ? { ...post, likesCount: updated.likesCount }
          : post
      )
    );
  };

  const handleComment = async (postId: number, content: string) => {
    const updated = await addComment(postId, content);
    setPosts((currentPosts) =>
      currentPosts.map((post) =>
        (post.postId ?? post.id) === postId
          ? { ...post, commentsCount: updated.commentsCount }
          : post
      )
    );
  };

  const handleDelete = async (postId: number) => {
    await deletePost(postId);
    setPosts((currentPosts) =>
      currentPosts.filter((post) => (post.postId ?? post.id) !== postId)
    );
  };

  useEffect(() => {
    loadFeed();
  }, [loadFeed]);

  useEffect(() => {
    loadCurrentProfile();
  }, [loadCurrentProfile]);

  const currentUserId = keycloak.tokenParsed?.sub ?? "";
  const currentUserName = profile
    ? `${profile.firstName} ${profile.lastName}`.trim() || "Current user"
    : "Current user";
  const profileReady = Boolean(profile) && !profileMissing;

  return (
    <div className="min-h-screen bg-[#f4f2ee] pb-16 text-[#191919] md:pb-0">
      <main className="mx-auto grid max-w-[1128px] grid-cols-1 gap-6 px-3 py-6 sm:px-4 md:grid-cols-[225px_minmax(0,1fr)] xl:grid-cols-[225px_minmax(0,555px)_300px]">
        <div className="hidden md:block">
          <LeftProfileCard
            profile={profile}
            loading={profileLoading}
            missing={profileMissing}
          />
        </div>

        <section className="space-y-3">
          {profileMissing && (
            <div className="rounded-lg border border-[#d6d6d6] bg-white p-5 shadow-[0_1px_2px_rgba(0,0,0,0.08)]">
              <h1 className="text-lg font-semibold text-gray-900">
                Complete your profile to use your feed
              </h1>
              <p className="mt-1 text-sm text-gray-500">
                Your account is signed in, but this workspace needs a profile before posts and comments can use your identity.
              </p>
              <a
                href="/profile"
                className="mt-4 inline-flex rounded-full bg-[#0a66c2] px-4 py-2 text-sm font-semibold text-white hover:bg-[#004182]"
              >
                Complete profile
              </a>
            </div>
          )}

          <CreatePostCard
            onCreate={handleCreatePost}
            currentUserName={currentUserName}
            currentUserAvatarUrl={profile?.profilePictureUrl}
            disabled={!profileReady}
          />

          <div className="flex items-center gap-2 text-xs text-gray-500">
            <div className="h-px flex-1 bg-[#d6d6d6]" />
            <div className="flex items-center gap-2">
              <span className="whitespace-nowrap">Sort by:</span>
              <div className="inline-flex overflow-hidden rounded-full border border-[#d0d0d0] bg-white">
                <button
                  onClick={() => setSortMode("top")}
                  className={`px-3 py-1.5 font-semibold ${
                    sortMode === "top"
                      ? "bg-[#0a66c2] text-white"
                      : "text-gray-700 hover:bg-[#f3f6f8]"
                  }`}
                >
                  Top
                </button>
                <button
                  onClick={() => setSortMode("recent")}
                  className={`px-3 py-1.5 font-semibold ${
                    sortMode === "recent"
                      ? "bg-[#0a66c2] text-white"
                      : "text-gray-700 hover:bg-[#f3f6f8]"
                  }`}
                >
                  Recent
                </button>
              </div>
            </div>
          </div>

          {error && (
            <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}

          {loading ? (
            <div className="rounded-lg border border-[#dfdeda] bg-white p-6 text-center text-sm text-gray-500">
              Loading feed...
            </div>
          ) : posts.length === 0 ? (
            <div className="rounded-lg border border-[#dfdeda] bg-white p-8 text-center">
              <h2 className="text-base font-semibold text-gray-900">
                Your timeline is ready
              </h2>
              <p className="mt-1 text-sm text-gray-500">
                Create the first post or wait for posts from your network.
              </p>
            </div>
          ) : (
            posts.map((post) => (
              <FeedPostCard
                key={post.postId ?? post.id}
                post={post}
                currentUserName={currentUserName}
                currentUserAvatarUrl={profile?.profilePictureUrl}
                onLike={handleLike}
                onUnlike={handleUnlike}
                onComment={profileReady ? handleComment : undefined}
                onDelete={
                  post.authorId === currentUserId ? handleDelete : undefined
                }
                readOnly={!profileReady}
              />
            ))
          )}
        </section>

        <div className="hidden xl:block">
          <RightNewsCard />
        </div>
      </main>
    </div>
  );
}
