import {Component, OnInit} from '@angular/core';
import {skipWhile, take} from 'rxjs/operators';
import {fetchConversationsAction} from '../../store/actions/conversation.action';
import {ActivatedRoute} from '@angular/router';
import {NgRedux, select} from '@angular-redux/store';
import {AppState} from '../../store';
import {User} from '../../model/User';
import {Observable} from 'rxjs';
import {Conversation} from '../../model/Conversation';
import {selectCurrentUser} from '../../store/selectors/current-user.selector';
import {conversationsList, isLoading} from '../../store/selectors/conversation.selector';

@Component({
  selector: 'app-conversation-list',
  templateUrl: './conversation-list.component.html',
  styleUrls: ['./conversation-list.component.css']
})
export class ConversationListComponent implements OnInit {

  @select(isLoading)
  isLoading: Observable<boolean>;

  @select(conversationsList)
  conversations: Observable<Conversation[]>;

  @select(selectCurrentUser)
  currentUser: Observable<User>;

  constructor(private route: ActivatedRoute, private ngRedux: NgRedux<AppState>) {
  }

  ngOnInit() {
    console.log('ng on init conversation');
    this.isLoading.pipe(skipWhile(result => result === true), take(1))
      .subscribe(() =>
        this.ngRedux.dispatch(fetchConversationsAction(this.ngRedux.getState().currentUserState.currentUser.id))
      );

    this.isLoading.pipe(skipWhile(result => result === true), take(1))
      .subscribe(() => this.ngRedux.select(conversationsList));
  }
}
