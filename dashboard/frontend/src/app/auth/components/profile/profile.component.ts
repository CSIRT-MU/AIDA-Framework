import { AuthService } from './../../services/auth.service';
import { SimpleLoginService } from './../../simple-login/simple-login.service';
import { Component, OnInit } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';

/**
 * Displays user profile with details and settings.
 */
@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  constructor(
    private auth: AuthService
  ) {

  }
  /**
   * Function to be called on component initialisation.
   */
  ngOnInit() { }

  get userProfile() {
    return this.auth.getUserProfile();
  }
}
