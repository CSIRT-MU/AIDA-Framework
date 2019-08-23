import {Component, OnInit} from '@angular/core';
import { NgForm } from '@angular/forms';

import { OAuthService } from 'angular-oauth2-oidc';
import { Router } from '@angular/router';

/**
 * Component with login form and submit callback.
 */
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  /**
   * LoginComponent Constructor with DI
   * @param auth Injected AuthService
   * @param router Injected Router
   */
  constructor(
    private router: Router,
    private oauthService: OAuthService
  ) { }

  ngOnInit() {
    // if (this.oauthService.hasValidAccessToken()) {
    //   this.router.navigate(['auth/profile']);
    // }
  }

  public oidcLogin() {
    this.oauthService.initImplicitFlow();
  }
}
