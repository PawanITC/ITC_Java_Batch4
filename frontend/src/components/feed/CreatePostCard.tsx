import { ChangeEvent, useRef, useState } from "react";
import { CalendarDays, Image, Newspaper, PlaySquare, Send, X } from "lucide-react";
import Avatar from "../common/Avatar";
import { MediaUploadResponse } from "../../types/feed";

type Props = {
  onCreate: (
    content: string,
    media?: Pick<MediaUploadResponse, "mediaObjectKey" | "objectKey" | "mediaType">
  ) => Promise<void>;
  onUploadMedia?: (file: File) => Promise<MediaUploadResponse>;
  currentUserName: string;
  currentUserAvatarUrl?: string;
  disabled?: boolean;
};

export default function CreatePostCard({
  onCreate,
  onUploadMedia,
  currentUserName,
  currentUserAvatarUrl,
  disabled = false,
}: Props) {
  const [content, setContent] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [expanded, setExpanded] = useState(false);
  const [media, setMedia] = useState<MediaUploadResponse | null>(null);
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const acceptedMediaRef = useRef("image/*");

  const submit = async () => {
    if ((!content.trim() && !media) || submitting || disabled || uploading) return;

    try {
      setSubmitting(true);
      setError("");
      if (media) {
        await onCreate(content.trim(), {
          mediaObjectKey: media.mediaObjectKey,
          objectKey: media.objectKey,
          mediaType: media.mediaType,
        });
      } else {
        await onCreate(content.trim());
      }
      setContent("");
      setMedia(null);
      setExpanded(false);
    } catch (error) {
      console.error("Post creation error", error);
      setError("Post could not be shared. Please try again.");
    } finally {
      setSubmitting(false);
    }
  };

  const chooseMedia = (accept: string) => {
    if (disabled || uploading || submitting) return;
    acceptedMediaRef.current = accept;
    if (fileInputRef.current) {
      fileInputRef.current.accept = accept;
      fileInputRef.current.value = "";
      fileInputRef.current.click();
    }
  };

  const handleFileSelected = async (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file || !onUploadMedia) return;

    try {
      setUploading(true);
      setError("");
      const uploaded = await onUploadMedia(file);
      setMedia(uploaded);
      setExpanded(true);
    } catch (error) {
      console.error("Media upload error", error);
      setError("Media could not be uploaded. Use a photo or video up to 50 MB.");
    } finally {
      setUploading(false);
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

      <input
        ref={fileInputRef}
        type="file"
        accept={acceptedMediaRef.current}
        onChange={handleFileSelected}
        className="hidden"
      />

      {media && (
        <div className="relative mt-3 overflow-hidden rounded-lg border border-[#d6d6d6] bg-black">
          <button
            onClick={() => setMedia(null)}
            className="absolute right-2 top-2 z-10 flex h-8 w-8 items-center justify-center rounded-full bg-black/70 text-white hover:bg-black"
            title="Remove media"
          >
            <X size={18} />
          </button>
          {media.mediaType === "IMAGE" ? (
            <img src={media.mediaUrl} alt="" className="max-h-[420px] w-full object-contain" />
          ) : (
            <video src={media.mediaUrl} controls className="max-h-[420px] w-full bg-black" />
          )}
        </div>
      )}

      <div className="mt-3 flex items-center justify-between border-t border-[#edf0f3] pt-2">
        <div className="grid flex-1 grid-cols-4 text-sm font-semibold text-gray-600">
          <button
            type="button"
            onClick={() => chooseMedia("image/*")}
            className="flex items-center justify-center gap-2 rounded px-2 py-3 hover:bg-gray-100"
          >
            <Image size={20} className="text-[#378fe9]" />
            <span className="hidden sm:inline">Photo</span>
          </button>
          <button
            type="button"
            onClick={() => chooseMedia("video/*")}
            className="flex items-center justify-center gap-2 rounded px-2 py-3 hover:bg-gray-100"
          >
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
          disabled={(!content.trim() && !media) || submitting || disabled || uploading}
          className="ml-2 flex h-9 items-center gap-2 rounded-full bg-[#0a66c2] px-4 text-sm font-semibold text-white transition hover:bg-[#004182] disabled:cursor-not-allowed disabled:bg-gray-300"
        >
          <Send size={16} />
          {uploading ? "Uploading" : submitting ? "Posting" : "Post"}
        </button>
      </div>
    </div>
  );
}
