import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { throwError } from 'rxjs';
import { Observable } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Widget } from '../models/widget.model';
import { Panel } from '../models/general-panel.model';
import { environment } from '../../environments/environment';
import { OAuthService } from 'angular-oauth2-oidc';
import { SimpleLoginService } from '../auth/simple-login/simple-login.service';
import { PanelGroup } from '../models/panel_group.model';
import { WidgetGroup } from '../models/widget-group.model';

@Injectable()
export class BackendService {
  private _users: any[];
  apiURL: string;
  httpHeaders: any;

  constructor(private http: HttpClient, private oauthService: OAuthService,
    private auth: SimpleLoginService) {

    // User has logged in via oidc login
    if (this.oauthService.hasValidAccessToken()) {
      this.httpHeaders =
        new HttpHeaders({
          'Authorization': 'Bearer ' + this.oauthService.getAccessToken(),
          'Content-Type': 'application/json'
        });
      this.apiURL = environment.oidcApiURL;
    }

    // User has logged in via simple login
    if (this.auth.isAuthenticated()) {
      this.httpHeaders = new HttpHeaders({
        'Authorization': 'JWT ' + this.auth.getToken(),
        'Content-Type': 'application/json'
      });
      this.apiURL = environment.simpleLoginApiUrl;
    }
  }

  get users(): any[] {
    return this._users;
  }

  private handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      console.error(`Backend returned code ${error.status}, body was: ${error.error}`);
    }
    // return an ErrorObservable with a user-facing error message
    return throwError('Something bad happened; please try again later.');
  }

  getPanels(): Observable<Panel[]> {
    return this.http.get<Panel[]>(`${this.apiURL}panels/`, { headers: this.httpHeaders })
      .pipe(
        tap(panels => console.log('Fetched panels from server', panels)),
        catchError(this.handleError)
      );
  }

  getPanel(alias: string): Observable<Panel> {
    return this.http.get<Panel>(`${this.apiURL}panels/${alias}/`, { headers: this.httpHeaders })
      .pipe(
        tap(panel => console.log('Fetched panel from server', panel)),
        catchError(this.handleError)
      );
  }

  // , date_from, date_to, aggregation, filter
  getWidgets(panel_id): Observable<Widget[]> {
    const params = new HttpParams()
      .set('panel_id', panel_id);

    console.log(params);
    return this.http.get<Widget[]>(`${this.apiURL}widgets/`, { params: params, headers: this.httpHeaders })
      .pipe(
        tap(dataset => console.log('Fetched datasets from server', dataset)),
        catchError(this.handleError)
      );
  }

  // ------------------- this will be removed -------------------
  getWidgetsLegacy(panel_id, date_from, date_to, aggregation, filter): Observable<Widget[]> {
    const params = new HttpParams()
      .set('panel_id', panel_id)
      .set('aggregation', aggregation)
      .set('filter', filter)
      .set('date_from', date_from)
      .set('date_to', date_to);

    console.log(params);
    return this.http.get<Widget[]>(`${this.apiURL}widgets/`, { params: params, headers: this.httpHeaders })
      .pipe(
        tap(dataset => console.log('Fetched datasets from server', dataset)),
        catchError(this.handleError)
      );
  }

  getWidgetData(widget_id, date_from, date_to, aggregation, filter): Observable<Widget> {
    const params = new HttpParams()
      .set('id', widget_id)
      .set('aggregation', aggregation)
      .set('filter', filter)
      .set('date_from', date_from)
      .set('date_to', date_to);
    console.log(params);
    return this.http.get<Widget>(`${this.apiURL}widgets/`, { params: params, headers: this.httpHeaders })
      .pipe(
        tap(dataset => console.log('Fetched datasets from server', dataset)),
        catchError(this.handleError)
      );
  }

  getWidgetGroups(panel_id): Observable<WidgetGroup[]> {

    const params = new HttpParams()
      .set('panel_id', panel_id);

    console.log(params);
    return this.http.get<WidgetGroup[]>(`${this.apiURL}widget_groups/`, { params: params, headers: this.httpHeaders })
      .pipe(
        tap(dataset => console.log('Fetched datasets from server', dataset)),
        catchError(this.handleError)
      );

  }

  getPanelGroups(): Observable<PanelGroup[]> {
    return this.http.get<PanelGroup[]>(`${this.apiURL}panel_groups/`, { headers: this.httpHeaders })
      .pipe(
        tap(panel_groups => console.log('Fetched panel groups from server', panel_groups)),
        catchError(this.handleError)
      );
  }
}
