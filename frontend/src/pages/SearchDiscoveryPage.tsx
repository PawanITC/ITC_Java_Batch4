import { useEffect, useState } from "react";
import {
  SearchType,
  DiscoverySuggestion,
  TrendingTopic,
} from "../types/search";
import {
  searchByType,
  getDiscoverySuggestions,
  getTrendingTopics,
} from "../services/searchApi";

import SearchBar from "../components/search/SearchBar";
import SearchTabs from "../components/search/SearchTabs";
import PeopleResultCard from "../components/search/PeopleResultCard";
import PostResultCard from "../components/search/PostResultCard";
import JobResultCard from "../components/search/JobResultCard";
import CompanyResultCard from "../components/search/CompanyResultCard";
import DiscoverySuggestions from "../components/search/DiscoverySuggestions";
import TrendingTopics from "../components/search/TrendingTopics";

export default function SearchDiscoveryPage() {
  const [query, setQuery] = useState("");
  const [activeTab, setActiveTab] = useState<SearchType>("people");
  const [results, setResults] = useState<any[]>([]);
  const [suggestions, setSuggestions] = useState<DiscoverySuggestion[]>([]);
  const [topics, setTopics] = useState<TrendingTopic[]>([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    if (!query.trim()) return;

    try {
      setLoading(true);
      const data = await searchByType(activeTab, query);
      setResults(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
  const loadData = async () => {
    try {
      const suggestionsData = await getDiscoverySuggestions();
      setSuggestions(suggestionsData);
    } catch (error) {
      console.error("Suggestions failed", error);
      setSuggestions([]);
    }

    try {
      const topicsData = await getTrendingTopics();
      setTopics(topicsData);
    } catch (error) {
      console.error("Trending failed", error);
      setTopics([]);
    }
  };

  loadData();
}, []);
  const renderResults = () => {
    if (loading) {
      return <div className="bg-white rounded-lg shadow p-6">Searching...</div>;
    }

    if (results.length === 0) {
      return (
        <div className="bg-white rounded-lg shadow p-6 text-gray-500">
          Search for people, posts, jobs or companies.
        </div>
      );
    }

    return results.map((item) => {
      if (activeTab === "people") {
        return <PeopleResultCard key={item.id} person={item} />;
      }

      if (activeTab === "posts") {
        return <PostResultCard key={item.id} post={item} />;
      }

      if (activeTab === "jobs") {
        return <JobResultCard key={item.id} job={item} />;
      }

      return <CompanyResultCard key={item.id} company={item} />;
    });
  };

  return (
    <div className="min-h-screen bg-[#f3f2ef]">
      <main className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-4 gap-6 px-4 py-6">
        <section className="lg:col-span-3 space-y-4">
          <SearchBar
            query={query}
            onChange={setQuery}
            onSearch={handleSearch}
          />

          <SearchTabs activeTab={activeTab} onChange={setActiveTab} />

          <div className="space-y-4">{renderResults()}</div>
        </section>

        <aside className="space-y-4">
          <DiscoverySuggestions suggestions={suggestions} />
          <TrendingTopics topics={topics} />
        </aside>
      </main>
    </div>
  );
}