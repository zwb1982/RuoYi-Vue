// src/services/api.ts
import axios from 'axios';
import { useUserStore } from '../store/userStore'; // To access token directly if needed, or rely on localStorage

const apiClient = axios.create({
  baseURL: '/api',
  // timeout: 10000,
});

apiClient.interceptors.request.use(config => {
  // const userStore = useUserStore(); // Pinia store might not be available globally here this simply.
  // It's generally safer to read directly from localStorage in an interceptor,
  // or ensure Pinia is initialized before this interceptor runs if you need reactive state from store.
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// Optional: Add response interceptor for global error handling (e.g. 401 unauthenticated)
// apiClient.interceptors.response.use(
//   response => response,
//   error => {
//     if (error.response && error.response.status === 401) {
//       const userStore = useUserStore(); // This would need Pinia to be setup to be accessible
//       userStore.logout();
//       // window.location.href = '/login'; // Force redirect
//        // Or use router if available: router.push('/login');
//     }
//     return Promise.reject(error);
//   }
// );

export default apiClient;
