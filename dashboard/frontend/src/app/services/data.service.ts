import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { throwError, Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

 /**
  * @author Jakub Kolman <442445@mail.muni.cz>
  */


@Injectable({
  providedIn: 'root'
})
export class DataService {

  url = 'http://sabu-testbed.cesnet.cz:7777';

  constructor(private http: HttpClient) { }

  get(endpoint) {
    return this.http.get(this.url + endpoint).pipe(map(response => response));
  }

  post(endpoint, resource) {
    return this.http.post(this.url + endpoint, resource).pipe(
      catchError(this.handleError));
  }

  private handleError(error: Response | any) {
    const errorMessage: string  = error.message ? error.message : error.toString();
    return Observable.throw(errorMessage);
  }

}
