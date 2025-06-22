import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { AuthResponse } from '../models/auth-response.model';
import { LoginRequest } from '../models/login-request.model';


@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = 'http://localhost:9090/api/users';
  private loginUrl = `${this.baseUrl}/login`;
  private registerUrl = `${this.baseUrl}/register`;

  constructor(private http: HttpClient) {}

  // ========== REGISTER ==========
  register(user: User): Observable<any> {
    return this.http.post(this.registerUrl, user);
  }

  // ========== LOGIN ==========
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(this.loginUrl, credentials);
  }

  // ========== GET CURRENT USER INFO ==========
  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/me`);
  }

  // ========== GET ALL USERS (Admin only) ==========
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.baseUrl);
  }

  // ========== GET USER BY ID (Admin or Self) ==========
  getUserById(userId: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/${userId}`);
  }

  // ========== UPDATE USER (Admin or Self) ==========
  updateUser(userId: number, user: User): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/${userId}`, user);
  }

  // ========== DELETE USER (Admin or Self) ==========
  deleteUser(userId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${userId}`);
  }
//===========Reset Password===================
  resetPassword(email: string, newPassword: string): Observable<any> {
  return this.http.post(`${this.baseUrl}/reset-password`, { email, newPassword });
}



  // ========== HELPER FUNCTIONS ==========
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  getUserId(): number {
    return Number(localStorage.getItem('userId'));
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  isUser(): boolean {
    return this.getRole() === 'USER';
  }

  isOperator(): boolean {
    return this.getRole() === 'OPERATOR';
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  logout(): void {
    localStorage.clear();
  }
}
