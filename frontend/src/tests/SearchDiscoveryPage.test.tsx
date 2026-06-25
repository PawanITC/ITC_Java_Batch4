import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import SearchDiscoveryPage from "../pages/SearchDiscoveryPage";
import {
  getDiscoverySuggestions,
  getTrendingTopics,
  searchByType,
} from "../services/searchApi";

let mockSearchString = "";

jest.mock(
  "react-router-dom",
  () => {
    const React = require("react");

    return {
      useSearchParams: () => {
        const [params, setParams] = React.useState(
          () => new URLSearchParams(mockSearchString)
        );

        return [
          params,
          (next: string | URLSearchParams) => {
            const nextParams = new URLSearchParams(next);
            mockSearchString = nextParams.toString();
            setParams(nextParams);
          },
        ] as const;
      },
    };
  },
  { virtual: true }
);

jest.mock("../services/searchApi", () => ({
  searchByType: jest.fn(),
  getDiscoverySuggestions: jest.fn(),
  getTrendingTopics: jest.fn(),
}));

const mockSearchByType = searchByType as jest.Mock;
const mockGetDiscoverySuggestions = getDiscoverySuggestions as jest.Mock;
const mockGetTrendingTopics = getTrendingTopics as jest.Mock;

function renderSearchPage(initialEntry = "/search") {
  const searchIndex = initialEntry.indexOf("?");
  mockSearchString = searchIndex >= 0 ? initialEntry.slice(searchIndex + 1) : "";
  return render(<SearchDiscoveryPage />);
}

describe("SearchDiscoveryPage", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetDiscoverySuggestions.mockResolvedValue([
      {
        id: "suggestion-1",
        fullName: "Maya Patel",
        headline: "UX Designer",
        reason: "People in your network follow Maya",
      },
    ]);
    mockGetTrendingTopics.mockResolvedValue([
      {
        topic: "cloud",
        postCount: 42,
        category: "Technology",
      },
    ]);
  });

  test("loads discovery suggestions and trending topics", async () => {
    renderSearchPage();

    expect(await screen.findByText("Maya Patel")).toBeInTheDocument();
    expect(screen.getByText(/#cloud/i)).toBeInTheDocument();
    expect(
      screen.getByText(/search for people, posts, jobs or companies/i)
    ).toBeInTheDocument();
  });

  test("searches people by default and renders people results", async () => {
    mockSearchByType.mockResolvedValue([
      {
        id: "person-1",
        fullName: "Sam Rivera",
        headline: "Backend Developer",
        location: "Bristol",
        connectionDegree: "3rd",
      },
    ]);

    renderSearchPage();

    fireEvent.change(
      screen.getByPlaceholderText(/search people, jobs, posts, companies/i),
      { target: { value: "sam" } }
    );
    fireEvent.click(screen.getByRole("button", { name: /^search$/i }));

    await waitFor(() => {
      expect(mockSearchByType).toHaveBeenCalledWith("people", "sam");
    });
    expect(await screen.findByText("Sam Rivera")).toBeInTheDocument();
    expect(screen.getByText(/1 people results/i)).toBeInTheDocument();
  });

  test("switches tabs and searches posts", async () => {
    mockSearchByType.mockResolvedValue([
      {
        id: "post-1",
        authorName: "Nina Brown",
        content: "Post search result",
        likesCount: 6,
        commentsCount: 1,
      },
    ]);

    renderSearchPage();

    fireEvent.change(
      screen.getByPlaceholderText(/search people, jobs, posts, companies/i),
      { target: { value: "testing" } }
    );
    fireEvent.click(screen.getByRole("button", { name: /posts/i }));

    await waitFor(() => {
      expect(mockSearchByType).toHaveBeenCalledWith("posts", "testing");
    });
    expect(await screen.findByText("Post search result")).toBeInTheDocument();
  });

  test("does not search when query is blank", async () => {
    renderSearchPage();

    await screen.findByText("Maya Patel");
    fireEvent.change(
      screen.getByPlaceholderText(/search people, jobs, posts, companies/i),
      { target: { value: "   " } }
    );
    fireEvent.click(screen.getByRole("button", { name: /^search$/i }));

    expect(mockSearchByType).not.toHaveBeenCalled();
  });

  test("loads from url params and shows quick filters", async () => {
    mockSearchByType.mockResolvedValue([
      {
        id: "job-1",
        title: "Java Backend Developer",
        companyName: "LinkedIn Demo Company",
        location: "London",
        workplaceType: "Hybrid",
      },
      {
        id: "job-2",
        title: "Spring Boot Engineer",
        companyName: "Tech Talent Ltd",
        location: "Remote",
        workplaceType: "Remote",
      },
    ]);

    renderSearchPage("/search?type=jobs&q=java");

    await waitFor(() => {
      expect(mockSearchByType).toHaveBeenCalledWith("jobs", "java");
    });

    expect(await screen.findByText("Java Backend Developer")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Hybrid" })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Remote" })).toBeInTheDocument();
  });

  test("clears side panel data when discovery calls fail", async () => {
    const consoleError = jest
      .spyOn(console, "error")
      .mockImplementation(() => undefined);
    mockGetDiscoverySuggestions.mockRejectedValue(new Error("Suggestions down"));
    mockGetTrendingTopics.mockRejectedValue(new Error("Trending down"));

    renderSearchPage();

    await waitFor(() => {
      expect(mockGetDiscoverySuggestions).toHaveBeenCalled();
      expect(mockGetTrendingTopics).toHaveBeenCalled();
    });
    expect(screen.getByText(/people you may know/i)).toBeInTheDocument();
    expect(screen.getByText(/trending topics/i)).toBeInTheDocument();
    expect(screen.queryByText("Maya Patel")).not.toBeInTheDocument();
    expect(screen.queryByText(/#cloud/i)).not.toBeInTheDocument();

    consoleError.mockRestore();
  });
});
