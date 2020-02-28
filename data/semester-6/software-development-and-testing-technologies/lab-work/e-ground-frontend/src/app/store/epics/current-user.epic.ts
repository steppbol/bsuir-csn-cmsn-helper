import {Injectable} from '@angular/core';
import {AnyAction} from 'redux';
import {ActionsObservable} from 'redux-observable';
import {of} from 'rxjs';
import {catchError, map, switchMap} from 'rxjs/operators';
import {
  LOGIN_USER,
  loginUserFailedAction,
  loginUserSuccessAction
} from '../actions/current-user.actions';
import {AuthenticationService} from '../../services/authentication.service';
import {GlobalUserStorageService} from '../../services/global-storage.service';
import {AccountService} from '../../services/account.service';
import {NotifierService} from 'angular-notifier';

@Injectable()
export class CurrentUserEpic {
  constructor(private authService: AuthenticationService,
              private localStorageService: GlobalUserStorageService,
              private accountService: AccountService,
              private notifierService: NotifierService) {
  }
  loginUser$ = (action$: ActionsObservable<AnyAction>) => {
    return action$.ofType(LOGIN_USER).pipe(
      switchMap(({payload}) => {
        return this.authService
          .login(payload.credential)
          .pipe(
            map(user => {
              this.localStorageService.currentUser = {...user};
              this.notifierService.notify('success', 'User login successful');
              return loginUserSuccessAction(user);
            }), catchError(error => {
              this.notifierService.notify('error', 'Login user failed');
              return of(loginUserFailedAction(error)); })
          );
      })
    );
  }
}
