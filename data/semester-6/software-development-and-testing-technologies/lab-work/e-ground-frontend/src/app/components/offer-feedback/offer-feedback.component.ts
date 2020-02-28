import {Component, OnInit} from '@angular/core';
import {NgRedux, select} from '@angular-redux/store';
import {commentIsLoading, selectComments} from '../../store/selectors/offer.selector';
import {Observable} from 'rxjs';
import {Comment} from '../../model/Comment';
import {AppState} from '../../store';
import {ActivatedRoute} from '@angular/router';
import {fetchCommentsAction} from '../../store/actions/offer.actions';
import {selectCurrentUser} from '../../store/selectors/current-user.selector';
import {User} from '../../model/User';
import {OfferService} from '../../services/offer.service';

@Component({
  selector: 'app-offer-feedback',
  templateUrl: './offer-feedback.component.html',
  styleUrls: ['./offer-feedback.component.css']
})
export class OfferFeedbackComponent implements OnInit {

  @select(selectComments)
  comments: Observable<Comment[]>;

  @select(commentIsLoading)
  isLoading: Observable<boolean>;

  @select(selectCurrentUser)
  user: Observable<User>;

  defaultTextAreaValue = '';

  constructor(private ngRedux: NgRedux<AppState>,
              private route: ActivatedRoute,
              private offerService: OfferService) {
  }

  ngOnInit() {
    this.ngRedux.dispatch(fetchCommentsAction(this.route.snapshot.paramMap.get('id')));
  }

  sendMessage(messageBody: string) {
    messageBody = messageBody.trim();
    if (messageBody.length !== 0) {
      this.offerService.addCommentToOffer(
        {
          customerId: this.ngRedux.getState().currentUserState.currentUser.id,
          offerId: this.route.snapshot.paramMap.get('id'),
          message: messageBody
        });
      this.defaultTextAreaValue = '';
    }
  }
}
