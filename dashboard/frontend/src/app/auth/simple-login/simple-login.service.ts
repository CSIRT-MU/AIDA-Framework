import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { User } from './models/user.model';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { JwtHelperService } from '@auth0/angular-jwt';

import { environment } from '../../../environments/environment';
import { LoginRequest } from './models/login-request.model';
import { LoginResponse } from './models/login-response.model';

/**
 * Service for user authentication.
 */
@Injectable()
export class SimpleLoginService {

  /**
   * Authentication token
   */
  private token: string;

  private user: User;

  /**
   * API URL for login
   */
  private loginURL: string;

  /**
   * API URL for signup
   */
  private signupURL: string;

  /**
   * User's username
   */
  private username: string;

  /**
   * Auth constructor. If there is a token stored in local storage,
   * user is automaticaly logged in.
   * @param http HttpClient
   */
  constructor(private http: HttpClient, private router: Router) {
    const token = localStorage.getItem('token');

    if (token != null) {
      this.setToken(token);
    }

    this.loginURL = environment.authUrl;
  }

  /**
   * Log user in with his credentials.
   * @param username Username to log in with
   * @param password Password to log in with
   * @returns Observable with server response
   */
  login(username: string, password: string): Observable<LoginResponse> {
    // TODO change this
    this.username = username;

    const request = new LoginRequest();
    request.username = username;
    request.password = password;
    return this.http.post<LoginResponse>(this.loginURL, request);
      // .pipe(
      //    catchError()     // TODO
      // );
  }

  register(user: User) {
    return this.http.post<LoginResponse>(this.signupURL, user);
  }

  /**
   * Set users authentication token.
   * @param token Authentication token
   */
  setToken(token: string) {
    this.token = token;
    localStorage.setItem('token', token);
  }

  /**
   * Get users authentication token.
   * @returns Authentication token
   */
  getToken(): string {
    return this.token;
  }

  /**
   * Logs user out.
   */
  logout() {
    this.token = null;
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  /**
   * Get username of currently logged in user.
   * @returns Logged user's username.
   */
  getUsername(): string {
    if (this.isAuthenticated()) {
      const helper = new JwtHelperService();
      const decodedToken = helper.decodeToken(this.token);
      return decodedToken.username;
    } else { return null; }
  }

  /**
   * Get user authentication status.
   * @returns Status of user authentication
   */
  isAuthenticated(): boolean {
    if (this.token) {
      // console.log(this.token);
      const helper = new JwtHelperService();
      // console.log("Is expired?", helper.isTokenExpired(this.token));
      // console.log('Exp date: ', helper.getTokenExpirationDate(this.token));
      return !helper.isTokenExpired(this.token);
    } else { return false; }
  }
}
