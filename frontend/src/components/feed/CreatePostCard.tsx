import { useState } from "react";

type Props = {
  onCreate: (content: string) => Promise<void>;
};

export default function CreatePostCard({ onCreate }: Props) {
  const [content, setContent] = useState("");

  const submit = async () => {
    if (!content.trim()) return;

    await onCreate(content);
    setContent("");
  };

  return (
    <div className="bg-white rounded-lg shadow p-4">
      <div className="flex gap-3">
        <div className="w-12 h-12 rounded-full bg-gray-300 flex items-center justify-center">
          👩‍💻
        </div>

        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="Start a post..."
          rows={3}
          className="flex-1 border rounded-2xl px-4 py-3 resize-none outline-none"
        />
      </div>

      <div className="flex justify-between items-center mt-4 border-t pt-3">
        <div className="flex gap-6 text-sm text-gray-600">
          <span>🖼 Photo</span>
          <span>🎥 Video</span>
          <span>📝 Article</span>
        </div>

        <button
          onClick={submit}
          className="bg-[#0A66C2] text-white px-6 py-2 rounded-full font-semibold hover:bg-[#004182]"
        >
          Post
        </button>
      </div>
    </div>
  );
}