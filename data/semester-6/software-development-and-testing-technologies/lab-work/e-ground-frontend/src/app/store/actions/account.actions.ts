import {User} from '../../model/User';

export const FETCH_USER = 'FETCH_USER';
export const FETCH_USER_SUCCESS = 'FETCH_USER_SUCCESS';
export const FETCH_USER_FAILED = 'FETCH_USER_FAILED';
export const UPDATE_ACCOUNT = 'UPDATE_ACCOUNT';
export const UPDATE_ACCOUNT_SUCCESS = 'UPDATE_ACCOUNT_SUCCESS';
export const UPDATE_ACCOUNT_FAILED = 'UPDATE_ACCOUNT_FAILED';

export function fetchUserAction(userId: string) {
  return {
    type: FETCH_USER,
    payload: {userId}
  };
}

export function fetchUserSuccessAction(user: User) {
  return {
    type: FETCH_USER_SUCCESS,
    payload: {user}
  };
}

export function fetchUserFailedAction(message: string) {
  return {
    type: FETCH_USER_FAILED,
    error: true,
    payload: {message}
  };
}

export function updateAccountAction(user: User) {
  return {
    type: UPDATE_ACCOUNT,
    payload: {user}
  };
}

export function updateAccountSuccessAction(user: User) {
  return {
    type: UPDATE_ACCOUNT_SUCCESS,
    payload: {user}
  };
}

export function updateAccountFailedAction(message: string) {
  return {
    type: UPDATE_ACCOUNT_FAILED,
    error: true,
    payload: {message}
  };
}



