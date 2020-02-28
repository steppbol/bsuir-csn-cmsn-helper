import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of, throwError} from 'rxjs';
import {User} from '../model/User';
import {catchError} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  apiUrl = '/api/v1/processor';

  constructor(private http: HttpClient) {
  }

  user: User = {
    id: '',
    firstName: 'Kirill',
    lastName: 'Friend',
    age: 303,
    phoneNumber: '2144231423',
    password: 'q1w2e3',
    email: 'kirill@mail.ru',
    compressedImageId: null,
    image: null,
    imageId: null
  };

  updateAccount(user: User): Observable<User> {
    console.log(user);
    console.log('update account');
    const options = { headers: new HttpHeaders().set('Content-Type', 'application/json') };
    return this.http.put<any>(`${this.apiUrl}/customers`, user, options)
      .pipe(catchError((error: any) => throwError(error.error)));
    /*return of(this.user);*/
  }

  getUserById(id: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/customers/${id}`)
      .pipe(catchError((error: any) => throwError(error.error)));
    /*return of(this.user);*/
  }
}
