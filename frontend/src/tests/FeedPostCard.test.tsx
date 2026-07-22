import { fireEvent, render, screen } from "@testing-library/react";
import FeedPostCard from "../components/feed/FeedPostCard";
import { FeedPost } from "../types/feed";

const post: FeedPost = {
  id: 10,
  postId: 20,
  authorId: "author-1",
  authorName: "Sam Taylor",
  authorHeadline: "Software Engineer",
  content: "Building useful things.",
  likesCount: 3,
  commentsCount: 2,
  createdAt: "2026-06-24T08:30:00.000Z",
};

describe("FeedPostCard", () => {
  test("renders author, content, and engagement counts", () => {
    render(<FeedPostCard post={post} />);

    expect(screen.getByText("ST")).toBeInTheDocument();
    expect(screen.getByText("Sam Taylor")).toBeInTheDocument();
    expect(screen.getByText("Software Engineer")).toBeInTheDocument();
    expect(screen.getByText("Building useful things.")).toBeInTheDocument();
    expect(screen.getByText(/3 reactions/i)).toBeInTheDocument();
    expect(screen.getByText(/2 comments/i)).toBeInTheDocument();
  });

  test("calls the like handler with the post id and updates the reaction count", async () => {
    const onLike = jest.fn().mockResolvedValue(undefined);
    const onUnlike = jest.fn().mockResolvedValue(undefined);
    render(<FeedPostCard post={post} onLike={onLike} onUnlike={onUnlike} />);

    fireEvent.click(screen.getByRole("button", { name: /like/i }));

    expect(await screen.findByText(/4 reactions/i)).toBeInTheDocument();
    expect(onLike).toHaveBeenCalledWith(20);

    fireEvent.click(screen.getByRole("button", { name: /like/i }));
    expect(await screen.findByText(/3 reactions/i)).toBeInTheDocument();
    expect(onUnlike).toHaveBeenCalledWith(20);
  });

  test("does not call like handler when read-only", () => {
    const onLike = jest.fn();
    render(<FeedPostCard post={post} onLike={onLike} readOnly />);

    fireEvent.click(screen.getByRole("button", { name: /like/i }));

    expect(onLike).not.toHaveBeenCalled();
    expect(screen.getByText(/3 reactions/i)).toBeInTheDocument();
  });

  test("opens the comment box and submits a non-empty comment", async () => {
    const onComment = jest.fn().mockResolvedValue(undefined);
    render(<FeedPostCard post={post} onComment={onComment} />);

    fireEvent.click(screen.getByRole("button", { name: /comment/i }));
    const input = screen.getByPlaceholderText(/add a comment/i);

    fireEvent.change(input, { target: { value: "Nice update" } });
    fireEvent.click(screen.getByTitle(/post comment/i));

    expect(await screen.findByText(/3 comments/i)).toBeInTheDocument();
    expect(onComment).toHaveBeenCalledWith(20, "Nice update");
  });

  test("shows delete action when provided", async () => {
    const onDelete = jest.fn().mockResolvedValue(undefined);
    render(<FeedPostCard post={post} onDelete={onDelete} />);

    fireEvent.click(screen.getByTitle(/more/i));
    fireEvent.click(screen.getByText(/delete post/i));

    expect(onDelete).toHaveBeenCalledWith(20);
  });

  test("keeps the comment box open for empty comments", () => {
    render(<FeedPostCard post={post} />);

    fireEvent.click(screen.getByRole("button", { name: /comment/i }));
    fireEvent.click(screen.getByTitle(/post comment/i));

    expect(screen.getByPlaceholderText(/add a comment/i)).toBeInTheDocument();
    expect(screen.getByText(/2 comments/i)).toBeInTheDocument();
  });
});
