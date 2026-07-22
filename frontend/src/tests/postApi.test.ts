import {
  addComment,
  createPost,
  deletePost,
  likePost,
  unlikePost,
} from "../services/postApi";
import keycloak from "../features/auth/keycloak";

jest.mock("../features/auth/keycloak", () => ({
  token: "token-123",
  updateToken: jest.fn().mockResolvedValue(true),
}));

const mockFetch = jest.fn();

describe("postApi", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    global.fetch = mockFetch;
  });

  test("createPost posts content and normalizes response data", async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      json: jest.fn().mockResolvedValue({
        data: {
          postId: 7,
          authorName: "Post Author",
          content: "Created content",
        },
      }),
    });

    const result = await createPost("Created content");

    expect(keycloak.updateToken).toHaveBeenCalledWith(30);
    expect(mockFetch).toHaveBeenCalledWith(
      "http://localhost:8085/api/posts",
      expect.objectContaining({
        method: "POST",
        headers: {
          Authorization: "Bearer token-123",
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ content: "Created content" }),
      })
    );
    expect(result).toMatchObject({
      id: 7,
      postId: 7,
      authorName: "Post Author",
      authorHeadline: "Professional",
      content: "Created content",
      likesCount: 0,
      commentsCount: 0,
    });
  });

  test("likePost, unlikePost, deletePost, and addComment call expected endpoints", async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      json: jest.fn().mockResolvedValue({ data: { ok: true } }),
    });

    await likePost(5);
    await unlikePost(5);
    await deletePost(5);
    await addComment(5, "Nice post");

    expect(mockFetch).toHaveBeenNthCalledWith(
      1,
      "http://localhost:8085/api/posts/5/like",
      expect.objectContaining({ method: "POST" })
    );
    expect(mockFetch).toHaveBeenNthCalledWith(
      2,
      "http://localhost:8085/api/posts/5/like",
      expect.objectContaining({ method: "DELETE" })
    );
    expect(mockFetch).toHaveBeenNthCalledWith(
      3,
      "http://localhost:8085/api/posts/5",
      expect.objectContaining({ method: "DELETE" })
    );
    expect(mockFetch).toHaveBeenNthCalledWith(
      4,
      "http://localhost:8085/api/posts/5/comments",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ content: "Nice post" }),
      })
    );
  });

  test("throws when createPost response is not ok", async () => {
    mockFetch.mockResolvedValue({
      ok: false,
      status: 500,
    });

    await expect(createPost("broken")).rejects.toThrow("Create post failed: 500");
  });
});
