import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth/services/auth.service';
import { Component, ViewChild, ElementRef } from '@angular/core';

import { OAuthService } from 'angular-oauth2-oidc';
import { JwksValidationHandler } from 'angular-oauth2-oidc';
import { authConfig } from './auth//auth.config';
import { Router } from '@angular/router';
import { SimpleLoginService } from './auth/simple-login/simple-login.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})

export class AppComponent {
  constructor(
              private http: HttpClient,
              private oauthService: OAuthService,
              public router: Router,
              private authService: AuthService,
              public auth: SimpleLoginService) {

    this.configureWithNewConfigApi();
  }

  private configureWithNewConfigApi() {
    // Check if OIDC provider is reachable (this should be handled by angular-oauth2-oidc library but is not)
    this.http.get(authConfig.issuer).subscribe(
      () => {
        this.oauthService.configure(authConfig);
        this.oauthService.setStorage(localStorage);
        this.oauthService.tokenValidationHandler = new JwksValidationHandler();
        this.oauthService.loadDiscoveryDocumentAndTryLogin().then(
          () => { this.router.initialNavigation(); }
        );
      },
      () => {
        console.log('OIDC Provider unreachable.');
        this.router.initialNavigation();
      }
    );
  }
}