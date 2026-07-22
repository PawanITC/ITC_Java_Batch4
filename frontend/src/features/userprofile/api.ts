import axios from "axios";
import keycloak from "../../features/auth/keycloak";
import { apiBaseUrl } from "../../config/runtimeConfig";

const userProfileApi = axios.create({
  baseURL: apiBaseUrl,
});

userProfileApi.interceptors.request.use(async (config) => {
  
  if (keycloak.authenticated) {
    try {
      await keycloak.updateToken(30);
    } catch {
      // Keep the existing token if refresh fails; protected routes will reject it if expired.
    }

    if (keycloak.token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${keycloak.token}`;
    }
    
  }

  return config;
});

export type Profile = {
  id: string;
  keycloakUserId?: string;
  firstName: string;
  lastName: string;
  email: string;
  headline?: string;
  about?: string;
  gender?: string;
  city?: string;
  country?: string;
  profilePictureUrl?: string;
  coverPhotoUrl?: string;
  industry?: string;
  currentCompany?: string;
  currentPosition?: string;
  website?: string;
  githubUrl?: string;
  linkedinUrl?: string;
  openToWork?: boolean;
  profilePublic?: boolean;
  educations?: Education[];
  experiences?: Experience[];
  skills?: Skill[];
  languages?: Language[];
};

export type Skill = {
  id: string;
  skillName: string;
  endorsementCount: number;
};

export type Education = {
  id: string;
  schoolName: string;
  degree: string;
  fieldOfStudy: string;
  startYear: number;
  endYear: number;
};

export type Experience = {
  id: string;
  companyName: string;
  title: string;
  description?: string;
  startDate: string;
  endDate?: string | null;
  current: boolean;
};

export type Language = {
  id: string;
  languageName: string;
  proficiency: string;
};

export type CreateProfilePayload = {
  firstName: string;
  lastName: string;
  email: string;
  headline?: string;
  about?: string;
  gender: string;
  city?: string;
  country?: string;
  openToWork?: boolean;
  profilePublic?: boolean;
};

export type UpdateProfilePayload = Partial<CreateProfilePayload> & {
  profilePictureUrl?: string;
  coverPhotoUrl?: string;
  industry?: string;
  currentCompany?: string;
  currentPosition?: string;
  website?: string;
  githubUrl?: string;
  linkedinUrl?: string;
};

export type SkillPayload = {
  profileId: string;
  skillName: string;
  endorsementCount: number;
};

export type EducationPayload = {
  profileId: string;
  schoolName: string;
  degree: string;
  fieldOfStudy: string;
  startYear: number;
  endYear: number;
};

export type ExperiencePayload = {
  profileId: string;
  companyName: string;
  title: string;
  description: string;
  startDate: string;
  endDate?: string | null;
  current: boolean;
};


export type FollowPayload = {
  followerId: string;
  followingId: string;
};

export type FollowResponse = {
  id?: string;
  followerId: string;
  followingId: string;
  createdAt?: string;
};

export type FollowCountResponse = {
  count: number;
};

export async function listProfiles() {
  const response = await userProfileApi.get<Profile[]>("/api/profiles");
  return response.data;
}

export async function getProfile(profileId: string) {
  const response = await userProfileApi.get<Profile>(`/api/profiles/${profileId}`);
  return response.data;
}

export async function getCurrentProfile() {
  const response = await userProfileApi.get<Profile>("/api/profiles/me");
  return response.data;
}

export async function createProfile(payload: CreateProfilePayload) {
  const response = await userProfileApi.post<Profile>("/api/profiles", payload);
  return response.data;
}

export async function updateProfile(profileId: string, payload: UpdateProfilePayload) {
  const response = await userProfileApi.put<Profile>(`/api/profiles/${profileId}`, payload);
  return response.data;
}

export async function updateCurrentProfile(payload: UpdateProfilePayload) {
  const response = await userProfileApi.put<Profile>("/api/profiles/me", payload);
  return response.data;
}

export async function createSkill(payload: SkillPayload) {
  const response = await userProfileApi.post<Skill>("/api/skills", payload);
  return response.data;
}

export async function updateSkill(skillId: string, payload: SkillPayload) {
  const response = await userProfileApi.put<Skill>(`/api/skills/${skillId}`, payload);
  return response.data;
}

export async function deleteSkill(skillId: string) {
  await userProfileApi.delete(`/api/skills/${skillId}`);
}

export async function createEducation(payload: EducationPayload) {
  const response = await userProfileApi.post<Education>("/api/educations", payload);
  return response.data;
}

export async function updateEducation(educationId: string, payload: EducationPayload) {
  const response = await userProfileApi.put<Education>(
    `/api/educations/${educationId}`,
    payload
  );
  return response.data;
}

export async function deleteEducation(educationId: string) {
  await userProfileApi.delete(`/api/educations/${educationId}`);
}

export async function createExperience(payload: ExperiencePayload) {
  const response = await userProfileApi.post<Experience>("/api/experiences", payload);
  return response.data;
}

export async function updateExperience(experienceId: string, payload: ExperiencePayload) {
  const response = await userProfileApi.put<Experience>(
    `/api/experiences/${experienceId}`,
    payload
  );
  return response.data;
}

export async function deleteExperience(experienceId: string) {
  await userProfileApi.delete(`/api/experiences/${experienceId}`);
}

export async function followUser(payload: FollowPayload) {
  const response = await userProfileApi.post<any>("/api/follows/follow", payload);
  return response.data;
}

export async function unfollowUser(payload: FollowPayload) {
  const response = await userProfileApi.post<any>("/api/follows/unfollow", payload);
  return response.data;
}

export async function toggleFollowUser(payload: FollowPayload, isFollowing: boolean) {
  return isFollowing ? unfollowUser(payload) : followUser(payload);
}

/**
 * Get profiles of users who are following the specified profile.
 * GET /api/follows/{profileId}/followers
 */
export async function getFollowers(profileId: string) {
  const response = await userProfileApi.get<Profile[] | { content?: Profile[] }>(
    `/api/follows/${profileId}/followers`
  );
  return Array.isArray(response.data) ? response.data : response.data.content ?? [];
}

/**
 * Get profiles of users that the specified profile is currently following.
 * GET /api/follows/{profileId}/following
 */
export async function getFollowing(profileId: string) {
  const response = await userProfileApi.get<Profile[] | { content?: Profile[] }>(
    `/api/follows/${profileId}/following`
  );
  return Array.isArray(response.data) ? response.data : response.data.content ?? [];
}

/**
 * Get total count of profiles that the specified profile is following.
 * GET /api/follows/{profileId}/following/count
 */
export async function getFollowingCount(profileId: string) {
  // If your API returns a raw number instead of an object, change type to: userProfileApi.get<number>
  const response = await userProfileApi.get<FollowCountResponse | number>(
    `/api/follows/${profileId}/following/count`
  );
  return typeof response.data === "number" ? response.data : response.data.count;
}

/**
 * Get total count of followers for the specified profile.
 * GET /api/follows/{profileId}/followers/count
 */
export async function getFollowersCount(profileId: string) {
  // If your API returns a raw number instead of an object, change type to: userProfileApi.get<number>
  const response = await userProfileApi.get<FollowCountResponse | number>(
    `/api/follows/${profileId}/followers/count`
  );
  return typeof response.data === "number" ? response.data : response.data.count;
}
