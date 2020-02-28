import {Injectable} from '@angular/core';
import {combineEpics} from 'redux-observable';
import {CatalogEpic} from './catalog.epic';
import {OfferEpic} from './offer.epic';
import {AccountEpic} from './account.epic';
import {ResetPasswordEpic} from './reset-password.epic';
import {CurrentUserEpic} from './current-user.epic';
import {UserEpic} from './user.epic';
import {ConversationsEpic} from './conversations.epic';

@Injectable()
export class EpicService {
  constructor(private catalogEpic: CatalogEpic,
              private offerEpic: OfferEpic,
              private currentUserEpic: CurrentUserEpic,
              private accountEpic: AccountEpic,
              private resetPasswordEpic: ResetPasswordEpic,
              private userEpic: UserEpic,
              private conversationEpic: ConversationsEpic) {
  }

  getEpics() {
    return combineEpics(
      this.catalogEpic.fetchOffers$,
      this.catalogEpic.searchOffers$,
      this.offerEpic.createOffer$,
      this.offerEpic.selectOffer$,
      this.offerEpic.fetchComments$,
      this.currentUserEpic.loginUser$,
      this.accountEpic.fetchUser$,
      this.accountEpic.updateAccount$,
      this.userEpic.createUser$,
      this.resetPasswordEpic.sendEmail$,
      this.resetPasswordEpic.resetPassword$,
      this.conversationEpic.fetchConversations$,
      this.conversationEpic.getConversation$
    );
  }
}
