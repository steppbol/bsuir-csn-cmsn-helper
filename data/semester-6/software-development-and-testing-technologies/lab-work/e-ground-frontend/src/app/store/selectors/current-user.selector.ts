import { AppState } from '..';

export const selectCurrentUser = (state: AppState) => state.currentUserState.currentUser;
export const isLoading = (state: AppState) => state.currentUserState.isLoading;
export const selectErrorMessage = (state: AppState) => state.currentUserState.errorMessage;
