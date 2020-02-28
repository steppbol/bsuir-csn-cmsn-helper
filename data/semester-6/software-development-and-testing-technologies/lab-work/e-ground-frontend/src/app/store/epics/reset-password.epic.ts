import {NgRedux} from '@angular-redux/store';
import {Injectable} from '@angular/core';
import {AppState} from '../index';
import {ActionsObservable} from 'redux-observable';
import {catchError, map, switchMap} from 'rxjs/operators';
import {ResetPasswordService} from '../../services/reset-password.service';
import {
  SAVE_PASSWORD, savePasswordFailedAction,
  savePasswordSuccessAction,
  SEND_EMAIL,
  sendResetPasswordEmailSuccessAction
} from '../actions/reset-password.actions';
import {AnyAction} from 'redux';
import {GlobalUserStorageService} from '../../services/global-storage.service';
// import {ChatServerService} from '../../services/chat-server.service';
import {of} from 'rxjs/index';
import {NotifierService} from 'angular-notifier';
import {loginUserSuccessAction} from '../actions/current-user.actions';

@Injectable()
export class ResetPasswordEpic {
  constructor(private resetPasswordService: ResetPasswordService,
              private ngRedux: NgRedux<AppState>,
              private localStorageService: GlobalUserStorageService,
              // private chatService: ChatServerService,
              private notifierService: NotifierService) {
  }

  sendEmail$ = (action$: ActionsObservable<AnyAction>) => {
    return action$.ofType(SEND_EMAIL).pipe(
      switchMap(({payload}) => {
        console.log(payload.email);
        return this.resetPasswordService
          .sendEmail(payload.email)
          .pipe(
            map(value => sendResetPasswordEmailSuccessAction(value))
          );
      })
    );
  }

  resetPassword$ = (action$: ActionsObservable<AnyAction>) => {
    return action$.ofType(SAVE_PASSWORD).pipe(
      switchMap(({payload}) => {
        console.log('payload' + payload.resetPassword.id);
        return this.resetPasswordService
          .updatePassword(payload.resetPassword)
          .pipe(
            map(user => {
                this.localStorageService.currentUser = {...user};
                // this.chatService.connect(user.token.accessToken, user.account.id);
                return loginUserSuccessAction(user);
                savePasswordSuccessAction();
              }
            ),
            catchError(error => {
              this.notifierService.notify('error', 'Reset password failed');
              return of(savePasswordFailedAction(error));
            })
          );
      })
    );
  }
}
