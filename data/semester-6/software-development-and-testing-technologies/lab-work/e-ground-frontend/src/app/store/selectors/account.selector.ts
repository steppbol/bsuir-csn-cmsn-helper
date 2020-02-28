import {AppState} from '../index';
import {defaultUser} from '../../model/User';

export const isLoading = (state: AppState) => state.accountPageState.isLoading;

export const selectCurrentUser = (state: AppState) => state.accountPageState.user;

export const selectAccountForEdit = (state: AppState) => {
  const user = state.accountPageState.user;
  return user ? user : defaultUser;
};
