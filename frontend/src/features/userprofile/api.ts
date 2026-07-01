import axios from "axios";
import keycloak from "../../features/auth/keycloak";
import { apiBaseUrl } from "../../config/runtimeConfig";

const userProfileApi = axios.create({
  baseURL: process.env.REACT_APP_USERPROFILE_API_BASE_URL ?? apiBaseUrl,
});

userProfileApi.interceptors.request.use(async (config) => {
  if (keycloak.authenticated) {
    try {
      await keycloak.updateToken(30);
    } catch {
      // The userprofile service currently allows these routes without auth.
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
  keycloakUserId?: string;
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

export type UpdateProfilePayload = Partial<Omit<CreateProfilePayload, "keycloakUserId">> & {
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

export async function listProfiles() {
  const response = await userProfileApi.get<Profile[]>("/api/profiles");
  return response.data;
}

export async function getProfile(profileId: string) {
  const response = await userProfileApi.get<Profile>(`/api/profiles/${profileId}`);
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
