import {AppState} from '..';

export const isLoading = (state: AppState) => state.userState.isLoading;
export const selectErrorMessage = (state: AppState) => state.userState.errorMessage;
