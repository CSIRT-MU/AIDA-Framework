import { RouterModule } from '@angular/router';
import { SimpleLoginService } from './simple-login.service';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SimpleLoginComponent } from './simple-login.component';

@NgModule({
  declarations: [
    SimpleLoginComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
  ],
  providers: [
    SimpleLoginService,
  ],
  exports: [
      SimpleLoginComponent
  ]
})
export class SimpleLoginModule {}
