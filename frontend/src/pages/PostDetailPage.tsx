import { ArrowLeft } from "lucide-react";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import FeedPostCard from "../components/feed/FeedPostCard";
import keycloak from "../features/auth/keycloak";
import {
  addComment,
  getComments,
  getPost,
  likePost,
  unlikePost,
} from "../services/postApi";
import { FeedPost } from "../types/feed";

function currentUserName() {
  const token = keycloak.tokenParsed;
  return String(
    token?.name ||
      [token?.given_name, token?.family_name].filter(Boolean).join(" ") ||
      token?.preferred_username ||
      "Current user"
  );
}

export default function PostDetailPage() {
  const navigate = useNavigate();
  const { postId } = useParams();
  const [post, setPost] = useState<FeedPost | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const numericPostId = Number(postId);

  useEffect(() => {
    const loadPost = async () => {
      if (!Number.isFinite(numericPostId)) {
        setError("Post not found.");
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError("");
        setPost(await getPost(numericPostId));
      } catch (loadError) {
        console.error("Post detail loading failed", loadError);
        setError("We couldn't load this post.");
      } finally {
        setLoading(false);
      }
    };

    void loadPost();
  }, [numericPostId]);

  const handleLike = async (id: number) => {
    const updated = await likePost(id);
    setPost(updated);
    return updated;
  };

  const handleUnlike = async (id: number) => {
    const updated = await unlikePost(id);
    setPost(updated);
    return updated;
  };

  const handleComment = async (id: number, content: string) => {
    const updated = await addComment(id, content);
    setPost(updated);
    return updated;
  };

  return (
    <main className="min-h-screen bg-[#f3f2ef] px-3 py-5">
      <section className="mx-auto max-w-[680px] space-y-4">
        <button
          type="button"
          onClick={() => navigate(-1)}
          className="inline-flex items-center gap-2 rounded-full px-3 py-2 text-sm font-semibold text-gray-700 hover:bg-white"
        >
          <ArrowLeft size={18} />
          Back
        </button>

        {loading && (
          <div className="rounded-lg border border-[#d6d6d6] bg-white p-6 text-sm text-gray-500">
            Loading post...
          </div>
        )}

        {!loading && error && (
          <div className="rounded-lg border border-red-200 bg-red-50 p-6 text-sm text-red-700">
            {error}
          </div>
        )}

        {!loading && post && (
          <FeedPostCard
            post={post}
            currentUserName={currentUserName()}
            onLike={handleLike}
            onUnlike={handleUnlike}
            onComment={handleComment}
            onLoadComments={getComments}
          />
        )}
      </section>
    </main>
  );
}
