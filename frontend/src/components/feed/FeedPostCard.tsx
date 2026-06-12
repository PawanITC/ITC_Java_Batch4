import { FeedPost } from "../../types/feed";

type Props = {
  post: FeedPost;
  onLike: (postId: number) => Promise<void>;
};

export default function FeedPostCard({ post, onLike }: Props) {
  return (
    <article className="bg-white rounded-lg shadow p-4">
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

      <p className="mt-4 text-gray-800 leading-relaxed">{post.content}</p>

      <div className="text-sm text-gray-500 mt-4">
        👍 {post.likesCount} · 💬 {post.commentsCount} comments
      </div>

      <div className="border-t mt-3 pt-3 grid grid-cols-3 text-sm text-gray-600">
        <button
          onClick={() => onLike(post.id)}
          className="hover:bg-gray-100 py-2 rounded"
        >
          👍 Like
        </button>
        <button className="hover:bg-gray-100 py-2 rounded">💬 Comment</button>
        <button className="hover:bg-gray-100 py-2 rounded">↗ Share</button>
      </div>
    </article>
  );
}