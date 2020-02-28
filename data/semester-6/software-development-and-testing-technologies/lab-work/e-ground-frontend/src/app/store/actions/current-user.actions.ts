import {User} from '../../model/User';
import {Credential} from '../../model/Credential';

export const LOGIN_USER = 'LOGIN_USER';
export const LOGIN_USER_SUCCESS = 'LOGIN_USER_SUCCESS';
export const LOGIN_USER_FAILED = 'LOGIN_USER_FAILED';
export const LOGOUT_USER = 'LOGOUT_USER';
export const LOGOUT_USER_SUCCESS = 'LOGOUT_USER_SUCCESS';
export const LOGOUT_USER_FAILED = 'LOGOUT_USER_FAILED';
export const CLEAR_USER_ERROR_MESSAGE = '[Current user] Error message cleared';

export function loginUserAction(credential: Credential) {
  return {
    type: LOGIN_USER,
    payload: {credential}
  };
}

export function loginUserSuccessAction(user: User) {
  return {
    type: LOGIN_USER_SUCCESS,
    payload: {user}
  };
}

export function loginUserFailedAction(errorMessage: string) {
  return {
    type: LOGIN_USER_FAILED,
    payload: {errorMessage}
  };
}

export function logoutUserAction() {
  return {
    type: LOGOUT_USER
  };
}

export function clearUserErrorMessage() {
  return {
    type: CLEAR_USER_ERROR_MESSAGE
  };
}
