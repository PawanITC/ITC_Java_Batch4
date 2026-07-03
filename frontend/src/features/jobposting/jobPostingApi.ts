import axios from "axios";
import keycloak from "../../features/auth/keycloak";
import { ApiResponse, BackendJob } from "../../pages/JobPosting";

// --- 1. Axios Client Configuration ---
const jobPostingApi = axios.create({
  baseURL:
    process.env.REACT_APP_JOBPOSTING_API_BASE_URL ?? "http://localhost:8089",
});

// --- 2. Keycloak Authentication Token Interceptor ---
jobPostingApi.interceptors.request.use(async (config) => {
  if (keycloak.authenticated) {
    try {
      await keycloak.updateToken(30);
    } catch {
      // Allow structural paths to try unauthenticated fallbacks if gateway permits
    }

    if (keycloak.token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${keycloak.token}`;
    }
  }
  return config;
});

// --- 3. Request/Response TypeScript DTO Payloads ---
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

// --- 4. API Request Methods ---

/**
 * Fetches the paginated job list content stream
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