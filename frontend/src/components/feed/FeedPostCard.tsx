import {
  Globe2,
  MessageCircle,
  MoreHorizontal,
  Repeat2,
  Send,
  ThumbsUp,
} from "lucide-react";
import { useState } from "react";
import Avatar from "../common/Avatar";
import { FeedComment, FeedPost } from "../../types/feed";

type Props = {
  post: FeedPost;
  currentUserName?: string;
  currentUserAvatarUrl?: string;
  onLike?: (postId: number) => Promise<void>;
  onUnlike?: (postId: number) => Promise<void>;
  onDelete?: (postId: number) => Promise<void>;
  onComment?: (postId: number, content: string) => Promise<void>;
  onLoadComments?: (postId: number) => Promise<FeedComment[]>;
  onRepost?: (post: FeedPost) => Promise<void>;
  onSend?: (post: FeedPost) => Promise<void>;
  readOnly?: boolean;
};

function formatDate(value: string) {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";

  return date.toLocaleString(undefined, {
    month: "short",
    day: "numeric",
    hour: "numeric",
    minute: "2-digit",
  });
}

export default function FeedPostCard({
  post,
  currentUserName = "Current user",
  currentUserAvatarUrl,
  onLike,
  onUnlike,
  onDelete,
  onComment,
  onLoadComments,
  onRepost,
  onSend,
  readOnly = false,
}: Props) {
  const postId = post.postId ?? post.id;
  const [liked, setLiked] = useState(false);
  const [likesCount, setLikesCount] = useState(post.likesCount);
  const [commentsCount, setCommentsCount] = useState(post.commentsCount);
  const [showCommentBox, setShowCommentBox] = useState(false);
  const [showComments, setShowComments] = useState(false);
  const [comments, setComments] = useState<FeedComment[]>([]);
  const [commentsLoading, setCommentsLoading] = useState(false);
  const [commentsError, setCommentsError] = useState("");
  const [comment, setComment] = useState("");
  const [menuOpen, setMenuOpen] = useState(false);
  const [actionMessage, setActionMessage] = useState("");
  const [reposting, setReposting] = useState(false);
  const [sending, setSending] = useState(false);

  const handleLike = async () => {
    if (readOnly) return;

    const nextLiked = !liked;
    setLiked(nextLiked);
    setLikesCount((current) => current + (nextLiked ? 1 : -1));

    if (nextLiked) {
      await onLike?.(postId);
    } else {
      await onUnlike?.(postId);
    }
  };

  const submitComment = async () => {
    if (!comment.trim() || readOnly) return;
    await onComment?.(postId, comment.trim());
    setCommentsCount((current) => current + 1);
    setComments((current) => [
      ...current,
      {
        id: Date.now(),
        postId,
        authorId: "",
        authorName: currentUserName,
        content: comment.trim(),
        createdAt: new Date().toISOString(),
      },
    ]);
    setComment("");
    setShowComments(true);
  };

  const handleRepost = async () => {
    if (readOnly || reposting) return;

    try {
      setReposting(true);
      setActionMessage("");
      await onRepost?.(post);
      setActionMessage("Reposted to your feed.");
    } catch (error) {
      console.error("Repost error", error);
      setActionMessage("Repost failed. Please try again.");
    } finally {
      setReposting(false);
    }
  };

  const handleSend = async () => {
    if (sending) return;

    try {
      setSending(true);
      setActionMessage("");
      await onSend?.(post);
      setActionMessage("Post link ready to share.");
    } catch (error) {
      console.error("Send error", error);
      setActionMessage("Send failed. Please try again.");
    } finally {
      setSending(false);
    }
  };

  const toggleComments = async () => {
    const nextVisible = !showComments;
    setShowComments(nextVisible);
    if (!nextVisible || comments.length > 0 || !onLoadComments) return;

    try {
      setCommentsLoading(true);
      setCommentsError("");
      setComments(await onLoadComments(postId));
    } catch (error) {
      console.error("Comment loading error", error);
      setCommentsError("Comments could not be loaded.");
    } finally {
      setCommentsLoading(false);
    }
  };

  return (
    <article className="rounded-lg border border-[#d6d6d6] bg-white shadow-[0_1px_2px_rgba(0,0,0,0.08)]">
      <div className="flex justify-between gap-3 px-4 pt-4">
        <div className="flex min-w-0 gap-3">
          <Avatar
            name={post.authorName}
            src={post.authorAvatarUrl}
            sizeClassName="h-12 w-12"
            textClassName="text-sm"
          />

          <div className="min-w-0">
            <h3 className="truncate text-sm font-semibold text-[#191919] hover:text-[#0a66c2] hover:underline">
              {post.authorName}
            </h3>
            <p className="truncate text-xs text-gray-500">
              {post.authorHeadline}
            </p>
            <p className="flex items-center gap-1 text-xs text-gray-500">
              {formatDate(post.createdAt)}
              <span aria-hidden="true">-</span>
              <Globe2 size={12} />
            </p>
          </div>
        </div>

        <div className="relative">
          <button
            onClick={() => setMenuOpen((current) => !current)}
            className="flex h-8 w-8 items-center justify-center rounded-full text-gray-500 hover:bg-[#f3f6f8] hover:text-[#0a66c2]"
            title="More"
          >
            <MoreHorizontal size={20} />
          </button>
          {onDelete && !readOnly && menuOpen && (
            <div className="absolute right-0 top-10 z-10 min-w-[120px] rounded-md border border-[#d6d6d6] bg-white p-1 shadow-lg">
              <button
                onClick={async () => {
                  setMenuOpen(false);
                  await onDelete(postId);
                }}
                className="w-full rounded px-3 py-2 text-left text-sm text-red-600 hover:bg-red-50"
              >
                Delete post
              </button>
            </div>
          )}
        </div>
      </div>

      <p className="whitespace-pre-wrap px-4 py-3 text-sm leading-relaxed text-[#191919]">
        {post.content}
      </p>

      {post.mediaUrl && (
        <div className="border-y border-[#edf0f3] bg-black">
          {post.mediaType === "VIDEO" ? (
            <video src={post.mediaUrl} controls className="max-h-[560px] w-full bg-black" />
          ) : (
            <img src={post.mediaUrl} alt="" className="max-h-[560px] w-full object-contain" />
          )}
        </div>
      )}

      <div className="flex items-center justify-between border-b border-[#edf0f3] px-4 pb-2 text-xs text-gray-500">
        <span className="flex items-center gap-1">
          <span className="flex h-4 w-4 items-center justify-center rounded-full bg-[#0a66c2] text-[10px] text-white">
            <ThumbsUp size={10} fill="currentColor" />
          </span>
          {likesCount} reactions
        </span>
        <button
          type="button"
          aria-label="Show discussion"
          onClick={toggleComments}
          className="rounded px-1 hover:text-[#0a66c2] hover:underline"
        >
          {commentsCount} comments
        </button>
      </div>

      <div className="grid grid-cols-4 px-2 py-1 text-sm font-semibold text-gray-600">
        <button
          onClick={handleLike}
          className={`flex items-center justify-center gap-2 rounded px-2 py-3 hover:bg-[#f3f6f8] hover:text-[#0a66c2] ${
            liked ? "text-[#0a66c2]" : ""
          }`}
        >
          <ThumbsUp size={18} fill={liked ? "currentColor" : "none"} />
          <span className="hidden sm:inline">Like</span>
        </button>

        <button
          type="button"
          aria-label="Comment"
          onClick={() => {
            if (!readOnly) {
              setShowCommentBox((current) => !current);
              if (!showComments) void toggleComments();
            }
          }}
          className="flex items-center justify-center gap-2 rounded px-2 py-3 hover:bg-[#f3f6f8] hover:text-[#0a66c2]"
        >
          <MessageCircle size={18} />
          <span className="hidden sm:inline">Comment</span>
        </button>

        <button
          type="button"
          onClick={handleRepost}
          disabled={readOnly || reposting}
          className="flex items-center justify-center gap-2 rounded px-2 py-3 hover:bg-[#f3f6f8] hover:text-[#0a66c2] disabled:cursor-not-allowed disabled:text-gray-300"
        >
          <Repeat2 size={18} />
          <span className="hidden sm:inline">{reposting ? "Reposting" : "Repost"}</span>
        </button>

        <button
          type="button"
          onClick={handleSend}
          disabled={sending}
          className="flex items-center justify-center gap-2 rounded px-2 py-3 hover:bg-[#f3f6f8] hover:text-[#0a66c2] disabled:cursor-not-allowed disabled:text-gray-300"
        >
          <Send size={18} />
          <span className="hidden sm:inline">{sending ? "Sending" : "Send"}</span>
        </button>
      </div>

      {actionMessage && (
        <p className="border-t border-[#edf0f3] px-4 py-2 text-xs text-gray-500">
          {actionMessage}
        </p>
      )}

      {showCommentBox && (
        <div className="flex gap-2 border-t border-[#edf0f3] px-4 py-3">
          <Avatar
            name={currentUserName}
            src={currentUserAvatarUrl}
            sizeClassName="h-9 w-9"
            textClassName="text-xs"
          />
          <div className="flex min-w-0 flex-1 items-center rounded-full border border-[#b2b2b2] px-3">
            <input
              value={comment}
              onChange={(event) => setComment(event.target.value)}
              onKeyDown={(event) => {
                if (event.key === "Enter") submitComment();
              }}
              placeholder="Add a comment..."
              className="min-w-0 flex-1 bg-transparent py-2 text-sm outline-none"
            />
            <button
              onClick={submitComment}
              disabled={!comment.trim()}
              className="flex h-8 w-8 items-center justify-center rounded-full text-[#0a66c2] hover:bg-[#edf3f8] disabled:text-gray-300"
              title="Post comment"
            >
              <Send size={16} />
            </button>
          </div>
        </div>
      )}

      {showComments && (
        <div className="space-y-3 border-t border-[#edf0f3] px-4 py-3">
          {commentsLoading ? (
            <p className="text-sm text-gray-500">Loading comments...</p>
          ) : commentsError ? (
            <p className="text-sm text-red-600">{commentsError}</p>
          ) : comments.length === 0 ? (
            <p className="text-sm text-gray-500">No comments yet.</p>
          ) : (
            comments.map((item) => (
              <div key={item.id} className="flex gap-2">
                <Avatar
                  name={item.authorName}
                  sizeClassName="h-8 w-8"
                  textClassName="text-xs"
                />
                <div className="min-w-0 rounded-2xl bg-[#f3f2ef] px-3 py-2">
                  <p className="text-xs font-semibold text-[#191919]">{item.authorName}</p>
                  <p className="whitespace-pre-wrap text-sm text-[#191919]">{item.content}</p>
                </div>
              </div>
            ))
          )}
        </div>
      )}
    </article>
  );
}
