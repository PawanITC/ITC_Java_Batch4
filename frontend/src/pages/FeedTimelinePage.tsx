import { useCallback, useEffect, useState } from "react";
import keycloak from "../features/auth/keycloak";
import { FeedPost } from "../types/feed";
import { getTimeline, TimelineSortMode } from "../services/timelineApi";
import { addComment, createPost, deletePost, likePost, unlikePost } from "../services/postApi";

import CreatePostCard from "../components/feed/CreatePostCard";
import LeftProfileCard from "../components/feed/LeftProfileCard";
import FeedPostCard from "../components/feed/FeedPostCard";
import RightNewsCard from "../components/feed/RightNewsCard";

export default function FeedTimelinePage() {
  const [posts, setPosts] = useState<FeedPost[]>([]);
  const [loading, setLoading] = useState(true);
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
  }, [sortMode]);

  const currentUserId = keycloak.tokenParsed?.sub ?? "";

  return (
    <div className="min-h-screen bg-[#f4f2ee] pb-16 text-[#191919] md:pb-0">
      <main className="mx-auto grid max-w-[1128px] grid-cols-1 gap-6 px-3 py-6 sm:px-4 md:grid-cols-[225px_minmax(0,1fr)] xl:grid-cols-[225px_minmax(0,555px)_300px]">
        <div className="hidden md:block">
          <LeftProfileCard />
        </div>

        <section className="space-y-3">
          <CreatePostCard onCreate={handleCreatePost} />

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
                onLike={handleLike}
                onUnlike={handleUnlike}
                onComment={handleComment}
                onDelete={
                  post.authorId === currentUserId ? handleDelete : undefined
                }
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
