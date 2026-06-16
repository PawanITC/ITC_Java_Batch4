import { PostSearchResult } from "../../types/search";

export default function PostResultCard({ post }: { post: PostSearchResult }) {
  return (
    <div className="bg-white rounded-lg shadow p-4">
      <h3 className="font-semibold">{post.authorName}</h3>
      <p className="mt-2 text-gray-700">{post.content}</p>
      <p className="text-sm text-gray-500 mt-3">
        👍 {post.likesCount} · 💬 {post.commentsCount}
      </p>
    </div>
  );
}