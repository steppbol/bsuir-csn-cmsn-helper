import {Injectable} from '@angular/core';
import {AccountService} from '../../services/account.service';
import {NgRedux} from '@angular-redux/store';
import {AppState} from '../index';
import {ActionsObservable} from 'redux-observable';
import {AnyAction} from 'redux';
import {catchError, map, switchMap} from 'rxjs/operators';
import {FETCH_USER, fetchUserFailedAction, fetchUserSuccessAction,
        UPDATE_ACCOUNT, updateAccountSuccessAction, updateAccountFailedAction} from '../actions/account.actions';
import {of} from 'rxjs';
import {NotifierService} from 'angular-notifier';


@Injectable()
export class AccountEpic {
  constructor(private accountService: AccountService,
              private ngRedux: NgRedux<AppState>,
              private notifierService: NotifierService) {
  }

  updateAccount$ = (action$: ActionsObservable<AnyAction>) => {
    return action$.ofType(UPDATE_ACCOUNT).pipe(
      switchMap(({payload}) => {
        return this.accountService
          .updateAccount(payload.user)
          .pipe(
            map(user => {
              this.notifierService.notify('success', 'Account was updated successful');
              return updateAccountSuccessAction(user);
            }),
            catchError(error => {
              this.notifierService.notify('error', 'Account update failed');
              return of(updateAccountFailedAction(error));
            })
          );
      })
    );
  };

  fetchUser$ = (action$: ActionsObservable<AnyAction>) => {
    return action$.ofType(FETCH_USER).pipe(
      switchMap(({payload}) => {
        return this.accountService
          .getUserById(payload.userId)
            .pipe(map(user => fetchUserSuccessAction(user)),
              catchError(error => {
                this.notifierService.notify('error', 'Account fetch failed');
                return of(fetchUserFailedAction(error));
              })
            );
      })
    );
  }
}
