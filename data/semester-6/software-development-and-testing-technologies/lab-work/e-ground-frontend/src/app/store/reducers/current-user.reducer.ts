import {Reducer} from 'redux';
import {CLEAR_USER_ERROR_MESSAGE, LOGIN_USER, LOGIN_USER_FAILED, LOGIN_USER_SUCCESS, LOGOUT_USER} from '../actions/current-user.actions';
import {User} from '../../model/User';

export interface CurrentUserState {
  readonly currentUser: User;
  readonly isLoading: boolean;
  readonly errorMessage: string;
}

const INITIAL_STATE = {
  currentUser: null,
  isLoading: false,
  errorMessage: null
};


export const currentUserReducer: Reducer<CurrentUserState> = (state: CurrentUserState = INITIAL_STATE, action) => {
  switch (action.type) {
    case LOGIN_USER: {
      return {...state, isLoading: true, errorMessage: null};
    }
    case LOGIN_USER_SUCCESS: {
      return {...state, currentUser: action.payload.user, isLoading: false, errorMessage: null};
    }
    case LOGIN_USER_FAILED: {
      return {...state, isLoading: false, errorMessage: action.payload.errorMessage};
    }
    case LOGOUT_USER: {
      return {...state, currentUser: null, isLoading: false, errorMessage: null};
    }
    case CLEAR_USER_ERROR_MESSAGE: {
      return {...state, errorMessage: null};
    }
    default: {
      return state;
    }
  }
};
