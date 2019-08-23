import { environment } from '../../../environments/environment';
import { OAuthService } from 'angular-oauth2-oidc';
import { SimpleLoginService } from '../simple-login/simple-login.service';
import { Injectable } from '@angular/core';
import { toUnicode } from 'punycode';
/*
    This service should handle authenticated user's credentials, logout
    It manages OIDC and Simple Login - based on user's choice
*/

@Injectable()

export class AuthService {
    private userProfileLoaded = false;

    constructor(private simpleLoginService: SimpleLoginService,
                private oAuthService: OAuthService) {}


    public logout() {
        if (this.oAuthService.hasValidAccessToken()) {
            // User is logged using OIDC service
            this.oAuthService.postLogoutRedirectUri = environment.postLogoutUrl;
            this.oAuthService.logOut();

        } else if (this.simpleLoginService.isAuthenticated()) {
            // User is logged using Simple login service
            this.simpleLoginService.logout();
        }
        return null;
    }

    public getUserName(): string {
        // User is logged using OIDC service
        if (this.oAuthService.hasValidAccessToken()) {
              if (!this.userProfileLoaded) {
                this.oAuthService.loadUserProfile();
              }

              const claims = this.oAuthService.getIdentityClaims();
              this.userProfileLoaded = true;
              return claims['name'];

        // User is logged using Simple login service
        } else if (this.simpleLoginService.isAuthenticated()) {
            return this.simpleLoginService.getUsername();
        }
    }

    public getUserProfile(): any {
        // User is logged using OIDC service
        if (this.oAuthService.hasValidAccessToken()) {
            const claims = this.oAuthService.getIdentityClaims();
            claims['local'] = false;
            if (!claims) {
                return null;
            }
            // console.log(claims);
            return claims;

        // User is logged using Simple login service
        } else if (this.simpleLoginService.isAuthenticated()) {
            const claims = [];
            claims['name'] = this.simpleLoginService.getUsername();
            claims['local'] = true;
            return claims;
        }
    }

    /**
    * Checks if user is authenticated by local authentication or OIDC authentication
    * @returns returns true if user is authenticated, false otherwise
    */
    public isAuthenticated(): Promise<boolean> {
        return new Promise((resolve, reject) => {
          // If user has logged via simple login immediately return true
          if (this.simpleLoginService.isAuthenticated()) {
            resolve(true);
          }
          // Check if user has logged via OIDC authentication
          if (this.oAuthService.hasValidAccessToken()) {
              this.oAuthService.loadUserProfile().then((response) => {
                if (('groupNames' in this.oAuthService.getIdentityClaims())
                    // Now this condition is fixed to "csirt-mu" group, it should by dynamically loaded in the future
                    && this.oAuthService.getIdentityClaims()['groupNames'].indexOf('csirt-mu') > -1) {
                  resolve(true);
                } else {
                  resolve(false);
                }
              });
          } else {
            resolve(false);
          }
      });
    }
}

