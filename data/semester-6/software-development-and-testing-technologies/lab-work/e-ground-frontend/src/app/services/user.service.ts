import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../model/User';
import {Observable} from 'rxjs';
import {RegistrationData} from '../model/RegistrationData';


@Injectable({providedIn: 'root'})
export class UserService {

  constructor(private http: HttpClient) {
  }

  user: User = {
    firstName: 'Kirill',
    lastName: 'Friend',
    age: 303,
    phoneNumber: '2144231423',
    id: '1',
    password: 'q1w2e3',
    email: 'kirill@mail.ru',
    compressedImageId: null,
    image: null,
    imageId: null
  };

  register(registrationData: RegistrationData): Observable<User> {
    return this.http.post<User>(`/api/v1/processor/customers`, registrationData);
    // return of(this.user);
  }
}
