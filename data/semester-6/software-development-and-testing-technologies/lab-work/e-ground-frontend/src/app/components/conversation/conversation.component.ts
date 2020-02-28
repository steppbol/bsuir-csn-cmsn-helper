import {Component, OnInit} from '@angular/core';
import {skipWhile, take} from 'rxjs/operators';
import {ActivatedRoute} from '@angular/router';
import {NgRedux, select} from '@angular-redux/store';
import {AppState} from '../../store';
import {Observable} from 'rxjs';
import {isLoading, selectCurrentConversation} from '../../store/selectors/conversation.selector';
import {Conversation} from '../../model/Conversation';
import {getConversationAction} from '../../store/actions/conversation.action';
import {ChatServerService} from '../../services/chat-server.service';

@Component({
  selector: 'app-conversation',
  templateUrl: './conversation.component.html',
  styleUrls: ['./conversation.component.css']
})
export class ConversationComponent implements OnInit {
  @select(isLoading)
  isLoading: Observable<boolean>;

  @select(selectCurrentConversation)
  currentConversation: Observable<Conversation>;

  defaultTextAreaValue = '';

  constructor(private route: ActivatedRoute, private ngRedux: NgRedux<AppState>, private chatService: ChatServerService) {
  }

  ngOnInit() {
    this.isLoading.pipe(skipWhile(result => result === true), take(1))
      .subscribe(() => this.ngRedux.dispatch(getConversationAction(
        this.ngRedux.getState().currentUserState.currentUser.id,
        this.route.snapshot.paramMap.get('id')))
      );
  }

  sendMessage(messageBody: string) {
    messageBody = messageBody.trim();
    if (messageBody.length !== 0) {
      this.chatService.sendMessage({
        conversationId: this.ngRedux.getState().conversationsState.currentConversation,
        senderId: this.ngRedux.getState().conversationsState.conversations.get(
          this.ngRedux.getState().conversationsState.currentConversation).firstUser.id,
        receiverId: this.ngRedux.getState().conversationsState.conversations.get(
          this.ngRedux.getState().conversationsState.currentConversation).secondUser.id,
        message: messageBody,
        creationDate: new Date()
      });
      this.defaultTextAreaValue = '';
    }
  }

  onKey(event: KeyboardEvent) {
    if (event.ctrlKey && event.key === 'Enter') {
      this.defaultTextAreaValue += '\n';
      return;
    }
    if (event.key === 'Enter') {
      this.sendMessage(this.defaultTextAreaValue);
    }
  }
}
