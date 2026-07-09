import { MessageCircle, ThumbsUp } from "lucide-react";
import { PostSearchResult } from "../../types/search";

export default function PostResultCard({ post }: { post: PostSearchResult }) {
  return (
    <a
      href={`/posts/${post.id}`}
      className="block rounded-lg bg-white p-4 shadow transition hover:shadow-md focus:outline-none focus:ring-2 focus:ring-[#0a66c2]"
    >
      <h3 className="font-semibold">{post.authorName}</h3>
      <p className="mt-2 text-gray-700">{post.content}</p>
      <p className="mt-3 flex items-center gap-4 text-sm text-gray-500">
        <span className="flex items-center gap-1">
          <ThumbsUp size={15} />
          {post.likesCount}
        </span>
        <span className="flex items-center gap-1">
          <MessageCircle size={15} />
          {post.commentsCount}
        </span>
      </p>
    </a>
  );
}
