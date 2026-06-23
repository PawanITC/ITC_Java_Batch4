import { useEffect, useState } from "react";
import { FeedPost } from "../types/feed";
import { getTimeline } from "../services/timelineApi";
import { createPost } from "../services/postApi";

import FeedNavbar from "../components/feed/FeedNavbar";
import CreatePostCard from "../components/feed/CreatePostCard";
import LeftProfileCard from "../components/feed/LeftProfileCard";
import FeedPostCard from "../components/feed/FeedPostCard";
import RightNewsCard from "../components/feed/RightNewsCard";

export default function FeedTimelinePage() {
  const [posts, setPosts] = useState<FeedPost[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadFeed = async () => {
    try {
      setLoading(true);
      setError("");
      const data = await getTimeline();
      setPosts(data);
    } catch (error) {
      console.error("Feed loading error", error);
      setError("We couldn't load your timeline. Check that the gateway and timeline service are running.");
      setPosts([]);
    } finally {
      setLoading(false);
    }
  };

  const handleCreatePost = async (content: string) => {
    setError("");
    const created = await createPost(content);
    setPosts((currentPosts) => [created, ...currentPosts]);
    await loadFeed();
  };

  useEffect(() => {
    loadFeed();
  }, []);

  return (
    <div className="min-h-screen bg-[#f4f2ee] pb-16 text-[#191919] md:pb-0">
      <FeedNavbar />

      <main className="mx-auto grid max-w-[1128px] grid-cols-1 gap-6 px-3 py-6 sm:px-4 md:grid-cols-[225px_minmax(0,1fr)] xl:grid-cols-[225px_minmax(0,555px)_300px]">
        <div className="hidden md:block">
          <LeftProfileCard />
        </div>

        <section className="space-y-3">
          <CreatePostCard onCreate={handleCreatePost} />

          <div className="flex items-center gap-2 text-xs text-gray-500">
            <div className="h-px flex-1 bg-[#d6d6d6]" />
            <button className="whitespace-nowrap hover:text-gray-900">
              Sort by: <span className="font-semibold text-gray-800">Top</span>
            </button>
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
