import { useEffect, useState } from "react";
import { FeedPost } from "../types/feed";
import {
  createPost,
  getFeed,
  likePost,
  unlikePost,
  deletePost,
  addComment,
} from "../services/feedApi";

import FeedNavbar from "../components/feed/FeedNavbar";
import LeftProfileCard from "../components/feed/LeftProfileCard";
import CreatePostCard from "../components/feed/CreatePostCard";
import FeedPostCard from "../components/feed/FeedPostCard";
import RightNewsCard from "../components/feed/RightNewsCard";

export default function FeedTimelinePage() {
  const [posts, setPosts] = useState<FeedPost[]>([]);
  const [loading, setLoading] = useState(true);

  const loadFeed = async () => {
    try {
      setLoading(true);
      const data = await getFeed();
      setPosts(data);
    } catch (error) {
      console.error("Feed loading error", error);
      setPosts([]);
    } finally {
      setLoading(false);
    }
  };

  const handleCreatePost = async (content: string) => {
    await createPost(content);
    await loadFeed();
  };

  const handleLikePost = async (postId: number) => {
    await likePost(postId);
    await loadFeed();
  };

  const handleUnlikePost = async (postId: number) => {
    await unlikePost(postId);
    await loadFeed();
  };

  const handleDeletePost = async (postId: number) => {
    await deletePost(postId);
    await loadFeed();
  };

  const handleAddComment = async (postId: number, content: string) => {
    await addComment(postId, content);
    await loadFeed();
  };

  useEffect(() => {
    loadFeed();
  }, []);

  return (
    <div className="min-h-screen bg-[#f3f2ef]">
      <FeedNavbar />

      <main className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-4 gap-6 px-4 py-6">
        <div className="hidden lg:block">
          <LeftProfileCard />
        </div>

        <section className="lg:col-span-2 space-y-4">
          <CreatePostCard onCreate={handleCreatePost} />

          {loading ? (
            <div className="bg-white rounded-lg shadow p-6 text-center text-gray-500">
              Loading feed...
            </div>
          ) : posts.length === 0 ? (
            <div className="bg-white rounded-lg shadow p-6 text-center text-gray-500">
              No posts yet. Start the conversation.
            </div>
          ) : (
            posts.map((post) => (
              <FeedPostCard
                key={post.id}
                post={post}
                onLike={handleLikePost}
                onUnlike={handleUnlikePost}
                onDelete={handleDeletePost}
                onComment={handleAddComment}
              />
            ))
          )}
        </section>

        <div className="hidden lg:block">
          <RightNewsCard />
        </div>
      </main>
    </div>
  );
}