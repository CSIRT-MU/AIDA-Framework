import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

 /**
  * @author Jakub Kolman <442445@mail.muni.cz>
  */

@Injectable({
  providedIn: 'root'
})
export class MessengerService {

  private subject = new Subject<any>();

  sendMessage(row: any[], action: string) {
      this.subject.next([row, action]);
    //   this.subject.next();
  }

  clearMessage() {
      this.subject.next();
  }

  getMessage(): Observable<any> {
      return this.subject.asObservable();
  }
}
