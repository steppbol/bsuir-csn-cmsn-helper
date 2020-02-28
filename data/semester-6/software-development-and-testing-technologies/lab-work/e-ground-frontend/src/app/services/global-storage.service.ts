import { Injectable } from '@angular/core';
import { fromEvent } from 'rxjs';
import {User} from '../model/User';


@Injectable({
  providedIn: 'root'
})
export class GlobalUserStorageService {
  private USER_KEY = 'currentUser';

  set currentUser(user: User) {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  get currentUser() {
    return JSON.parse(localStorage.getItem(this.USER_KEY));
  }

  getInitialState() {
    return {
      currentUserState: {
        currentUser: this.currentUser,
        isLoading: false,
        errorMessage: null
      }
    };
  }

  constructor() { }

  asObservable() {
    return fromEvent(window, 'storage');
  }

}
