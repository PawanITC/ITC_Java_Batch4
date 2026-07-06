import { useState } from "react";
import { CalendarDays, Image, Newspaper, PlaySquare, Send } from "lucide-react";
import Avatar from "../common/Avatar";

type Props = {
  onCreate: (content: string) => Promise<void>;
  currentUserName: string;
  currentUserAvatarUrl?: string;
  disabled?: boolean;
};

export default function CreatePostCard({
  onCreate,
  currentUserName,
  currentUserAvatarUrl,
  disabled = false,
}: Props) {
  const [content, setContent] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [expanded, setExpanded] = useState(false);

  const submit = async () => {
    if (!content.trim() || submitting || disabled) return;

    try {
      setSubmitting(true);
      setError("");
      await onCreate(content.trim());
      setContent("");
      setExpanded(false);
    } catch (error) {
      console.error("Post creation error", error);
      setError("Post could not be shared. Please try again.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="rounded-lg border border-[#d6d6d6] bg-white p-3 shadow-[0_1px_2px_rgba(0,0,0,0.08)]">
      <div className="flex gap-2">
        <Avatar
          name={currentUserName}
          src={currentUserAvatarUrl}
          sizeClassName="h-12 w-12"
          textClassName="text-sm"
        />

        <textarea
          value={content}
          onChange={(event) => setContent(event.target.value)}
          onFocus={() => setExpanded(true)}
          disabled={disabled}
          placeholder={disabled ? "Complete your profile to start posting" : "Start a post"}
          rows={expanded || content ? 3 : 1}
          maxLength={5000}
          className="min-h-[48px] flex-1 resize-none rounded-3xl border border-[#b2b2b2] px-4 py-3 text-sm font-semibold outline-none transition placeholder:text-gray-600 hover:bg-[#f3f6f8] focus:border-[#0a66c2] focus:bg-white focus:font-normal focus:ring-1 focus:ring-[#0a66c2] disabled:cursor-not-allowed disabled:bg-gray-100 disabled:text-gray-500"
        />
      </div>

      {error && <p className="mt-2 px-1 text-sm text-red-600">{error}</p>}

      <div className="mt-3 flex items-center justify-between border-t border-[#edf0f3] pt-2">
        <div className="grid flex-1 grid-cols-4 text-sm font-semibold text-gray-600">
          <button className="flex items-center justify-center gap-2 rounded px-2 py-3 hover:bg-gray-100">
            <Image size={20} className="text-[#378fe9]" />
            <span className="hidden sm:inline">Photo</span>
          </button>
          <button className="flex items-center justify-center gap-2 rounded px-2 py-3 hover:bg-gray-100">
            <PlaySquare size={20} className="text-[#5f9b41]" />
            <span className="hidden sm:inline">Video</span>
          </button>
          <button className="flex items-center justify-center gap-2 rounded px-2 py-3 hover:bg-gray-100">
            <CalendarDays size={20} className="text-[#c37d16]" />
            <span className="hidden sm:inline">Event</span>
          </button>
          <button className="flex items-center justify-center gap-2 rounded px-2 py-3 hover:bg-gray-100">
            <Newspaper size={20} className="text-[#e16745]" />
            <span className="hidden sm:inline">Article</span>
          </button>
        </div>

        <button
          onClick={submit}
          disabled={!content.trim() || submitting || disabled}
          className="ml-2 flex h-9 items-center gap-2 rounded-full bg-[#0a66c2] px-4 text-sm font-semibold text-white transition hover:bg-[#004182] disabled:cursor-not-allowed disabled:bg-gray-300"
        >
          <Send size={16} />
          {submitting ? "Posting" : "Post"}
        </button>
      </div>
    </div>
  );
}
