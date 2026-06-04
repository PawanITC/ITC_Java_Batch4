export interface User {
  id: string;
  name: string;
  email: string;
  roles: string[];
  profilePicture?: string;
}

export interface AuthState {
  user: User | null;
  isLoggedIn: boolean;
  accessToken: string | null;
  loading: boolean;
}