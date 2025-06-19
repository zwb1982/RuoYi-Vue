// src/services/authService.ts
import apiClient from './api';
import type { AxiosResponse, AxiosError } from 'axios'; // Import AxiosError

interface LoginCredentials {
  username?: string;
  password?: string;
}

interface LoginResponse {
  token: string; // Assuming backend returns an object with a token
  // Add other properties if backend returns more user info or permissions
  msg?: string; // RuoYi often returns msg
  code?: number; // RuoYi often returns code (200 for success)
}

// Helper to check if the error is an AxiosError
function isAxiosError(error: any): error is AxiosError {
  return error.isAxiosError === true;
}

export const login = async (credentials: LoginCredentials): Promise<LoginResponse> => {
  try {
    // RuoYi's default login endpoint is /login and expects username, password, code, uuid
    // For simplicity, assuming the backend /login for this custom UI might just need username/password
    // Or, if it's the standard RuoYi admin login, it needs captcha.
    // The design doc mentions "认证授权：完全复用RuoYi的Spring Security体系"
    // This implies we might need to handle captcha if calling the main RuoYi login.
    // For now, let's assume a simplified login or that captcha is handled elsewhere/disabled for this API route.
    // We will POST to `/api/login` which is proxied.
    const response: AxiosResponse<LoginResponse> = await apiClient.post('/login', {
        username: credentials.username,
        password: credentials.password
        // code: "captcha_code_if_needed", // Captcha code
        // uuid: "captcha_uuid_if_needed"  // Captcha UUID
    });
    // RuoYi success response often has { code: 200, msg: "操作成功", token: "..." }
    // Or { code: 200, msg: "登录成功", token: "..." }
    if (response.data && response.data.token) {
        return response.data;
    } else {
        // Handle cases where token is missing but request might seem successful
        throw new Error(response.data.msg || 'Login failed: No token received');
    }
  } catch (error: any) {
    if (isAxiosError(error) && error.response) {
      throw new Error((error.response.data as LoginResponse).msg || 'Login API error');
    }
    throw new Error(error.message || 'An unknown error occurred during login.');
  }
};

export const logout = async () => {
    // Standard RuoYi logout is POST /logout
    // try {
    //     await apiClient.post('/logout');
    // } catch (error) {
    //     console.error("Error during logout API call:", error);
    //     // Still proceed with client-side logout
    // }
    console.log("Logout function called (simulated API call)");
};
