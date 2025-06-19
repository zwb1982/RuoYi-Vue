// src/store/userStore.ts
import { defineStore } from 'pinia';
import { login as apiLogin, logout as apiLogout } from '../services/authService';

interface UserState {
  token: string | null;
  username: string | null;
  // roles: string[];
  // permissions: string[];
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: localStorage.getItem('authToken') || null,
    username: localStorage.getItem('username') || null,
    // roles: [],
    // permissions: [],
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
  },
  actions: {
    async login(credentials: {username?: string, password?: string}) {
      try {
        const response = await apiLogin(credentials); // Calls the authService
        if (response.token) {
          this.token = response.token;
          this.username = credentials.username || 'Unknown User'; // Store username
          localStorage.setItem('authToken', response.token);
          localStorage.setItem('username', this.username);
          // TODO: Fetch user info/permissions if needed from another endpoint using the token
          return true;
        }
        return false;
      } catch (error) {
        this.logout(); // Clear any partial state on error
        throw error; // Re-throw for the component to handle
      }
    },
    logout() {
      apiLogout(); // Call API logout (currently simulated)
      this.token = null;
      this.username = null;
      localStorage.removeItem('authToken');
      localStorage.removeItem('username');
      // this.roles = [];
      // this.permissions = [];
      // Potentially redirect to login page, handled in router or component
    },
    // Action to load user details after login or on app load if token exists
    // async fetchUserDetails() {
    //   if (!this.token) return;
    //   try {
    //     // const userInfo = await apiClient.get('/getInfo'); // Standard RuoYi getInfo endpoint
    //     // this.username = userInfo.data.user.userName;
    //     // this.roles = userInfo.data.roles;
    //     // this.permissions = userInfo.data.permissions;
    //   } catch (error) {
    //     console.error("Failed to fetch user details", error);
    //     this.logout(); // Token might be invalid
    //   }
    // }
  },
});
