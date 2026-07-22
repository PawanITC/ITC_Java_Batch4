import { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import {
  CompanySearchResult,
  DiscoverySuggestion,
  JobSearchResult,
  PeopleSearchResult,
  PostSearchResult,
  SearchType,
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
import JobPosting from "./JobPosting";
import { ManageNetworkCard } from "../features/userprofile/components/ManageNetworkCard";

const SEARCH_TABS: SearchType[] = ["people", "posts", "jobs", "companies"];
const ALL_FILTER = "All";

type SearchResult =
  | PeopleSearchResult
  | PostSearchResult
  | JobSearchResult
  | CompanySearchResult;

function isSearchType(value: string | null): value is SearchType {
  return value !== null && SEARCH_TABS.includes(value as SearchType);
}

export default function SearchDiscoveryPage() {
  const [searchParams, setSearchParams] = useSearchParams();

  const paramsQuery = searchParams.get("q") ?? "";
  const paramsType = isSearchType(searchParams.get("type"))
    ? (searchParams.get("type") as SearchType)
    : "people";

  const [query, setQuery] = useState(paramsQuery);
  const [activeTab, setActiveTab] = useState<SearchType>(paramsType);
  const [results, setResults] = useState<SearchResult[]>([]);
  const [suggestions, setSuggestions] = useState<DiscoverySuggestion[]>([]);
  const [topics, setTopics] = useState<TrendingTopic[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedFilter, setSelectedFilter] = useState(ALL_FILTER);

  useEffect(() => {
    setQuery(paramsQuery);
  }, [paramsQuery]);

  useEffect(() => {
    setActiveTab(paramsType);
  }, [paramsType]);

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

  useEffect(() => {
    const runSearch = async () => {
      const trimmedQuery = paramsQuery.trim();

      if (!trimmedQuery) {
        setResults([]);
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        const data = await searchByType(paramsType, trimmedQuery);
        setResults(data);
      } finally {
        setLoading(false);
      }
    };

    void runSearch();
  }, [paramsQuery, paramsType]);

  useEffect(() => {
    setSelectedFilter(ALL_FILTER);
  }, [activeTab, results]);

  const handleSearch = () => {
    const trimmedQuery = query.trim();
    const nextParams = new URLSearchParams(searchParams);

    nextParams.set("type", activeTab);
    if (trimmedQuery) {
      nextParams.set("q", trimmedQuery);
    } else {
      nextParams.delete("q");
    }

    setSearchParams(nextParams);
  };

  const handleTabChange = (tab: SearchType) => {
    setActiveTab(tab);

    const nextParams = new URLSearchParams(searchParams);
    nextParams.set("type", tab);

    const trimmedQuery = query.trim();
    if (trimmedQuery) {
      nextParams.set("q", trimmedQuery);
    } else {
      nextParams.delete("q");
    }

    setSearchParams(nextParams);
  };

  const filterOptions = useMemo(() => {
    const values =
      activeTab === "people"
        ? Array.from(
            new Set(
              (results as PeopleSearchResult[])
                .map((item) => item.location)
                .filter(Boolean)
            )
          )
        : activeTab === "posts"
        ? Array.from(
            new Set(
              (results as PostSearchResult[])
                .map((item) => item.authorName)
                .filter(Boolean)
            )
          )
        : activeTab === "jobs"
        ? Array.from(
            new Set(
              (results as JobSearchResult[])
                .map((item) => item.workplaceType)
                .filter(Boolean)
            )
          )
        : Array.from(
            new Set(
              (results as CompanySearchResult[])
                .map((item) => item.industry)
                .filter(Boolean)
            )
          );

    return [ALL_FILTER, ...values.slice(0, 4)];
  }, [activeTab, results]);

  const filteredResults = useMemo(() => {
    if (selectedFilter === ALL_FILTER) {
      return results;
    }

    if (activeTab === "people") {
      return (results as PeopleSearchResult[]).filter(
        (item) => item.location === selectedFilter
      );
    }

    if (activeTab === "posts") {
      return (results as PostSearchResult[]).filter(
        (item) => item.authorName === selectedFilter
      );
    }

    if (activeTab === "jobs") {
      return (results as JobSearchResult[]).filter(
        (item) => item.workplaceType === selectedFilter
      );
    }

    return (results as CompanySearchResult[]).filter(
      (item) => item.industry === selectedFilter
    );
  }, [activeTab, results, selectedFilter]);

  const hasSearched = paramsQuery.trim().length > 0;
  const resultLabel = activeTab === "people" ? "people" : activeTab;

  const renderResults = () => {
    if (loading) {
      return <div className="bg-white rounded-lg shadow p-6">Searching...</div>;
    }

    if (!hasSearched) {
      return (
        <div className="bg-white rounded-lg shadow p-6 text-gray-500">
          Search for people, posts, jobs or companies.
        </div>
      );
    }

    if (filteredResults.length === 0) {
      return (
        <div className="bg-white rounded-lg shadow p-6 text-gray-500">
          No {resultLabel} matched "{paramsQuery.trim()}".
        </div>
      );
    }

    return filteredResults.map((item) => {
      if (activeTab === "people") {
        return (
          <PeopleResultCard
            key={(item as PeopleSearchResult).id}
            person={item as PeopleSearchResult}
          />
        );
      }

      if (activeTab === "posts") {
        return (
          <PostResultCard
            key={(item as PostSearchResult).id}
            post={item as PostSearchResult}
          />
        );
      }

      if (activeTab === "jobs") {
        return (
          <JobResultCard
            key={(item as JobSearchResult).id}
            job={item as JobSearchResult}
          />
        );
      }

      return (
        <CompanyResultCard
          key={(item as CompanySearchResult).id}
          company={item as CompanySearchResult}
        />
      );
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

          <SearchTabs activeTab={activeTab} onChange={handleTabChange} />

          <div className="bg-white rounded-lg shadow p-4 space-y-4">
            <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p className="text-sm font-semibold text-gray-900 capitalize">
                  {hasSearched
                    ? `${filteredResults.length} ${resultLabel} results`
                    : "Search across your network"}
                </p>
                <p className="text-xs text-gray-500">
                  {hasSearched
                    ? `Showing ranked matches for "${paramsQuery.trim()}"`
                    : "Use the tabs to narrow people, posts, jobs and companies."}
                </p>
              </div>

              {filterOptions.length > 1 && (
                <div className="flex flex-wrap gap-2">
                  {filterOptions.map((option) => (
                    <button
                      key={option}
                      onClick={() => setSelectedFilter(option)}
                      className={`px-3 py-1.5 rounded-full text-xs font-semibold border ${
                        selectedFilter === option
                          ? "bg-[#0A66C2] text-white border-[#0A66C2]"
                          : "bg-white text-gray-600 border-gray-300"
                      }`}
                    >
                      {option}
                    </button>
                  ))}
                </div>
              )}
            </div>

            <div className="space-y-4">{renderResults()}</div>
          </div>
          {activeTab === "jobs" && (
            <div className="w-full mt-4">
              <JobPosting />
            </div>
          )}
        </section>

        <aside className="space-y-4">
          <DiscoverySuggestions suggestions={suggestions} />
          <TrendingTopics topics={topics} />
          <ManageNetworkCard/>
        </aside>
      </main>
    </div>
  );
}
