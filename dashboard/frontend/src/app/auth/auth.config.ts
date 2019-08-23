import { AuthConfig } from 'angular-oauth2-oidc';

export const authConfig: AuthConfig = {

  // Url of the Identity Provider
  issuer: 'https://oidc.ics.muni.cz/oidc/',

  // URL of the SPA to redirect the user after silent refresh
  silentRefreshRedirectUri: window.location.origin + '/silent-refresh.html',

  // URL of the SPA to redirect the user to after login
  redirectUri: window.location.origin + '/index.html',

  // The SPA's id. The SPA is registerd with this id at the auth-server
  clientId: 'f31500cd-f33c-4f38-88b5-5b0a840a960e',

  // set the scope for the permissions the client should request
  // The first three are defined by OIDC. The 4th is a usecase-specific one
  scope: 'openid profile email address phone groupNames',

  sessionChecksEnabled: false,

  userinfoEndpoint: 'https://oidc.ics.muni.cz/oidc/userinfo'
};
