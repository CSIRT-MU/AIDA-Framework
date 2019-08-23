import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';

import { SimpleLoginService } from './simple-login.service';
import { Router } from '@angular/router';

/**
 * Component with login form and submit callback.
 */
@Component({
  selector: 'app-simple-login',
  templateUrl: './simple-login.component.html',
})
export class SimpleLoginComponent {

  /**
   * LoginComponent Constructor with DI
   * @param auth Injected AuthService
   * @param router Injected Router
   */
  constructor(
    private auth: SimpleLoginService,
    private router: Router
  ) { }

  /**
   * Login error message to be displayed to user.
   */
  message: String = '';

  /**
   * Function called on form submit.
   * @param form Submitted form
   */
  submit(form: NgForm) {
    const values = form.value;

    this.auth.login(values.username, values.password)
      .subscribe(
        data => {
          const token = data.token;
          this.auth.setToken(token);
          this.router.navigate(['/auth']);
          // window.location.href = '/auth';
        },
        err => {
          this.message = err.error.non_field_errors[0];
        });
    return false;
  }
}
