import {Reducer} from 'redux';
import {Offer} from '../../model/Offer';
import {
  FETCH_OFFERS,
  FETCH_OFFERS_FAILED,
  FETCH_OFFERS_SUCCESS,
  SEARCH_OFFERS,
  SEARCH_OFFERS_FAILED,
  SEARCH_OFFERS_SUCCESS
} from '../actions/catalog.actions';


export interface CatalogState {
  readonly offers: Map<string, Offer>;
  readonly isLoading: boolean;
}

const INITIAL_STATE = {
  offers: new Map<string, Offer>(),
  isLoading: false
};

export const catalogReducer: Reducer<CatalogState> = (state: CatalogState = INITIAL_STATE, action): CatalogState => {
  switch (action.type) {
    case FETCH_OFFERS: {
      return {...state, isLoading: true};
    }
    case FETCH_OFFERS_SUCCESS: {
      return {...state, ...action.payload, isLoading: false};
    }
    case FETCH_OFFERS_FAILED: {
      return {...state, isLoading: false};
    }
    case SEARCH_OFFERS: {
      return {...state, isLoading: true};
    }
    case SEARCH_OFFERS_SUCCESS: {
      return {...state, ...action.payload, isLoading: false};
    }
    case SEARCH_OFFERS_FAILED: {
      return {...state, isLoading: false};
    }
    default: {
      return state;
    }
  }
};
