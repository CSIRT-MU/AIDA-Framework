import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Injectable } from '@angular/core';

/**
 * Guard to protect routes against guest users.
 */
@Injectable()
export class AuthGuardNegative implements CanActivate {
  /**
   * Constructor with DI
   * @param auth Injected AuthService
   * @param router Injected Router for redirection
   */
  constructor(
    private router: Router,
    private authService: AuthService


  ) { }

  /**
   * Implements function telling if route can be activated or not.
   * @param route Activated route
   * @param state Router state
   */
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
      return this.authService.isAuthenticated().then(
        (authenticated: boolean) => {
          if (authenticated) {
            this.router.navigate(['/auth/profile']);
            return false;
          } else {
            return true;
          }
        }
      );
  }
}
