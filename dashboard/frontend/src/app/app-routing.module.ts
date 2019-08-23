import { AuthSectionComponent } from './components/auth-section/auth-section.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

// Components
import { NotFoundComponent } from './components/not-found/not-found.component';
import { ProfileComponent } from './auth/components/profile/profile.component';
import { LoginComponent } from './auth/components/login/login.component';
import { GeneralPanelComponent } from './components/panel/general-panel.component';

import { AuthGuard } from './auth/services/auth-guard.service';
import { AuthGuardNegative } from './auth/services/auth-guard-negative.service';


const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [AuthGuardNegative] },
  { path: 'auth', component: AuthSectionComponent, canActivate: [AuthGuard], children: [
    { path: '', pathMatch: 'full', redirectTo: 'panel/overview'},
    { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
    { path: 'panel/:alias', component: GeneralPanelComponent, canActivate: [AuthGuard] }
  ] },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  { path: '**', component: NotFoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {initialNavigation: false})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
