import { fireEvent, render, screen } from "@testing-library/react";
import SearchBar from "../components/search/SearchBar";
import SearchTabs from "../components/search/SearchTabs";
import PeopleResultCard from "../components/search/PeopleResultCard";
import PostResultCard from "../components/search/PostResultCard";
import JobResultCard from "../components/search/JobResultCard";
import CompanyResultCard from "../components/search/CompanyResultCard";
import DiscoverySuggestions from "../components/search/DiscoverySuggestions";
import TrendingTopics from "../components/search/TrendingTopics";

jest.mock(
  "react-router-dom",
  () => {
    const React = require("react");

    return {
      Link: ({ to, children, ...props }: any) => (
        <a href={to} {...props}>
          {children}
        </a>
      ),
    };
  },
  { virtual: true }
);

describe("search components", () => {
  test("SearchBar updates query and triggers search", () => {
    const onChange = jest.fn();
    const onSearch = jest.fn();
    render(<SearchBar query="" onChange={onChange} onSearch={onSearch} />);

    fireEvent.change(
      screen.getByPlaceholderText(/search people, jobs, posts, companies/i),
      { target: { value: "developer" } }
    );
    fireEvent.click(screen.getByRole("button", { name: /search/i }));

    expect(onChange).toHaveBeenCalledWith("developer");
    expect(onSearch).toHaveBeenCalledTimes(1);
  });

  test("SearchTabs renders all tabs and changes active tab", () => {
    const onChange = jest.fn();
    render(<SearchTabs activeTab="people" onChange={onChange} />);

    expect(screen.getByRole("button", { name: /people/i })).toHaveClass("bg-[#0A66C2]");
    expect(screen.getByRole("button", { name: /posts/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /jobs/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /companies/i })).toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: /jobs/i }));

    expect(onChange).toHaveBeenCalledWith("jobs");
  });

  test("renders people, post, job, and company result cards", () => {
    render(
      <>
        <PeopleResultCard
          person={{
            id: "person-1",
            fullName: "Alex Morgan",
            headline: "Frontend Developer",
            location: "London",
            connectionDegree: "2nd",
          }}
        />
        <PostResultCard
          post={{
            id: "post-1",
            authorName: "Priya Shah",
            content: "React testing tips",
            likesCount: 12,
            commentsCount: 4,
          }}
        />
        <JobResultCard
          job={{
            id: "job-1",
            title: "Senior Engineer",
            companyName: "TechWorks",
            location: "Remote",
            workplaceType: "Hybrid",
          }}
        />
        <CompanyResultCard
          company={{
            id: "company-1",
            name: "CloudLabs",
            industry: "Software",
            location: "Manchester",
            followers: 1200,
          }}
        />
      </>
    );

    expect(screen.getByText("Alex Morgan")).toBeInTheDocument();
    expect(screen.getByText("React testing tips")).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /alex morgan/i })).toHaveAttribute(
      "href",
      "/profiles/person-1"
    );
    expect(screen.getByRole("link", { name: /priya shah/i })).toHaveAttribute(
      "href",
      "/posts/post-1"
    );
    expect(screen.getByText("Senior Engineer")).toBeInTheDocument();
    expect(screen.getByText("CloudLabs")).toBeInTheDocument();
    expect(screen.getByText(/1200 followers/i)).toBeInTheDocument();
  });

  test("renders discovery suggestions and trending topics", () => {
    render(
      <>
        <DiscoverySuggestions
          suggestions={[
            {
              id: "suggestion-1",
              fullName: "Jamie Lee",
              headline: "Product Manager",
              reason: "Works in your industry",
            },
          ]}
        />
        <TrendingTopics
          topics={[
            {
              topic: "typescript",
              postCount: 88,
              category: "Engineering",
            },
          ]}
        />
      </>
    );

    expect(screen.getByText(/people you may know/i)).toBeInTheDocument();
    expect(screen.getByText("Jamie Lee")).toBeInTheDocument();
    expect(screen.getByText(/works in your industry/i)).toBeInTheDocument();
    expect(screen.getByText(/trending topics/i)).toBeInTheDocument();
    expect(screen.getByText(/#typescript/i)).toBeInTheDocument();
    expect(screen.getByText(/88 posts/i)).toBeInTheDocument();
  });
});
