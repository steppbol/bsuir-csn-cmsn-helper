import {CurrentUserState} from './reducers/current-user.reducer';
import {UserSideNavState} from './reducers/user-side-nav.reducer';
import {CatalogState} from './reducers/catalog.reducer';
import {OfferPageState} from './reducers/offer-page.reducer';
import {AccountPageState} from './reducers/account-page.reducer';
import {DialogState} from './reducers/dialogs.reducer';
import {ResetPasswordState} from './reducers/reset-password.reducer';
import {UserState} from './reducers/user.reducer';
import {CatalogSearchToolbarState} from './reducers/catalog-search-toolbar.reducer';
import {ConversationsState} from './reducers/conversation.reducer';

export interface AppState {
  readonly router?: string;
  readonly currentUserState?: CurrentUserState;
  readonly userSideNavState?: UserSideNavState;
  readonly catalogState?: CatalogState;
  readonly offerPageState?: OfferPageState;
  readonly catalogSearchToolbarState?: CatalogSearchToolbarState;
  readonly accountPageState?: AccountPageState;
  readonly dialogsState?: DialogState;
  readonly resetPasswordState?: ResetPasswordState;
  readonly userState?: UserState;
  readonly conversationsState?: ConversationsState;
}


