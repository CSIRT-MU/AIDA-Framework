import { DisplayRulesComponent } from './components/widgets/display-rules/display-rules.component';

import { AuthService } from './auth/services/auth.service';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { BackendService } from './services/backend.service';

import { AppComponent } from './app.component';
import { WidgetComponent } from './components/widget/widget.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';

import { GeneralPanelComponent } from './components/panel/general-panel.component';
import { NotFoundComponent } from './components/not-found/not-found.component';


import { LoginComponent } from './auth/components/login/login.component';
import { ProfileComponent } from './auth/components/profile/profile.component';
import { LogoutComponent } from './auth/components/logout/logout.component';

import { OverviewComponent } from './components/widgets/overview/overview.component';
import { SafePipe } from './pipes/safe.pipe';

// Import routing from external file
import { AppRoutingModule } from './app-routing.module';

import { SimpleLoginModule } from './auth/simple-login/simple-login.module';
import { OAuthModule, OAuthStorage } from 'angular-oauth2-oidc';
import { AuthGuardNegative } from './auth/services/auth-guard-negative.service';
import { AuthGuard } from './auth/services/auth-guard.service';
import { NavbarComponent } from './components/navbar/navbar.component';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { DefaultFilterComponent } from './components/panel/filters/default-filter/default-filter.component';
import { DnsstatisticsFilterComponent } from './components/panel/filters/dnsstatistics-filter/dnsstatistics-filter.component';

import { MatTabsModule } from '@angular/material/tabs';
import { WidgetGroupComponent } from './components/widget-group/widget-group.component';
import { AuthSectionComponent } from './components/auth-section/auth-section.component';

import {
  MatIconModule, MatButtonModule, MatTableModule, MatPaginatorModule,
  MatSortModule, MatFormFieldModule, MatCheckboxModule, MatInputModule,
  MatCardModule, MatDatepickerModule, MatNativeDateModule, MatTooltipModule, MatSnackBar, MatSnackBarModule
} from '@angular/material';
import { SparkLogComponent } from './components/widgets/spark-log/spark-log.component';
import { NewLineComaPipe } from './pipes/new-line-coma.pipe';
import { NewLinePipe } from './pipes/new-line.pipe';
import { NewTableComponent } from './components/widgets/new-table/new-table.component';
import { ColumnSortComponent } from './components/widgets/table/column-sort/column-sort.component';
import { SanitizeHtmlPipe } from './pipes/sanitize-html.pipe';


@NgModule({
  declarations: [
    AppComponent,
    WidgetComponent,
    SidebarComponent,
    NotFoundComponent,
    GeneralPanelComponent,
    SidebarComponent,
    LoginComponent,
    ProfileComponent,
    LogoutComponent,
    NavbarComponent,
    OverviewComponent,
    DisplayRulesComponent,
    DefaultFilterComponent,
    DnsstatisticsFilterComponent,
    WidgetGroupComponent,
    AuthSectionComponent,
    SafePipe,
    SparkLogComponent,
    NewLinePipe,
    NewLineComaPipe,
    NewTableComponent,
    ColumnSortComponent,
    SanitizeHtmlPipe,
  ],
  imports: [
    SimpleLoginModule,
    BrowserModule,
    MatProgressSpinnerModule,
    BrowserAnimationsModule,
    MatTabsModule,
    FormsModule,
    HttpClientModule,
    NgbModule.forRoot(),
    AppRoutingModule,
    OAuthModule.forRoot(
      {
        resourceServer: {
          allowedUrls: ['https://oidc.ics.muni.cz/oidc/userinfo', 'http://localhost:8000/api/panels'],
          sendAccessToken: true
        }
      }
    ),
    BrowserAnimationsModule,
    BrowserModule,
    BrowserAnimationsModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSortModule,
    MatCheckboxModule,
    MatFormFieldModule,
    FormsModule,
    MatInputModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
    ReactiveFormsModule,
    MatTooltipModule,
    MatSnackBarModule,
  ],
  exports: [
    MatProgressSpinnerModule,
    MatTabsModule
  ],
  providers: [
    { provide: OAuthStorage, useValue: localStorage }, AuthGuard, AuthGuardNegative, BackendService, AuthService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
