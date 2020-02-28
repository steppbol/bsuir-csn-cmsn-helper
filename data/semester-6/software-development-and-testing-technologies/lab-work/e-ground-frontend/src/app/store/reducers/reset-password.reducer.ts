import {Reducer} from 'redux';
import {ResetPassword} from '../../model/ResetPassword';
import {SAVE_PASSWORD, SAVE_PASSWORD_SUCCESS} from '../actions/reset-password.actions';

export interface ResetPasswordState {
  readonly payload: ResetPassword;
  readonly isLoading: boolean;
}

const INITIAL_STATE = {
  payload: null,
  isLoading: false
};
export const resetPasswordReducer: Reducer<ResetPasswordState> =
  (state: ResetPasswordState = INITIAL_STATE, action): ResetPasswordState => {
  switch (action.type) {
    case SAVE_PASSWORD: {
      return {...state, isLoading: true};
    }
    case SAVE_PASSWORD_SUCCESS: {
      return {...state, isLoading: false};
    }
    default: {
      return state;
    }
  }
};
