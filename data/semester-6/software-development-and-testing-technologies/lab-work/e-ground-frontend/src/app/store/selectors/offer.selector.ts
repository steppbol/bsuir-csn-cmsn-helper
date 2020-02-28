import {AppState} from '../index';

export const isSelected = (state: AppState) => state.offerPageState.isSelected;

export const selectOffer = (state: AppState) => state.offerPageState.offer;

export const selectComments = (state: AppState) => state.offerPageState.comments;

export const commentIsLoading = (state: AppState) => state.offerPageState.commentsIsLoading;
