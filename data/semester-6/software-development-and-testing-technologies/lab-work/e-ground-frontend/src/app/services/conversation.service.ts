import {Injectable} from '@angular/core';
import {Observable, of, throwError} from 'rxjs';
import {Conversation} from '../model/Conversation';
import {HttpClient} from '@angular/common/http';
import {Message} from '../model/Message';
import {User} from '../model/User';
import {catchError} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ConversationService {
  conversationUrl = '/api/v1/processor';

  user1: User = {
    id: '1',
    firstName: 'Kirill',
    lastName: 'Petrov',
    age: 20,
    phoneNumber: '911',
    email: 'asdsad',
    compressedImageId: null,
    password: 'q1w2e3',
    imageId: null,
    image: null
  };

  user2: User = {
    id: '2',
    firstName: 'Evgeniy',
    lastName: 'Garkavik',
    age: 20,
    phoneNumber: '912',
    email: 'gdrytury',
    compressedImageId: null,
    password: 'q1w2e3',
    image: null,
    imageId: null
  };

  messages: Message[] = [{
    conversationId: '1',
    senderId: '1',
    receiverId: '2',
    message: 'Hello',
    creationDate: new Date(2019, 4, 17)
  }, {
    conversationId: '2',
    senderId: '2',
    receiverId: '1',
    message: 'Hello',
    creationDate: new Date(2019, 4, 17)
  }];

  conversation: Conversation = {
    id: '1',
    firstUser: this.user1,
    secondUser: this.user2,
    conversationMessages: this.messages
  };

  constructor(private http: HttpClient) {
  }

  getUserConversations(id: string): Observable<Conversation[]> {
    console.log('Hello conversation');
    return this.http.get<Conversation[]>(`${this.conversationUrl}/conversations/users/${id}`)
      .pipe(catchError((error: any) => throwError(error)));
    /*return of([this.conversation]);*/
  }

  getConversationByUsersIds(yourId: string, otherId: string) {
    console.log('Hello conversation');
    return this.http.get<Conversation>(`${this.conversationUrl}/conversations`, {
      params: {id: yourId, otherId}
    }).pipe(catchError((error: any) => throwError(error)));
    /*return of(this.conversation);*/
  }
}
