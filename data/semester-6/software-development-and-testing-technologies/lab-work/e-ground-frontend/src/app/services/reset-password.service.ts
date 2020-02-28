import { Injectable } from '@angular/core';
import {Observable, throwError} from 'rxjs/index';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {ResetPassword} from '../model/ResetPassword';
import {catchError} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ResetPasswordService {

  userUrl = '/api/user/';

  constructor(private http: HttpClient) { }

  sendEmail(email: string): Observable<any> {
    console.log(email + 'send');
    return this.http.post<any>(this.userUrl + 'resetPassword',
      null, {
        params: new HttpParams().set('email', email)
      }).pipe(catchError((error: any) => throwError(error.error)));
  }

  updatePassword(resetPassword: ResetPassword): Observable<any> {
    console.log('service' + resetPassword.id + resetPassword.token + resetPassword.password);
    return this.http.post<any>(this.userUrl + 'savePassword',
      {
        id: resetPassword.id,
        token: resetPassword.token,
        password: resetPassword.password
      }).pipe(catchError((error: any) => throwError(error.error)));
  }

}
