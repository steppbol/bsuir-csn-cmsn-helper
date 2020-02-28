import {Reducer} from 'redux';
import {HIDE_USER_SIDE_NAV, SHOW_USER_SIDE_NAV} from '../actions/user-side-nav.actions';


export interface UserSideNavState {
  readonly isOpened: boolean;
}


const INITIAL_STATE = {
  isOpened: false
};


export const userSideNavReducer: Reducer<UserSideNavState> = (state: UserSideNavState = INITIAL_STATE, action): UserSideNavState => {
    switch (action.type) {
      case SHOW_USER_SIDE_NAV: {
        return {...state, isOpened: true};
      }
      case HIDE_USER_SIDE_NAV: {
        return {...state, isOpened: false};
      }
      default: {
        return state;
      }
    }
  }
;
