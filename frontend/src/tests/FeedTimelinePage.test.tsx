import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import FeedTimelinePage from "../pages/FeedTimelinePage";
import { createPost } from "../services/postApi";
import { getTimeline } from "../services/timelineApi";

jest.mock("../services/timelineApi", () => ({
  getTimeline: jest.fn(),
}));

jest.mock("../services/postApi", () => ({
  createPost: jest.fn(),
}));

jest.mock("../components/feed/LeftProfileCard", () => () => <aside>Left profile</aside>);
jest.mock("../components/feed/RightNewsCard", () => () => <aside>Right news</aside>);

const mockGetTimeline = getTimeline as jest.Mock;
const mockCreatePost = createPost as jest.Mock;

const firstPost = {
  id: 1,
  postId: 1,
  authorId: "author-1",
  authorName: "Feed Author",
  authorHeadline: "Engineer",
  content: "Existing feed post",
  likesCount: 2,
  commentsCount: 1,
  createdAt: "2026-06-24T08:00:00.000Z",
};

describe("FeedTimelinePage", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("shows loading state and then renders timeline posts", async () => {
    mockGetTimeline.mockResolvedValue([firstPost]);

    render(<FeedTimelinePage />);

    expect(screen.getByText(/loading feed/i)).toBeInTheDocument();
    expect(await screen.findByText("Existing feed post")).toBeInTheDocument();
    expect(screen.getByText("Feed Author")).toBeInTheDocument();
  });

  test("renders empty timeline state", async () => {
    mockGetTimeline.mockResolvedValue([]);

    render(<FeedTimelinePage />);

    expect(await screen.findByText(/your timeline is ready/i)).toBeInTheDocument();
    expect(screen.getByText(/create the first post/i)).toBeInTheDocument();
  });

  test("shows an error when timeline loading fails", async () => {
    const consoleError = jest
      .spyOn(console, "error")
      .mockImplementation(() => undefined);
    mockGetTimeline.mockRejectedValue(new Error("Timeline down"));

    render(<FeedTimelinePage />);

    expect(await screen.findByText(/we couldn't load your timeline/i)).toBeInTheDocument();
    expect(screen.queryByText(/loading feed/i)).not.toBeInTheDocument();

    consoleError.mockRestore();
  });

  test("creates a post and reloads the timeline", async () => {
    mockGetTimeline
      .mockResolvedValueOnce([])
      .mockResolvedValueOnce([
        {
          ...firstPost,
          id: 2,
          postId: 2,
          content: "New post from composer",
        },
      ]);
    mockCreatePost.mockResolvedValue({
      ...firstPost,
      id: 2,
      postId: 2,
      content: "New post from composer",
    });

    render(<FeedTimelinePage />);

    await screen.findByText(/your timeline is ready/i);
    fireEvent.change(screen.getByPlaceholderText(/start a post/i), {
      target: { value: "New post from composer" },
    });
    fireEvent.click(screen.getByRole("button", { name: /^post$/i }));

    await waitFor(() => expect(mockCreatePost).toHaveBeenCalledWith("New post from composer"));
    expect(mockGetTimeline).toHaveBeenCalledTimes(2);
    expect(await screen.findByText("New post from composer")).toBeInTheDocument();
  });
});
