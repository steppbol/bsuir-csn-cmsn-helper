import {Message} from './Message';
import {User} from './User';

export class Conversation {
  id: string;
  firstUser: User;
  secondUser: User;
  conversationMessages: Message[];
}
