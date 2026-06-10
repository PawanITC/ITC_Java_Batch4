<<<<<<< HEAD
export interface User {
  id: string;
  name: string;
  email: string;
  roles: string[];
  profilePicture?: string;
}
=======

export interface User {
  name: string;
  email: string;
} 
>>>>>>> c4e713a82e1baa1ee93fdb436c56d7991ad51579

export interface AuthState {
  user: User | null;
  isLoggedIn: boolean;
<<<<<<< HEAD
  accessToken: string | null;
  loading: boolean;
=======
>>>>>>> c4e713a82e1baa1ee93fdb436c56d7991ad51579
}