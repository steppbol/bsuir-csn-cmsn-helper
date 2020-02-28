import {combineReducers, Reducer} from 'redux';
import {currentUserReducer} from './current-user.reducer';
import {userSideNavReducer} from './user-side-nav.reducer';
import {catalogReducer} from './catalog.reducer';
import {routerReducer} from '@angular-redux/router';
import {offerPageReducer} from './offer-page.reducer';
import {accountPageReducer} from './account-page.reducer';
import {resetPasswordReducer} from './reset-password.reducer';
import {dialogStateReducer} from './dialogs.reducer';
import {userReducer} from './user.reducer';
import {catalogSearchToolbarReducer} from './catalog-search-toolbar.reducer';
import {conversationReducer} from './conversation.reducer';

export const reducers: Reducer = combineReducers({
  currentUserState: currentUserReducer,
  userSideNavState: userSideNavReducer,
  catalogState: catalogReducer,
  router: routerReducer,
  offerPageState: offerPageReducer,
  catalogSearchToolbarState: catalogSearchToolbarReducer,
  accountPageState: accountPageReducer,
  resetPasswordState: resetPasswordReducer,
  dialogsState: dialogStateReducer,
  userState: userReducer,
  conversationsState: conversationReducer
});
