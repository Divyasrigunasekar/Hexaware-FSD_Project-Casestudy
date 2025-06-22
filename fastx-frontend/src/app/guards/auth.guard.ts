import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {} //  Inject services

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    if (!this.authService.isLoggedIn()) {
      return this.router.createUrlTree(['/login']); // Redirect to login if not logged in
    }

    const expectedRole = route.data['role'];
    const userRole = this.authService.getRole();

    if (expectedRole && userRole !== expectedRole) {
      return this.router.createUrlTree(['/unauthorized']); // Redirect if role doesn't match
    }

    return true;
  }
}
  

