import { getTimeline } from "../services/timelineApi";
import keycloak from "../features/auth/keycloak";

jest.mock("../features/auth/keycloak", () => ({
  token: "timeline-token",
  updateToken: jest.fn().mockResolvedValue(true),
}));

const mockFetch = jest.fn();

describe("timelineApi", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    global.fetch = mockFetch;
  });

  test("loads and normalizes timeline response data", async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      json: jest.fn().mockResolvedValue({
        data: [
          {
            postId: 12,
            authorName: "Timeline Author",
            content: "Timeline content",
          },
        ],
      }),
    });

    const result = await getTimeline();

    expect(keycloak.updateToken).toHaveBeenCalledWith(30);
    expect(mockFetch).toHaveBeenCalledWith("http://localhost:8085/api/timeline", {
      headers: {
        Authorization: "Bearer timeline-token",
        "Content-Type": "application/json",
      },
    });
    expect(result).toHaveLength(1);
    expect(result[0]).toMatchObject({
      id: 12,
      postId: 12,
      authorName: "Timeline Author",
      authorHeadline: "Professional",
      content: "Timeline content",
      likesCount: 0,
      commentsCount: 0,
    });
  });

  test("supports array responses without a data wrapper", async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      json: jest.fn().mockResolvedValue([
        {
          id: 3,
          authorName: "Array Author",
          content: "Array content",
        },
      ]),
    });

    const result = await getTimeline();

    expect(result[0]).toMatchObject({
      id: 3,
      postId: 3,
      authorName: "Array Author",
      content: "Array content",
    });
  });

  test("throws when timeline response is not ok", async () => {
    mockFetch.mockResolvedValue({
      ok: false,
      status: 503,
    });

    await expect(getTimeline()).rejects.toThrow("Timeline failed: 503");
  });
});
