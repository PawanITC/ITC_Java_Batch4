import axios from "axios";
import keycloak from "../../features/auth/keycloak";
import { apiBaseUrl } from "../../config/runtimeConfig";
import { ApiResponse, BackendJob } from "../../pages/JobPosting";

const jobPostingApi = axios.create({
  baseURL: apiBaseUrl,
});

jobPostingApi.interceptors.request.use(async (config) => {
  if (keycloak.authenticated) {
    try {
      // Refresh the token if it is about to expire within 30 seconds
      await keycloak.updateToken(30);
    } catch (error) {
      console.warn("Could not refresh Keycloak token; attempting request with existing or empty token state.", error);
    }

    if (keycloak.token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${keycloak.token}`;
    }
  }
  return config;
});

export type CreateJobPayload = {
  companyId: string;
  title: string;
  description: string;
  location: string;
  salaryMin: number;
  salaryMax: number;
  status: "OPEN" | "CLOSED" | "DRAFT";
  requirements: Array<{
    requirement: string;
    isMandatory: boolean;
  }>;
  benefits: string[];
};

export type UpdateJobPayload = Partial<CreateJobPayload>;


/**
 * Fetches the paginated job list content stream from the gateway
 */
export async function fetchJobsList() {
  const response = await jobPostingApi.get<ApiResponse>("/api/v1/jobs");
  return response.data;
}

/**
 * Fetches a single job profile by its distinct UUID
 */
export async function getJobById(jobId: string) {
  const response = await jobPostingApi.get<BackendJob>(`/api/v1/jobs/${jobId}`);
  return response.data;
}

/**
 * Creates a brand new job opening in the centralized database
 */
export async function postNewJob(payload: CreateJobPayload) {
  const response = await jobPostingApi.post<BackendJob>("/api/v1/jobs", payload);
  return response.data;
}

/**
 * Updates an existing job record configuration layout
 */
export async function updateJobPost(jobId: string, payload: UpdateJobPayload) {
  const response = await jobPostingApi.put<BackendJob>(`/api/v1/jobs/${jobId}`, payload);
  return response.data;
}

/**
 * Permanently drops a job post from the index
 */
export async function deleteJobPost(jobId: string) {
  await jobPostingApi.delete(`/api/v1/jobs/${jobId}`);
}