import {
  getDiscoverySuggestions,
  getTrendingTopics,
  searchByType,
} from "../services/searchApi";
import keycloak from "../features/auth/keycloak";

jest.mock("../features/auth/keycloak", () => ({
  authenticated: true,
  token: "search-token",
  updateToken: jest.fn().mockResolvedValue(true),
}));

const mockFetch = jest.fn();

describe("searchApi", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (keycloak as any).authenticated = true;
    (keycloak as any).token = "search-token";
    global.fetch = mockFetch;
    jest.spyOn(console, "log").mockImplementation(() => undefined);
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  test("searchByType calls encoded search endpoint", async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      text: jest.fn().mockResolvedValue(
        JSON.stringify({ data: [{ id: "person-1" }] })
      ),
    });

    const result = await searchByType("people", "sam rivera");

    expect(keycloak.updateToken).toHaveBeenCalledWith(30);
    expect(mockFetch).toHaveBeenCalledWith(
      "http://localhost:8085/api/search/people?q=sam%20rivera",
      {
        headers: {
          Authorization: "Bearer search-token",
        },
      }
    );
    expect(result).toEqual([{ id: "person-1" }]);
  });

  test("loads discovery suggestions and trending topics", async () => {
    mockFetch
      .mockResolvedValueOnce({
        ok: true,
        text: jest.fn().mockResolvedValue(
          JSON.stringify({ data: [{ id: "suggestion-1" }] })
        ),
      })
      .mockResolvedValueOnce({
        ok: true,
        text: jest.fn().mockResolvedValue(
          JSON.stringify({ data: [{ topic: "react" }] })
        ),
      });

    expect(await getDiscoverySuggestions()).toEqual([{ id: "suggestion-1" }]);
    expect(await getTrendingTopics()).toEqual([{ topic: "react" }]);
    expect(mockFetch).toHaveBeenNthCalledWith(
      1,
      "http://localhost:8085/api/discovery/suggestions",
      expect.any(Object)
    );
    expect(mockFetch).toHaveBeenNthCalledWith(
      2,
      "http://localhost:8085/api/discovery/trending/topics",
      expect.any(Object)
    );
  });

  test("throws when user is not authenticated", async () => {
    (keycloak as any).authenticated = false;
    (keycloak as any).token = undefined;

    await expect(searchByType("people", "sam")).rejects.toThrow(
      "User is not authenticated. Token is missing."
    );
    expect(mockFetch).not.toHaveBeenCalled();
  });

  test("throws when search response is not ok", async () => {
    mockFetch.mockResolvedValue({
      ok: false,
      status: 401,
    });

    await expect(searchByType("jobs", "engineer")).rejects.toThrow(
      "Search failed: 401"
    );
  });

  test("returns empty arrays when successful responses have no body", async () => {
    mockFetch
      .mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: jest.fn().mockResolvedValue(""),
      })
      .mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: jest.fn().mockResolvedValue(""),
      })
      .mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: jest.fn().mockResolvedValue(""),
      });

    await expect(searchByType("people", "sam")).resolves.toEqual([]);
    await expect(getDiscoverySuggestions()).resolves.toEqual([]);
    await expect(getTrendingTopics()).resolves.toEqual([]);
  });
});
