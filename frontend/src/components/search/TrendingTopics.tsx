import { TrendingTopic } from "../../types/search";

export default function TrendingTopics({ topics }: { topics: TrendingTopic[] }) {
  return (
    <div className="bg-white rounded-lg shadow p-4">
      <h3 className="font-semibold mb-3">Trending topics</h3>

      <ul className="space-y-3">
        {topics.map((topic) => (
          <li key={topic.topic}>
            <p className="font-medium">#{topic.topic}</p>
            <p className="text-xs text-gray-500">
              {topic.postCount} posts · {topic.category}
            </p>
          </li>
        ))}
      </ul>
    </div>
  );
}