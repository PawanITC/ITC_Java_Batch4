import keycloak from "../features/auth/keycloak";

var mockCreateAxiosClient = jest.fn();

jest.mock("axios", () => {
  return {
    __esModule: true,
    default: {
      create: mockCreateAxiosClient,
    },
    create: mockCreateAxiosClient,
  };
});

jest.mock("../features/auth/keycloak", () => ({
  authenticated: true,
  token: "profile-token",
  updateToken: jest.fn().mockResolvedValue(true),
}));

const mockAxiosClient = {
  get: jest.fn(),
  post: jest.fn(),
  put: jest.fn(),
  delete: jest.fn(),
  interceptors: {
    request: {
      use: jest.fn(),
    },
  },
};

function loadUserProfileApi() {
  let api: typeof import("../features/userprofile/api");

  jest.isolateModules(() => {
    mockCreateAxiosClient.mockReturnValue(mockAxiosClient);
    api = require("../features/userprofile/api");
  });

  return api!;
}

describe("userProfileApi", () => {
  beforeEach(() => {
    mockCreateAxiosClient.mockClear();
    mockAxiosClient.interceptors.request.use.mockClear();
    mockAxiosClient.get.mockClear();
    mockAxiosClient.post.mockClear();
    mockAxiosClient.put.mockClear();
    mockAxiosClient.delete.mockClear();
    (keycloak.updateToken as jest.Mock).mockClear();
    mockAxiosClient.get.mockResolvedValue({ data: {} });
    mockAxiosClient.post.mockResolvedValue({ data: {} });
    mockAxiosClient.put.mockResolvedValue({ data: {} });
    mockAxiosClient.delete.mockResolvedValue({ data: undefined });
  });

  test("creates the axios client with the API gateway base URL", () => {
    loadUserProfileApi();

    expect(mockCreateAxiosClient).toHaveBeenCalledWith({
      baseURL: "http://localhost:8085",
    });
  });

  test("adds the bearer token to profile requests", async () => {
    loadUserProfileApi();
    const interceptor = mockAxiosClient.interceptors.request.use.mock.calls[0][0];
    const config = await interceptor({ headers: {} });

    expect(keycloak.updateToken).toHaveBeenCalledWith(30);
    expect(config.headers.Authorization).toBe("Bearer profile-token");
  });

  test("calls profile endpoints through gateway paths", async () => {
    const {
      createProfile,
      getCurrentProfile,
      getProfile,
      listProfiles,
      updateCurrentProfile,
      updateProfile,
    } = loadUserProfileApi();

    await listProfiles();
    await getCurrentProfile();
    await getProfile("profile-1");
    await createProfile({
      firstName: "Sam",
      lastName: "Rivera",
      email: "sam@example.com",
      gender: "PREFER_NOT_TO_SAY",
    });
    await updateCurrentProfile({ headline: "Senior Engineer" });
    await updateProfile("profile-1", { headline: "Engineer" });

    expect(mockAxiosClient.get).toHaveBeenNthCalledWith(1, "/api/profiles");
    expect(mockAxiosClient.get).toHaveBeenNthCalledWith(2, "/api/profiles/me");
    expect(mockAxiosClient.get).toHaveBeenNthCalledWith(3, "/api/profiles/profile-1");
    expect(mockAxiosClient.post).toHaveBeenCalledWith(
      "/api/profiles",
      expect.objectContaining({ email: "sam@example.com" })
    );
    expect(mockAxiosClient.put).toHaveBeenCalledWith("/api/profiles/me", {
      headline: "Senior Engineer",
    });
    expect(mockAxiosClient.put).toHaveBeenCalledWith("/api/profiles/profile-1", {
      headline: "Engineer",
    });
  });

  test("calls profile detail subresources through gateway paths", async () => {
    const {
      createEducation,
      createExperience,
      createSkill,
      deleteEducation,
      deleteExperience,
      deleteSkill,
      updateEducation,
      updateExperience,
      updateSkill,
    } = loadUserProfileApi();

    await createSkill({ profileId: "profile-1", skillName: "Java", endorsementCount: 1 });
    await updateSkill("skill-1", {
      profileId: "profile-1",
      skillName: "Spring",
      endorsementCount: 2,
    });
    await deleteSkill("skill-1");

    await createEducation({
      profileId: "profile-1",
      schoolName: "University",
      degree: "BS",
      fieldOfStudy: "CS",
      startYear: 2020,
      endYear: 2024,
    });
    await updateEducation("education-1", {
      profileId: "profile-1",
      schoolName: "University",
      degree: "MS",
      fieldOfStudy: "CS",
      startYear: 2024,
      endYear: 2026,
    });
    await deleteEducation("education-1");

    await createExperience({
      profileId: "profile-1",
      companyName: "Acme",
      title: "Developer",
      description: "Built APIs",
      startDate: "2024-01-01",
      current: true,
    });
    await updateExperience("experience-1", {
      profileId: "profile-1",
      companyName: "Acme",
      title: "Senior Developer",
      description: "Built platforms",
      startDate: "2024-01-01",
      current: true,
    });
    await deleteExperience("experience-1");

    expect(mockAxiosClient.post).toHaveBeenCalledWith("/api/skills", expect.any(Object));
    expect(mockAxiosClient.put).toHaveBeenCalledWith("/api/skills/skill-1", expect.any(Object));
    expect(mockAxiosClient.delete).toHaveBeenCalledWith("/api/skills/skill-1");
    expect(mockAxiosClient.post).toHaveBeenCalledWith("/api/educations", expect.any(Object));
    expect(mockAxiosClient.put).toHaveBeenCalledWith(
      "/api/educations/education-1",
      expect.any(Object)
    );
    expect(mockAxiosClient.delete).toHaveBeenCalledWith("/api/educations/education-1");
    expect(mockAxiosClient.post).toHaveBeenCalledWith("/api/experiences", expect.any(Object));
    expect(mockAxiosClient.put).toHaveBeenCalledWith(
      "/api/experiences/experience-1",
      expect.any(Object)
    );
    expect(mockAxiosClient.delete).toHaveBeenCalledWith("/api/experiences/experience-1");
  });
});
