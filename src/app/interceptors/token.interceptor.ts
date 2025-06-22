import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();

    // Define endpoints where token should NOT be attached
    const publicUrls = [
      '/api/users/login',
      '/api/users/register',
      '/api/users/forgot-password',
      '/api/users/reset-password'
      
    ];
    const isPublicUrl = publicUrls.some(url => req.url.includes(url));

    // Attach token only if it's not a public URL and token exists
    if (token && !isPublicUrl) {
      req = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
    }

    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 403) {
          alert('Access Denied: You are not authorized to perform this action.');
          this.authService.logout();
          this.router.navigate(['/login']);
        } else if (error.status === 401) {
          alert('Session expired. Please login again.');
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }
}
