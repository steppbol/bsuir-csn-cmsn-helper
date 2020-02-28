import {Injectable} from '@angular/core';
import {AnyAction} from 'redux';
import {ActionsObservable} from 'redux-observable';
import {of} from 'rxjs';
import {catchError, map, switchMap} from 'rxjs/operators';
import {GlobalUserStorageService} from '../../services/global-storage.service';
import {AccountService} from '../../services/account.service';
import {NotifierService} from 'angular-notifier';
import {CREATE_USER, createUserFailedAction, createUserSuccessAction} from '../actions/user.actions';
import {UserService} from '../../services/user.service';

@Injectable()
export class UserEpic {
  constructor(private userService: UserService,
              private localStorageService: GlobalUserStorageService,
              private accountService: AccountService,
              private notifierService: NotifierService) {
  }
  createUser$ = (action$: ActionsObservable<AnyAction>) => {
    return action$.ofType(CREATE_USER).pipe(
      switchMap(({payload}) => {
        return this.userService
          .register(payload.registrationData)
          .pipe(
            map(user => {
              this.notifierService.notify('success', 'Create user successful');
              return createUserSuccessAction(user);
            }),
            catchError(error => {
              this.notifierService.notify('error', 'Create user failed');
              return of(createUserFailedAction(error)); })
          );
      })
    );
  }
}
