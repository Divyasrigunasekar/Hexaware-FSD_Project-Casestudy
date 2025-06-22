import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { LoginRequest } from '../models/login-request.model';
import { AuthResponse } from '../models/auth-response.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = 'http://localhost:9090/api/users';

  constructor(private http: HttpClient) {}

  register(user: User): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.baseUrl}/register`, user);
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, credentials);
  }

  storeToken(token: string): void {
    localStorage.setItem('token', token);

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const role = payload.role;
      const userId = payload.userId;
      const email = payload.sub;

      // Store individual items
      localStorage.setItem('role', role);
      localStorage.setItem('userId', userId.toString());

      // Store unified user object (used in booking and other modules)
      const user = { role, userId, email, token };
      localStorage.setItem('user', JSON.stringify(user));
    } catch (e) {
      console.error('Failed to decode and store role/userId from token', e);
    }
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  getUserId(): number | null {
    const id = localStorage.getItem('userId');
    return id ? parseInt(id, 10) : null;
  }

  getEmail(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub || null;
    } catch (e) {
      return null;
    }
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    localStorage.removeItem('user');
  }
}
