import {Reducer} from 'redux';
import {CLEAR_USER_ERROR_MESSAGE} from '../actions/current-user.actions';
import {CREATE_USER, CREATE_USER_FAILED, CREATE_USER_SUCCESS} from '../actions/user.actions';

export interface UserState {
  readonly isLoading: boolean;
  readonly errorMessage: string;
}

const INITIAL_STATE = {
  isLoading: false,
  errorMessage: null
};


export const userReducer: Reducer<UserState> = (state: UserState = INITIAL_STATE, action) => {
  switch (action.type) {
    case CREATE_USER: {
      return {...state, isLoading: true, errorMessage: null};
    }
    case CREATE_USER_SUCCESS: {
      return {...state, isLoading: false, errorMessage: null};
    }
    case CREATE_USER_FAILED: {
      return {...state, isLoading: false, errorMessage: action.payload.errorMessage};
    }
    case CLEAR_USER_ERROR_MESSAGE: {
      return {...state, errorMessage: null};
    }
    default: {
      return state;
    }
  }
};
