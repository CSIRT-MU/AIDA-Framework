import { AuthService } from './../../services/auth.service';
import { Component, OnInit } from '@angular/core';

import { Router } from '@angular/router';

import { OAuthService } from 'angular-oauth2-oidc';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

  constructor(private router: Router,
              private auth: AuthService) { }

  ngOnInit() {
  }

  logout() {
    this.auth.logout();
  }

}
