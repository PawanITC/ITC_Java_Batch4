import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import FeedTimelinePage from "../pages/FeedTimelinePage";
import { addComment, createPost, deletePost, likePost, unlikePost } from "../services/postApi";
import { getTimeline } from "../services/timelineApi";

jest.mock("../services/timelineApi", () => ({
  getTimeline: jest.fn(),
}));

jest.mock("../services/postApi", () => ({
  createPost: jest.fn(),
  likePost: jest.fn(),
  unlikePost: jest.fn(),
  addComment: jest.fn(),
  deletePost: jest.fn(),
}));

jest.mock("../features/auth/keycloak", () => ({
  tokenParsed: { sub: "author-1" },
}));

jest.mock("../components/feed/LeftProfileCard", () => () => <aside>Left profile</aside>);
jest.mock("../components/feed/RightNewsCard", () => () => <aside>Right news</aside>);

const mockGetTimeline = getTimeline as jest.Mock;
const mockCreatePost = createPost as jest.Mock;
const mockLikePost = likePost as jest.Mock;
const mockUnlikePost = unlikePost as jest.Mock;
const mockAddComment = addComment as jest.Mock;
const mockDeletePost = deletePost as jest.Mock;

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

    expect(mockGetTimeline).toHaveBeenCalledWith("top");
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

  test("switches between top and recent timeline sorts", async () => {
    mockGetTimeline
      .mockResolvedValueOnce([firstPost])
      .mockResolvedValueOnce([
        {
          ...firstPost,
          id: 9,
          postId: 9,
          content: "Most recent post",
        },
      ]);

    render(<FeedTimelinePage />);

    expect(await screen.findByText("Existing feed post")).toBeInTheDocument();
    fireEvent.click(screen.getByRole("button", { name: /recent/i }));

    await waitFor(() => expect(mockGetTimeline).toHaveBeenLastCalledWith("recent"));
    expect(await screen.findByText("Most recent post")).toBeInTheDocument();
  });

  test("likes, unlikes, comments, and deletes posts", async () => {
    mockGetTimeline.mockResolvedValue([firstPost]);
    mockLikePost.mockResolvedValue({ ...firstPost, likesCount: 3 });
    mockUnlikePost.mockResolvedValue({ ...firstPost, likesCount: 2 });
    mockAddComment.mockResolvedValue({ ...firstPost, commentsCount: 2 });
    mockDeletePost.mockResolvedValue(true);

    render(<FeedTimelinePage />);

    expect(await screen.findByText("Existing feed post")).toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: /like/i }));
    await waitFor(() => expect(mockLikePost).toHaveBeenCalledWith(1));

    fireEvent.click(screen.getByRole("button", { name: /like/i }));
    await waitFor(() => expect(mockUnlikePost).toHaveBeenCalledWith(1));

    fireEvent.click(screen.getByRole("button", { name: /comment/i }));
    fireEvent.change(screen.getByPlaceholderText(/add a comment/i), {
      target: { value: "Looks good" },
    });
    fireEvent.click(screen.getByTitle(/post comment/i));
    await waitFor(() => expect(mockAddComment).toHaveBeenCalledWith(1, "Looks good"));

    fireEvent.click(screen.getByTitle(/more/i));
    fireEvent.click(screen.getByText(/delete post/i));
    await waitFor(() => expect(mockDeletePost).toHaveBeenCalledWith(1));
    await waitFor(() => expect(screen.queryByText("Existing feed post")).not.toBeInTheDocument());
  });
});
