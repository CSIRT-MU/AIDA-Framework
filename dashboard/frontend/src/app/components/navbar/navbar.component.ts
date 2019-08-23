import { AuthService } from '../../auth/services/auth.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})

export class NavbarComponent implements OnInit {
  userProfileLoaded = false;

  constructor(private authService: AuthService) { }

  ngOnInit() {  }

  get userName(): string {
    return this.authService.getUserName();
  }
}
