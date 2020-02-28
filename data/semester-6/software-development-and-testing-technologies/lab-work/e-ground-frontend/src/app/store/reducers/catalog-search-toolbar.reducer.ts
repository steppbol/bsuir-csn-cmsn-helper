import {Reducer} from 'redux';
import {UPDATE_CATALOG_SEARCH_PARAMS} from '../actions/catalog-search-toolbar.actions';

export interface CatalogSearchToolbarState {
  readonly catalogSearchParams: string;
}

const INITIAL_STATE = {
  catalogSearchParams: ''
};

export const catalogSearchToolbarReducer:
  Reducer<CatalogSearchToolbarState> = (state: CatalogSearchToolbarState = INITIAL_STATE, action): CatalogSearchToolbarState => {
  switch (action.type) {
    case UPDATE_CATALOG_SEARCH_PARAMS:
      return {...state, catalogSearchParams: action.payload.firstName};
    default:
      return state;
  }
};
