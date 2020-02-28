import {AppState} from '../index';

export const selectOffers = (state: AppState) => Array.from(state.catalogState.offers.values());

export const isLoading = (state: AppState) => state.catalogState.isLoading;
