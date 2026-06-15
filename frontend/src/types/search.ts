export type PeopleSearchResult = {
  id: string;
  fullName: string;
  headline: string;
  location: string;
  profileImageUrl?: string;
  connectionDegree: string;
};

export type PostSearchResult = {
  id: string;
  authorName: string;
  content: string;
  likesCount: number;
  commentsCount: number;
};

export type JobSearchResult = {
  id: string;
  title: string;
  companyName: string;
  location: string;
  workplaceType: string;
};

export type CompanySearchResult = {
  id: string;
  name: string;
  industry: string;
  location: string;
  followers: number;
};

export type DiscoverySuggestion = {
  id: string;
  fullName: string;
  headline: string;
  reason: string;
  profileImageUrl?: string;
};

export type TrendingTopic = {
  topic: string;
  postCount: number;
  category: string;
};

export type SearchType = "people" | "posts" | "jobs" | "companies";