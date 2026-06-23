import { useState } from "react";
import { CalendarDays, Image, Newspaper, PlaySquare, Send } from "lucide-react";

type Props = {
  onCreate: (content: string) => Promise<void>;
};

export default function CreatePostCard({ onCreate }: Props) {
  const [content, setContent] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [expanded, setExpanded] = useState(false);

  const submit = async () => {
    if (!content.trim() || submitting) return;

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
    <div className="rounded-lg border border-[#dfdeda] bg-white p-3">
      <div className="flex gap-2">
        <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-[#c7d1d8] text-sm font-semibold text-[#38434f]">
          ST
        </div>

        <textarea
          value={content}
          onChange={(event) => setContent(event.target.value)}
          onFocus={() => setExpanded(true)}
          placeholder="Start a post"
          rows={expanded || content ? 3 : 1}
          maxLength={5000}
          className="min-h-[48px] flex-1 resize-none rounded-3xl border border-[#b2b2b2] px-4 py-3 text-sm font-semibold outline-none transition placeholder:text-gray-600 hover:bg-gray-50 focus:border-[#0a66c2] focus:bg-white focus:font-normal focus:ring-1 focus:ring-[#0a66c2]"
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
          disabled={!content.trim() || submitting}
          className="ml-2 flex h-9 items-center gap-2 rounded-full bg-[#0a66c2] px-4 text-sm font-semibold text-white transition hover:bg-[#004182] disabled:cursor-not-allowed disabled:bg-gray-300"
        >
          <Send size={16} />
          {submitting ? "Posting" : "Post"}
        </button>
      </div>
    </div>
  );
}
