import { useState } from "react";
import { FeedPost } from "../../types/feed";

type Props = {
  post: FeedPost;
  onLike: (postId: number) => Promise<void>;
  onUnlike: (postId: number) => Promise<void>;
  onDelete: (postId: number) => Promise<void>;
  onComment: (postId: number, content: string) => Promise<void>;
};

export default function FeedPostCard({
  post,
  onLike,
  onUnlike,
  onDelete,
  onComment,
}: Props) {
  const [comment, setComment] = useState("");
  const [showCommentBox, setShowCommentBox] = useState(false);

  const handleCommentSubmit = async () => {
    if (!comment.trim()) return;

    await onComment(post.id, comment);
    setComment("");
    setShowCommentBox(false);
  };

  return (
    <article className="bg-white rounded-lg shadow p-4">
      <div className="flex justify-between">
        <div className="flex gap-3">
          <div className="w-12 h-12 rounded-full bg-gray-300 flex items-center justify-center">
            👤
          </div>

          <div>
            <h3 className="font-semibold text-gray-900">{post.authorName}</h3>
            <p className="text-sm text-gray-500">{post.authorHeadline}</p>
            <p className="text-xs text-gray-400">
              {new Date(post.createdAt).toLocaleString()}
            </p>
          </div>
        </div>

        <button
          onClick={() => onDelete(post.id)}
          className="text-sm text-gray-400 hover:text-red-600"
        >
          Delete
        </button>
      </div>

      <p className="mt-4 text-gray-800 leading-relaxed whitespace-pre-wrap">
        {post.content}
      </p>

      <div className="text-sm text-gray-500 mt-4">
        👍 {post.likesCount} · 💬 {post.commentsCount} comments
      </div>

      <div className="border-t mt-3 pt-3 grid grid-cols-4 text-sm text-gray-600">
        <button
          onClick={() => onLike(post.id)}
          className="hover:bg-gray-100 py-2 rounded"
        >
          👍 Like
        </button>

        <button
          onClick={() => onUnlike(post.id)}
          className="hover:bg-gray-100 py-2 rounded"
        >
          👎 Unlike
        </button>

        <button
          onClick={() => setShowCommentBox(!showCommentBox)}
          className="hover:bg-gray-100 py-2 rounded"
        >
          💬 Comment
        </button>

        <button className="hover:bg-gray-100 py-2 rounded">
          ↗ Share
        </button>
      </div>

      {showCommentBox && (
        <div className="mt-3 flex gap-2">
          <input
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            placeholder="Write a comment..."
            className="flex-1 border rounded-full px-4 py-2 text-sm"
          />

          <button
            onClick={handleCommentSubmit}
            className="bg-[#0A66C2] text-white px-4 py-2 rounded-full text-sm"
          >
            Post
          </button>
        </div>
      )}
    </article>
  );
}