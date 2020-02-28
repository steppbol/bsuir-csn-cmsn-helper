import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable, throwError} from 'rxjs';
import {Credential} from '../model/Credential';
import {NgxPermissionsService} from 'ngx-permissions';
import {User} from '../model/User';
import {catchError} from 'rxjs/operators';


@Injectable({providedIn: 'root'})
export class AuthenticationService {
  private currentUserSubject: BehaviorSubject<User>;
  public currentUser: Observable<User>;

  apiUrl = '/api/v1/processor';

  constructor(private http: HttpClient,
              private  permissionsServive: NgxPermissionsService) {
    this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('currentUser')));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  user: User = {
    id: '1',
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

  login(credential: Credential): Observable<User> {
    return this.http.post<any>(`${this.apiUrl}/authorization`, {email: credential.email, password: credential.password})
      .pipe(catchError((error) => throwError(error)));

    /*return of(this.user);*/
  }
}
