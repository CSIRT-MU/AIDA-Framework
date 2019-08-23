// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  itemsPerPage: 10,
  oidcApiURL: 'http://localhost:8000/oidc-api/oidc-protected/',
  simpleLoginApiUrl: 'http://localhost:8000/rest-api/',
  postLogoutUrl: 'http://localhost:4200/login',
  authUrl: 'http://localhost:8000/api-token-auth/'
};
